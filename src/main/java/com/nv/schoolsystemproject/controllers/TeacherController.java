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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nv.schoolsystemproject.controllers.dto.TeacherRegisterDTO;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.SubjectEntity;
import com.nv.schoolsystemproject.entities.TeacherEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.SubjectRepository;
import com.nv.schoolsystemproject.repositories.TeacherRepository;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;
import com.nv.schoolsystemproject.utils.UserCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/project/teacher")
public class TeacherController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private GradeCardRepository gradeCardRepository;
	@Autowired private LectureRepository lectureRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private TeacherRepository teacherRepository;
	@Autowired private UserRepository userRepository;

	@Autowired private UserServiceImpl userServiceImpl;
	
	@Autowired UserCustomValidator userValidator;
	
	
	// =-=-=-= GET =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.GET) 
	public ResponseEntity<?> getTeacher() {
		
		Optional<String> loggedInUsernameOpt = userServiceImpl.getLoggedInUsername();
		
		if (!loggedInUsernameOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error."), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		
		UserEntity user = userRepository.findByUsername(loggedInUsernameOpt.get()).get();
		
		if (user.getRole() != EUserRole.TEACHER)
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.BAD_REQUEST.value(), "User is not a teacher."), 
					HttpStatus.BAD_REQUEST);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed own profile.");
		
		return new ResponseEntity<>(userRepository.findByUsername(loggedInUsernameOpt.get()).get(), HttpStatus.OK);
	}
	
	
	@RequestMapping(path = "/grades", method = RequestMethod.GET) 
	public ResponseEntity<?> getGradeCards() {
		
		TeacherEntity user = (TeacherEntity) getTeacher().getBody();
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed own students' grades.");
		
		return new ResponseEntity<>(user.getLectures().stream()
				.flatMap(l -> l.getGradeCards().stream()).collect(Collectors.toList()), HttpStatus.OK);
	}
	
	
	// =-=-=-= PUT =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody TeacherRegisterDTO teacherDTO, BindingResult result) {

		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			userValidator.validate(teacherDTO, result);
		
		Optional<TeacherEntity> teacherOpt = teacherRepository.findById(id);
		
		if (!teacherOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found."), HttpStatus.NOT_FOUND);
		
		TeacherEntity teacher = teacherOpt.get();
		
		teacher.setEmail(teacherDTO.getEmail());
		teacher.setFirstName(teacherDTO.getFirstName());
		teacher.setLastName(teacherDTO.getLastName());
		teacher.setPassword(teacherDTO.getPassword());
		teacher.setUsername(teacherDTO.getUsername());
		
		teacherRepository.save(teacher);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated teacher " + teacher.getUsername());
		
		return new ResponseEntity<>(teacher, HttpStatus.OK);
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
			
			Optional<TeacherEntity> teacherOpt = teacherRepository.findById(id);
			
			if (!teacherOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found."), HttpStatus.NOT_FOUND);
			
			TeacherEntity teacher = teacherOpt.get();
			
			for (LectureEntity l : teacher.getLectures()) {
				
				SubjectEntity currSubject = l.getSubject();
				currSubject.getLectures().remove(l);
				subjectRepository.save(currSubject);
				
				Set<GradeCardEntity> currGC = l.getGradeCards();
				
				Iterator<GradeCardEntity> it = currGC.iterator();
				
				while(it.hasNext()){
					
					GradeCardEntity gc = it.next();
					
					gc.setLecture(null);
					gradeCardRepository.save(gc);
				}
				
				lectureRepository.delete(l);
			}
			
			teacherRepository.delete(teacher);
			
			logger.info(userServiceImpl.getLoggedInUsername() + " : deleted teacher " + teacher.getUsername());
			
			return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
