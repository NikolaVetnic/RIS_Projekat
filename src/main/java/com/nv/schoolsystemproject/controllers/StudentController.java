package com.nv.schoolsystemproject.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(path = "/api/v1/project/student")
public class StudentController {
	
	
	@SuppressWarnings("unused")
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	
	// =-=-=-= GET =-=-=-=
	
	@RequestMapping(path = "/home", method = RequestMethod.GET) 
	public ModelAndView getHome(HttpServletRequest request) {
		
		return new ModelAndView("/student/home");
	}
}
