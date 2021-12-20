package com.nv.schoolsystemproject.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.controllers.dto.UserLoginDTO;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.utils.Encryption;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping(path = "/api/v1/project/login")
public class UserLoginController {
	
	/*
	 * Dve nove metode: 
	 * 1) login – medota koju ce klijenti koristiti da posalju svoje k-
	 *    redencijale i kao odgovor dobijati UserLoginDTO u kome se na-
	 *    lazi njihovo korisnicko ime i token koje ce korisnici korist-
	 *    iti,
	 * 2) getJWTToken – metoda za kreiranje JWT token-a koji ce biti v-
	 *    racen u login metodi
	 */
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	

	@Autowired private UserRepository userRepository;
	
	@Value("${spring.security.secret-key}")
	private String secretKey;
	
	@Value("${spring.security.token-duration}")
	private Integer duration;

    @Resource(name="authenticationManager")
    private AuthenticationManager authManager;
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView login(@RequestParam String username, @RequestParam String pwd, 
			HttpServletRequest request) throws IOException, ServletException {
		
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		
		String path = "/index";
		
		for (UserEntity userEntity : users) {
			if (userEntity.getUsername().equals(username) && Encryption.validatePassword(pwd, userEntity.getPassword())) {

				String token = getJWTToken(userEntity);
				
				UserLoginDTO userLoginDTO = new UserLoginDTO();
				userLoginDTO.setUsername(username);
				userLoginDTO.setToken(token);
				
				logger.info(userLoginDTO.getUsername() + " : logged in.");
				
				// povratna vrednost verzije kada je radjen samo back
				// return new ResponseEntity<>(userLoginDTO, HttpStatus.OK);
				
				request.getSession().setAttribute("userLoginDTO", userLoginDTO);
				request.setAttribute("userLoginMsg", "Ulogovani ste kao : " + username);
				
				// postaviti ulogovanog korisnika na nivo sesije, ulogu cu koristiti da postavim GrantedAuthority-e
				EUserRole userRole = userRepository.findByUsername(username).get().getRole();
				
				// dodavanje autoriteta korisniku koga manuelno postavljam preko SecurityContext-a
				Set<GrantedAuthority> authorities = new HashSet<>();
				authorities.add(new SimpleGrantedAuthority(userRole.toString().toUpperCase()));
				
				// radim autentikaciju preko prosledjenih parametara
				UsernamePasswordAuthenticationToken authReq = 
						new UsernamePasswordAuthenticationToken(username, 
								new BCryptPasswordEncoder().encode("password"), authorities);
				
				// postavljanje autentikacije
				SecurityContext sc = SecurityContextHolder.getContext();
				sc.setAuthentication(authReq);
				
				// stavljanje autentikacije na nivo sesije
				HttpSession session = request.getSession(true);
				session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
				
				// sanity check
				System.out.printf("username : %s | role : %s | granted authority: %s \n", 
						SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
						userRole,
						SecurityContextHolder.getContext().getAuthentication().getAuthorities());				
				
				
				// ZNAM da se ovako ne radi ali nazalost ne znam bolji nacin
				ModelAndView mav = new ModelAndView("/" + userRole.toString().toLowerCase() + "/home");
				
				return mav;
			}
		}
		
		// povratna vrednost verzije kada je radjen samo back
		// return new ResponseEntity<>("Username and password do not match", HttpStatus.UNAUTHORIZED);
		
		request.setAttribute("userLoginMsg", "Korisnicko ime i lozinka moraju biti ispravni.");
		
		ModelAndView mav = new ModelAndView(path);
		
		return mav;
	}
	
	
	private String getJWTToken(UserEntity user) {
		
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole().name());
		
		String token = Jwts.builder().setId("softtekJWT").setSubject(user.getUsername())
				.claim("authorities",
						grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();
		
		logger.info(user.toString() + " : JWTToken granted.");
		
		return "Bearer " + token;
	}
}
