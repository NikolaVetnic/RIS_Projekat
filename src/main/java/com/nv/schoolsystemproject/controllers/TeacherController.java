package com.nv.schoolsystemproject.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;
import com.nv.schoolsystemproject.utils.UserCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/project/teacher")
public class TeacherController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private StudentRepository studentRepository;

	@Autowired private UserServiceImpl userServiceImpl;
	
	@Autowired UserCustomValidator userValidator;
	
	
	// =-=-=-= GET =-=-=-=
	
	
	@RequestMapping(path = "/home", method = RequestMethod.GET) 
	public ModelAndView getHome(HttpServletRequest request) {
		
		return new ModelAndView("/teacher/home");
	}
	
	
	@RequestMapping(path = "/users", method = RequestMethod.GET) 
	public ModelAndView getAllUsers(HttpServletRequest request) {
		
		String username = ((UserEntity) request.getSession().getAttribute("user")).getUsername();
		
		String path = "/teacher/users";
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.TEACHER))
			path = "/teacher/error";
		
		request.setAttribute("users", studentRepository.findByTeacher(username));
		
		logger.info(username + " : viewed own students.");
		
		return new ModelAndView(path);
	}
}
