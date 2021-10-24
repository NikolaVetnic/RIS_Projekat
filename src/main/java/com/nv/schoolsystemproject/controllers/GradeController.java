package com.nv.schoolsystemproject.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.GradeEntity;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.GradeRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;

@RestController
@RequestMapping(path = "/api/v1/project/grade")
public class GradeController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private GradeCardRepository gradeCardRepository;
	@Autowired private GradeRepository gradeRepository;
	
	@Autowired private UserServiceImpl userServiceImpl;
	
	
	// =-=-=-= PUT =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{id}/{grade}")
	public ResponseEntity<?> update(@PathVariable Integer id, @PathVariable Integer grade) {
		
		// if grade is invalid return
		if (!(0 < grade && grade <= 5))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.BAD_REQUEST.value(), "Invalid grade."), HttpStatus.BAD_REQUEST);
		
		Optional<GradeEntity> gradeOpt = gradeRepository.findById(id);
		
		if (!gradeOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.NOT_FOUND.value(), "Grade not found."), HttpStatus.NOT_FOUND);
		
		GradeEntity gradeEntity = gradeOpt.get();
		
		boolean isAdmin = userServiceImpl.isAuthorizedAs(EUserRole.ADMIN);
		boolean isTeacher = userServiceImpl.getLoggedInUsername().isPresent() && 
							userServiceImpl.getLoggedInUsername().get().equals(gradeEntity.getGradeCard().getLecture().getTeacher().getUsername());
		
		if (!isAdmin && !isTeacher)
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		gradeEntity.setGrade(grade);
		
		gradeRepository.save(gradeEntity);
		
		logger.info("Grade #" + gradeEntity.getId() + " : updated.");
		
		return new ResponseEntity<>(gradeEntity, HttpStatus.OK);
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.DELETE, value ="/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		try {
			
			Optional<GradeEntity> gradeOpt = gradeRepository.findById(id);
			
			if (!gradeOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Grade not found."), HttpStatus.NOT_FOUND);
			
			GradeEntity grade = gradeOpt.get();
			
			GradeCardEntity gc = grade.getGradeCard();
			gc.getGrades().remove(grade);
			gradeCardRepository.save(gc);
			
			gradeRepository.delete(grade);
			
			logger.info("Grade #" + grade.getId() + " : deleted.");
			
			return new ResponseEntity<GradeEntity>(grade, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
