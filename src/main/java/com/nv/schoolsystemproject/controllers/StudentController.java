package com.nv.schoolsystemproject.controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
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

import com.nv.schoolsystemproject.controllers.dto.StudentRegisterDTO;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.GradeEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.GradeRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.ParentRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;
import com.nv.schoolsystemproject.utils.UserCustomValidator;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RestController
@RequestMapping(path = "/api/v1/project/student")
public class StudentController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());


	@Autowired private GradeCardRepository gradeCardRepository;
	@Autowired private GradeRepository gradeRepository;
	@Autowired private LectureRepository lectureRepository;
	@Autowired private ParentRepository parentRepository;
	@Autowired private StudentRepository studentRepository;
	@Autowired private UserRepository userRepository;
	
	@Autowired private UserServiceImpl userServiceImpl;

	@Autowired UserCustomValidator userValidator;
	
	
	// =-=-=-= GET =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.GET) 
	public ResponseEntity<?> getStudent() {
		
		Optional<String> loggedInUsernameOpt = userServiceImpl.getLoggedInUsername();
		
		if (!loggedInUsernameOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error."), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		
		UserEntity user = userRepository.findByUsername(loggedInUsernameOpt.get()).get();
		
		if (user.getRole() != EUserRole.STUDENT)
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.BAD_REQUEST.value(), "User is not a student."), 
					HttpStatus.BAD_REQUEST);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed own profile.");
		
		return new ResponseEntity<>(userRepository.findByUsername(loggedInUsernameOpt.get()).get(), HttpStatus.OK);
	}
	
	
	@RequestMapping(path = "/grades", method = RequestMethod.GET) 
	public ResponseEntity<?> getGradeCards() {
		
		StudentEntity user = (StudentEntity) getStudent().getBody();
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed own grades.");
		
		return new ResponseEntity<>(user.getGradeCards(), HttpStatus.OK);
	}
	
	
	@RequestMapping(path = "/grades/{subjectId}", method = RequestMethod.GET) 
	public ResponseEntity<?> getGradeCardsForSubject(@PathVariable Integer subjectId) {
		
		StudentEntity user = (StudentEntity) getStudent().getBody();
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed own grades.");
		
		return new ResponseEntity<>(user.getGradeCards().stream()
				.filter(g -> g.getLecture().getSubject().getId() == subjectId)
				.collect(Collectors.toList()), HttpStatus.OK);
	}
	
	
	// =-=-=-= PUT =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody StudentRegisterDTO studentDTO, BindingResult result) {

		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			userValidator.validate(studentDTO, result);
		
		Optional<StudentEntity> studentOpt = studentRepository.findById(id);
		
		if (!studentOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found."), HttpStatus.NOT_FOUND);
		
		StudentEntity student = studentOpt.get();
		
		student.setJmbg(studentDTO.getJmbg());
		student.setFirstName(studentDTO.getFirstName());
		student.setLastName(studentDTO.getLastName());
		student.setPassword(studentDTO.getPassword());
		student.setUsername(studentDTO.getUsername());
		
		studentRepository.save(student);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated student " + student.getUsername());
		
		return new ResponseEntity<>(student, HttpStatus.OK);
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
			
			Optional<StudentEntity> studentOpt = studentRepository.findById(id);
			
			if (!studentOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found."), HttpStatus.NOT_FOUND);
			
			StudentEntity student = studentOpt.get();
			
			for (ParentEntity p : student.getParents()) {
				
				p.getStudents().remove(student);
				parentRepository.save(p);
			}
			
			for (GradeCardEntity gc : student.getGradeCards()) {
				
				LectureEntity currLecture = gc.getLecture();
				currLecture.getGradeCards().remove(gc);
				lectureRepository.save(currLecture);
				
				for (GradeEntity g : gc.getGrades())
					gradeRepository.delete(g);
				
				gradeCardRepository.delete(gc);
			}
			
			studentRepository.delete(student);
			
			logger.info(userServiceImpl.getLoggedInUsername() + " : deleted student " + student.getUsername());
			
			return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
