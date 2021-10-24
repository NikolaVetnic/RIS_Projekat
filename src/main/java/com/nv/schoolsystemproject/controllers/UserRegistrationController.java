package com.nv.schoolsystemproject.controllers;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nv.schoolsystemproject.controllers.dto.AdminRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.ParentRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.StudentRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.TeacherRegisterDTO;
import com.nv.schoolsystemproject.controllers.factory.UserFactory;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.AdminEntity;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.TeacherEntity;
import com.nv.schoolsystemproject.repositories.AdminRepository;
import com.nv.schoolsystemproject.repositories.ParentRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.repositories.TeacherRepository;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;
import com.nv.schoolsystemproject.utils.UserCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/project/registration")
public class UserRegistrationController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired AdminRepository adminRepository;
	@Autowired ParentRepository parentRepository;
	@Autowired StudentRepository studentRepository;
	@Autowired TeacherRepository teacherRepository;
	@Autowired UserRepository userRepository;
	
	@Autowired private UserServiceImpl userServiceImpl;
	
	@Autowired UserCustomValidator userValidator;

	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(userValidator);
	}
	

	@RequestMapping(method = RequestMethod.POST, value = "/admins")
	public ResponseEntity<?> addNewAdmin(@Valid @RequestBody AdminRegisterDTO adminDTO, BindingResult result) {

		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			userValidator.validate(adminDTO, result);
		
		AdminEntity admin = (AdminEntity) UserFactory.createUser(adminDTO);
		userRepository.save(admin);
		
		logger.info(admin.toString() + " : created.");
		
		return new ResponseEntity<>(admin, HttpStatus.OK);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/teachers")
	public ResponseEntity<?> addNewTeacher(@Valid @RequestBody TeacherRegisterDTO teacherDTO, BindingResult result) {

		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);

		if (teacherRepository.findByEmail(teacherDTO.getEmail()).isPresent())
			return new ResponseEntity<>("Email must be unique.", HttpStatus.BAD_REQUEST);
		else if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			userValidator.validate(teacherDTO, result);
		
		TeacherEntity teacher = (TeacherEntity) UserFactory.createUser(teacherDTO);
		userRepository.save(teacher);
		
		logger.info(teacher.toString() + " : created.");
		
		return new ResponseEntity<>(teacher, HttpStatus.OK);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/parents")
	public ResponseEntity<?> addNewParent(@Valid @RequestBody ParentRegisterDTO parentDTO, BindingResult result) {

		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);

		if (parentRepository.findByEmail(parentDTO.getEmail()).isPresent())
			return new ResponseEntity<>("Email must be unique.", HttpStatus.BAD_REQUEST);
		else if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			userValidator.validate(parentDTO, result);
		
		ParentEntity parent = (ParentEntity) UserFactory.createUser(parentDTO);
		userRepository.save(parent);
		
		logger.info(parent.toString() + " : created.");
		
		return new ResponseEntity<>(parent, HttpStatus.OK);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/students")
	public ResponseEntity<?> addNewStudent(@Valid @RequestBody StudentRegisterDTO studentDTO, BindingResult result) {

		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);

		if (studentRepository.findByJmbg(studentDTO.getJmbg()).isPresent())
			return new ResponseEntity<>("Personal ID number must be unique.", HttpStatus.BAD_REQUEST);
		else if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			userValidator.validate(studentDTO, result);
		
		StudentEntity student = (StudentEntity) UserFactory.createUser(studentDTO);
		userRepository.save(student);
		
		logger.info(student.toString() + " : created.");
		
		return new ResponseEntity<>(student, HttpStatus.OK);
	}
	

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
}
