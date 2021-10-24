package com.nv.schoolsystemproject.controllers;

import java.time.LocalDate;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nv.schoolsystemproject.controllers.dto.SessionRegisterDTO;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.SessionEntity;
import com.nv.schoolsystemproject.repositories.AbsenceRepository;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.SessionRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;

@RestController
@RequestMapping(path = "/api/v1/project/sessions")
public class SessionController {

	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired AbsenceRepository absenceRepository;
	@Autowired GradeCardRepository gradeCardRepository;
	@Autowired LectureRepository lectureRepository;
	@Autowired SessionRepository sessionRepository;
	@Autowired StudentRepository studentRepository;

	@Autowired private UserServiceImpl userServiceImpl;
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/register/{lectureId}")
	public ResponseEntity<?> addSessionToLecture(
			@PathVariable Integer lectureId, @Valid @RequestBody SessionRegisterDTO sessionDTO, BindingResult result) {
		
		try {
		
			Optional<LectureEntity> lectureOpt = lectureRepository.findById(lectureId);
			
			if (!lectureOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Lecture not found."), HttpStatus.NOT_FOUND);
			
			LectureEntity lecture = lectureOpt.get();
			
			// only admin and teacher teaching the subject may input sessions
			boolean isAdmin = userServiceImpl.isAuthorizedAs(EUserRole.ADMIN);
			boolean isTeacher = userServiceImpl.getLoggedInUsername().isPresent() && 
								userServiceImpl.getLoggedInUsername().get().equals(lecture.getTeacher().getUsername());
			
			SessionEntity newSession = new SessionEntity();
			newSession.setDate(LocalDate.now());
			newSession.setTopic(sessionDTO.getTopic());
			sessionRepository.save(newSession);
			
			logger.info("Session '" + sessionDTO.getTopic() + "' for subject '" + lecture.getSubject() + "' by " + lecture.getTeacher() + " registered!");
			
			return new ResponseEntity<>(lecture, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
