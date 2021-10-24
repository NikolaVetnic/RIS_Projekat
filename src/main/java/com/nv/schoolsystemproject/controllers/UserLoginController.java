package com.nv.schoolsystemproject.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nv.schoolsystemproject.controllers.dto.UserLoginDTO;
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
	

	@Autowired
	private UserRepository userRepository;
	
	@Value("${spring.security.secret-key}")
	private String secretKey;
	
	@Value("${spring.security.token-duration}")
	private Integer duration;
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String pwd) {
		
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		
		for (UserEntity userEntity : users) {
			if (userEntity.getUsername().equals(username) && Encryption.validatePassword(pwd, userEntity.getPassword())) {
				
				String token = getJWTToken(userEntity);
				
				UserLoginDTO userLoginDTO = new UserLoginDTO();
				userLoginDTO.setUsername(username);
				userLoginDTO.setToken(token);
				
				logger.info(userLoginDTO.getUsername() + " : logged in.");
				
				return new ResponseEntity<>(userLoginDTO, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<>("Username and password do not match", HttpStatus.UNAUTHORIZED);
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
