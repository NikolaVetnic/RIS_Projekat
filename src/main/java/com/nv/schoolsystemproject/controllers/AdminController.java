package com.nv.schoolsystemproject.controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.controllers.dto.AdminRegisterDTO;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.AdminEntity;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.AdminRepository;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;
import com.nv.schoolsystemproject.utils.UserCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/project/admin")
public class AdminController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private AdminRepository adminRepository;
	@Autowired private UserRepository userRepository;

	@Autowired private UserServiceImpl userServiceImpl;
	
	@Autowired UserCustomValidator userValidator;
	
	
	// =-=-=-= GET =-=-=-=
	
	@RequestMapping(path = "/home", method = RequestMethod.GET) 
	public ModelAndView getHome(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/home");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/users", method = RequestMethod.GET) 
	public ModelAndView getAllUsers(HttpServletRequest request) {
		
//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		String path = "/admin/users";
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			path = "/admin/error";
		
		List<UserEntity> users = StreamSupport
				  .stream(userRepository.findAll().spliterator(), false)
				  .collect(Collectors.toList());
		
		request.setAttribute("users", users);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed all users.");

		// povratna vrednost verzije kada je radjen samo back
		// return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
		
		ModelAndView mav = new ModelAndView(path);
		
		return mav;
	}
	
	
	@RequestMapping(path = "/logs", method = RequestMethod.GET)
	public ModelAndView getLogs(HttpServletRequest request) throws IOException {
		
//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		String path = "/admin/logs";
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			path = "/admin/error";
		
		BufferedReader in = new BufferedReader(
				new FileReader("logs//spring-boot-logging.log"));
		
		List<String> lines = new ArrayList<String>();		
		String line = null;

		while ((line = in.readLine()) != null)
			lines.add(line);
		
		in.close();
		
		request.setAttribute("logs", lines);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed logs.");
		
		// povratna vrednost verzije kada je radjen samo back
		// return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
		
		ModelAndView mav = new ModelAndView(path);
		
		return mav;
	}
	
	
	// =-=-=-= PUT =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody AdminRegisterDTO adminDTO, BindingResult result) {

		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			userValidator.validate(adminDTO, result);
		
		Optional<AdminEntity> adminOpt = adminRepository.findById(id);
		
		if (!adminOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found."), HttpStatus.NOT_FOUND);
		
		AdminEntity admin = adminOpt.get();
		
		admin.setUsername(adminDTO.getUsername());
		admin.setPassword(adminDTO.getPassword());
		
		adminRepository.save(admin);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated admin " + admin.getUsername());
		
		return new ResponseEntity<>(admin, HttpStatus.OK);
	}
	

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.DELETE, value ="/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		try {
		
			AdminEntity admin = adminRepository.findById(id).orElse(null);
			
			adminRepository.delete(admin);
			
			if (admin == null)
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Admin not found."), HttpStatus.NOT_FOUND);
			
			logger.info(userServiceImpl.getLoggedInUsername() + " : deleted admin " + admin.getUsername());
			
			return new ResponseEntity<AdminEntity>(admin, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
