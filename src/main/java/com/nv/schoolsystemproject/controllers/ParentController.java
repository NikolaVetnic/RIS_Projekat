package com.nv.schoolsystemproject.controllers;

import java.util.Optional;
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

import com.nv.schoolsystemproject.controllers.dto.AbsenceDTO;
import com.nv.schoolsystemproject.controllers.dto.ParentRegisterDTO;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.AbsenceEntity;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.AbsenceRepository;
import com.nv.schoolsystemproject.repositories.ParentRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;
import com.nv.schoolsystemproject.utils.UserCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/project/parents")
public class ParentController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private AbsenceRepository absenceRepository;
	@Autowired private ParentRepository parentRepository;
	@Autowired private StudentRepository studentRepository;
	@Autowired private UserRepository userRepository;
	
	@Autowired private UserServiceImpl userServiceImpl;
	
	@Autowired private UserCustomValidator userValidator;
	
	
	// =-=-=-= GET =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.GET) 
	public ResponseEntity<?> getParent() {
		
		Optional<String> loggedInUsernameOpt = userServiceImpl.getLoggedInUsername();
		
		if (!loggedInUsernameOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error."), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		
		UserEntity user = userRepository.findByUsername(loggedInUsernameOpt.get()).get();
		
		if (user.getRole() != EUserRole.PARENT)
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.BAD_REQUEST.value(), "User is not a parent."), 
					HttpStatus.BAD_REQUEST);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed own profile.");
		
		return new ResponseEntity<>(userRepository.findByUsername(loggedInUsernameOpt.get()).get(), HttpStatus.OK);
	}
	
	
	@RequestMapping(path = "/grades", method = RequestMethod.GET) 
	public ResponseEntity<?> getGradeCards() {
		
		ParentEntity user = (ParentEntity) getParent().getBody();
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed children's grades.");
		
		return new ResponseEntity<>(user.getStudents().stream()
				.map(StudentEntity::getGradeCards).collect(Collectors.toList()), HttpStatus.OK);
	}
	
	
	@RequestMapping(path = "/absences", method = RequestMethod.GET) 
	public ResponseEntity<?> getAbsences() {
		
		ParentEntity user = (ParentEntity) getParent().getBody();
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed children's grades.");
		
		return new ResponseEntity<>(user.getStudents().stream()
				.map(s -> s.getGradeCards().stream().map(gc -> gc.getAbsences())).collect(Collectors.toList()), HttpStatus.OK);
	}
	
	
	@RequestMapping(path = "/grades/{subjectId}", method = RequestMethod.GET) 
	public ResponseEntity<?> getGradeCardsForSubject(@PathVariable Integer subjectId) {
		
		ParentEntity user = (ParentEntity) getParent().getBody();
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed children's grades.");
		
		return new ResponseEntity<>(user.getStudents().stream()
				.flatMap(s -> s.getGradeCards().stream())
				.filter(g -> g.getLecture().getSubject().getId() == subjectId)
				.collect(Collectors.toList()), HttpStatus.OK);
	}
	
	
	// =-=-=-= PUT =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody ParentRegisterDTO parentDTO, BindingResult result) {

		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		if (result.hasErrors())
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		else
			userValidator.validate(parentDTO, result);
		
		Optional<ParentEntity> parentOpt = parentRepository.findById(id);
		
		if (!parentOpt.isPresent())
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.NOT_FOUND.value(), "Parent not found."), HttpStatus.NOT_FOUND);
		
		ParentEntity parent = parentOpt.get();
		
		parent.setEmail(parentDTO.getEmail());
		parent.setFirstName(parentDTO.getFirstName());
		parent.setLastName(parentDTO.getLastName());
		parent.setPassword(parentDTO.getPassword());
		parent.setUsername(parentDTO.getUsername());
		
		parentRepository.save(parent);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated parent " + parent.getUsername());
		
		return new ResponseEntity<>(parent, HttpStatus.OK);
	}
	

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/insert/{studentId}/into/{parentId}")
	public ResponseEntity<?> connectStudnetWithParent(@PathVariable Integer studentId, @PathVariable Integer parentId) {
		
		try {
		
			Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
			Optional<ParentEntity> parentOpt = parentRepository.findById(parentId);
			
			if (!studentOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found."), HttpStatus.NOT_FOUND);
			
			StudentEntity student = studentOpt.get();
			
			if (student.getParents().size() > 2)
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.BAD_REQUEST.value(), "Student already has both parents."), HttpStatus.BAD_REQUEST);
			
			if (!parentOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Parent not found."), HttpStatus.NOT_FOUND);
			
			ParentEntity parent = parentOpt.get();
			
			student.getParents().add(parent);
			studentRepository.save(student);
			
			parent.getStudents().add(student);
			parentRepository.save(parent);
			
			logger.info(userServiceImpl.getLoggedInUsername() + " : connected student " + 
					student.getUsername() + " with parent " + parent.getUsername());
			
			return new ResponseEntity<>(parent, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/absence/note/{absenceId}")
	public ResponseEntity<?> addAbsenceNote(@PathVariable Integer absenceId, @RequestBody AbsenceDTO absenceDTO) {
		
		try {
			
			// check if absence exists
			Optional<AbsenceEntity> absenceOpt = absenceRepository.findById(absenceId);
			
			if (!absenceOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Absence not found."), HttpStatus.NOT_FOUND);
			
			AbsenceEntity absence = absenceOpt.get();
			StudentEntity student = absence.getGradeCard().getStudent();
			
			// only admin and student's parent may input notes
			boolean isAdmin = userServiceImpl.isAuthorizedAs(EUserRole.ADMIN);
			boolean isParent = userServiceImpl.getLoggedInUsername().isPresent() && student.getParents().stream()
					.map(p -> p.getUsername())
					.collect(Collectors.toSet())
					.contains(userServiceImpl.getLoggedInUsername().get());
			
			if (!isAdmin && !isParent)
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
			
			absence.setNote(absenceDTO.getNote());
			absenceRepository.save(absence);
			
			logger.info(userServiceImpl.getLoggedInUsername() + " : added note for " + student.getUsername() + 
					" regarding absence from " + absence.getGradeCard().getLecture().getSubject().getName() + " made on " + absence.getDate());
			
			return new ResponseEntity<>(absence, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.DELETE, value ="/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		try {
			
			Optional<ParentEntity> parentOpt = parentRepository.findById(id);
			
			if (!parentOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Parent not found."), HttpStatus.NOT_FOUND);
			
			ParentEntity parent = parentOpt.get();
			
			for (StudentEntity s : parent.getStudents()) {
				
				s.getParents().remove(parent);
				studentRepository.save(s);
			}
			
			parentRepository.delete(parent);
			
			logger.info(userServiceImpl.getLoggedInUsername() + " : deleted parent " + parent.getUsername());
			
			return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
