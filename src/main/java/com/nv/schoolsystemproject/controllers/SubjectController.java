package com.nv.schoolsystemproject.controllers;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nv.schoolsystemproject.controllers.dto.SubjectRegisterDTO;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.SubjectEntity;
import com.nv.schoolsystemproject.entities.TeacherEntity;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.SubjectRepository;
import com.nv.schoolsystemproject.repositories.TeacherRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;
import com.nv.schoolsystemproject.utils.SubjectCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/project/subjects")
public class SubjectController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private GradeCardRepository gradeCardRepository;
	@Autowired private LectureRepository lectureRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private TeacherRepository teacherRepository;
	
	@Autowired private UserServiceImpl userServiceImpl;
	
	@Autowired SubjectCustomValidator subjectValidator;

	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(subjectValidator);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public ResponseEntity<?> addNewSubject(@Valid @RequestBody SubjectRegisterDTO subjectDTO, BindingResult result) {
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			subjectValidator.validate(subjectDTO, result);
		
		SubjectEntity subject = new SubjectEntity();
		
		subject.setName(subjectDTO.getName());
		subject.setTotalHours(subjectDTO.getTotalHours());
		subject.setYearAccredited(subjectDTO.getYearAccredited());
		
		subjectRepository.save(subject);
		
		logger.info(subject.getName() + " : created.");
		
		return new ResponseEntity<>(subject, HttpStatus.OK);
	}
	

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
	
	
	// =-=-=-= PUT =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody SubjectRegisterDTO subjectDTO, BindingResult result) {
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			subjectValidator.validate(subjectDTO, result);
		
		Optional<SubjectEntity> subjectOpt = subjectRepository.findById(id);
		
		if (!subjectOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found."), HttpStatus.NOT_FOUND);
		
		SubjectEntity subject = subjectOpt.get();
		
		subject.setName(subjectDTO.getName());
		subject.setTotalHours(subjectDTO.getTotalHours());
		subject.setYearAccredited(subjectDTO.getYearAccredited());
		
		subjectRepository.save(subject);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated subject " + subject.getName());
		
		return new ResponseEntity<>(subject, HttpStatus.OK);
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.DELETE, value ="/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		try {
		
			Optional<SubjectEntity> subjectOpt = subjectRepository.findById(id);
			
			if (!subjectOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found."), HttpStatus.NOT_FOUND);
			
			SubjectEntity subject = subjectOpt.get();
			
			for (LectureEntity l : subject.getLectures()) {
				
				TeacherEntity currTeacher = l.getTeacher();
				currTeacher.getLectures().remove(l);
				teacherRepository.save(currTeacher);
				
				Set<GradeCardEntity> currGC = l.getGradeCards();
				
				Iterator<GradeCardEntity> it = currGC.iterator();
				
				while(it.hasNext()){
					
					GradeCardEntity gc = it.next();
					
					gc.setLecture(null);
					gradeCardRepository.save(gc);
				}
				
				lectureRepository.delete(l);
			}
			
			subjectRepository.delete(subject);
			
			logger.info(userServiceImpl.getLoggedInUsername() + " : deleted subject " + subject.getName());
			
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
