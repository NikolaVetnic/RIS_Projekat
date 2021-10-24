package com.nv.schoolsystemproject.controllers;

import java.time.LocalDate;
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

import com.nv.schoolsystemproject.controllers.dto.SessionRegisterDTO;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.AbsenceEntity;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.SessionEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.models.EmailObject;
import com.nv.schoolsystemproject.repositories.AbsenceRepository;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.SessionRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.services.EmailService;
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

	@Autowired private EmailService emailService;
	
	
	// =-=-=-= POST =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/register/{lectureId}")
	public ResponseEntity<?> addSessionToLecture(
			@PathVariable Integer lectureId, @Valid @RequestBody SessionRegisterDTO sessionDTO, BindingResult result) {
		
		try {
		
			// check if such lecture exists
			Optional<LectureEntity> lectureOpt = lectureRepository.findById(lectureId);
			
			if (!lectureOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Lecture not found."), HttpStatus.NOT_FOUND);
			
			LectureEntity lecture = lectureOpt.get();
			
			// check if input has errors
			if (result.hasErrors())
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			
			// only admin and teacher teaching the subject may input grades
			boolean isAdmin = userServiceImpl.isAuthorizedAs(EUserRole.ADMIN);
			boolean isTeacher = userServiceImpl.getLoggedInUsername().isPresent() && 
								userServiceImpl.getLoggedInUsername().get().equals(lecture.getTeacher().getUsername());  
			
			if (!isAdmin && !isTeacher)
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
			
			// check if today's session is already created
			if (sessionRepository.findByLectureAndDate(lecture, LocalDate.now()).isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.UNAUTHORIZED.value(), "Today's session already created."), HttpStatus.UNAUTHORIZED);
			
			// create new session
			SessionEntity newSession = new SessionEntity();
			newSession.setDate(LocalDate.now());
			newSession.setTopic(sessionDTO.getTopic());
			newSession.setLecture(lecture);
			sessionRepository.save(newSession);
			
			lecture.getSessions().add(newSession);
			lectureRepository.save(lecture);
			
			logger.info("Session '" + sessionDTO.getTopic() + "' for subject '" + lecture.getSubject() + "' by " + lecture.getTeacher() + " registered!");
			
			return new ResponseEntity<>(lecture, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/absence/{studentId}/from/{lectureId}")
	public ResponseEntity<?> addAbsentStudentFromLecture(
			@PathVariable Integer studentId, @PathVariable Integer lectureId) {
		
		try {
		
			Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
			Optional<LectureEntity> lectureOpt = lectureRepository.findById(lectureId);
		
			// if student is not found return
			if (!studentOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found."), HttpStatus.NOT_FOUND);
			
			// if lecture is not found return
			if (!lectureOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Lecture not found."), HttpStatus.NOT_FOUND);			
			
			// get student and lecture
			StudentEntity student = studentOpt.get();
			LectureEntity lecture = lectureOpt.get();
			
			// check if student is attending selected class
			if (!lecture.getGradeCards().stream().map(gc -> gc.getStudent()).anyMatch(s -> s.equals(student)))
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.UNAUTHORIZED.value(), "Selected student does not attend selected class."), HttpStatus.UNAUTHORIZED);
			
			// only admin and teacher teaching the subject may input grades
			boolean isAdmin = userServiceImpl.isAuthorizedAs(EUserRole.ADMIN);
			boolean isTeacher = userServiceImpl.getLoggedInUsername().isPresent() && 
								userServiceImpl.getLoggedInUsername().get().equals(lecture.getTeacher().getUsername());
			
			Optional<GradeCardEntity> existingGradeCard = lecture.getGradeCards().stream().filter(gc -> gc.getStudent().equals(student)).findFirst();
			
			// check if today's session is already created
			if (existingGradeCard.isPresent())
				if (absenceRepository.findByGradeCardAndDate(existingGradeCard.get(), LocalDate.now()).isPresent())
					return new ResponseEntity<RESTError>(
							new RESTError(HttpStatus.UNAUTHORIZED.value(), "Today's absence already registered."), HttpStatus.UNAUTHORIZED);
			
			if (!isAdmin && !isTeacher)
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
			
			// if no grade card is created yet create one, otherwise get it
			GradeCardEntity gradeCard = gradeCardRepository.findByLectureAndStudent(lecture, student).orElseGet(() -> {
				
				GradeCardEntity newGradeCard = new GradeCardEntity();
				newGradeCard.setLecture(lecture);
				newGradeCard.setStudent(student);
				gradeCardRepository.save(newGradeCard);
				
				student.getGradeCards().add(newGradeCard);
				studentRepository.save(student);
				
				lecture.getGradeCards().add(newGradeCard);
				lectureRepository.save(lecture);
				
				return newGradeCard;
			});
			
			// create new absence
			AbsenceEntity newAbsence = new AbsenceEntity();
			newAbsence.setNote("");
			newAbsence.setDate(LocalDate.now());
			newAbsence.setGradeCard(gradeCard);
			absenceRepository.save(newAbsence);
			
			gradeCard.getAbsences().add(newAbsence);
			gradeCardRepository.save(gradeCard);
			
			logger.info("Lecture #" + lecture.getId() + " : student " + student.getUsername() + " is absent on " + newAbsence.getDate());
			
			// parent is notified of absence
			EmailObject emailObject = new EmailObject();
			emailObject.setSubject(String.format("%s %s - absent from '%s' on %s", 
					student.getLastName(), student.getFirstName(), 
					gradeCard.getLecture().getSubject(), newAbsence.getDate()));
			
			emailObject.setText(String.format("Your child was absent from '%s' on %s.", gradeCard.getLecture().getSubject().getName(), newAbsence.getDate()));
			
			Optional<ParentEntity> parentOpt = student.getParents().stream().findFirst();
			
			if (parentOpt.isPresent()) {
				
				emailObject.setTo(parentOpt.get().getEmail());
				emailService.sendSimpleMessage(emailObject);
				
				logger.info("Lecture #" + lecture.getId() + " : parent notified of absence.");
			} else {
				
				logger.info("Lecture #" + lecture.getId() + " : parent not found.");
			}
			
			return new ResponseEntity<>(lecture, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
