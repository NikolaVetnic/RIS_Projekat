package com.nv.schoolsystemproject.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.StudentRepository;

@RestController
@RequestMapping(path = "/api/v1/project/parent")
public class ParentController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private StudentRepository studentRepository;
	
	
	// =-=-=-= GET =-=-=-=
	
	@RequestMapping(path = "/home", method = RequestMethod.GET) 
	public ModelAndView getHome(HttpServletRequest request) {
		
		return new ModelAndView("/parent/home");
	}
	
	
	@RequestMapping(path = "/users", method = RequestMethod.GET) 
	public ModelAndView getAllUsers(HttpServletRequest request) {
		
		String username = ((UserEntity) request.getSession().getAttribute("user")).getUsername();
		request.setAttribute("users", studentRepository.findByParent(username));
		
		logger.info(username + " : viewed own children.");
		
		return new ModelAndView("/parent/users");
	}
}
