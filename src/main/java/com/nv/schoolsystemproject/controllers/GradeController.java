package com.nv.schoolsystemproject.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.AbsenceEntity;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.GradeEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.SessionEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.AbsenceRepository;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.GradeRepository;
import com.nv.schoolsystemproject.repositories.SessionRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;

@RestController
@RequestMapping(path = "/api/v1/project/grade")
public class GradeController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private AbsenceRepository absenceRepository;
	@Autowired private GradeCardRepository gradeCardRepository;
	@Autowired private GradeRepository gradeRepository;
	@Autowired private SessionRepository sessionRepository;
	@Autowired private StudentRepository studentRepository;
	@Autowired private UserRepository userRepository;
	
	@Autowired private UserServiceImpl userServiceImpl;
	
	
	// =-=-=-= GET : ROUTES
	
	
	@RequestMapping(path = "/grade_cards", method = RequestMethod.GET) 
	public ModelAndView getStudentForGradeCards(HttpServletRequest request) {

		Integer id = Integer.parseInt((String) request.getParameter("idToUpdate"));
		
		request.getSession().setAttribute("student", studentRepository.findById(id).get());
		request.getSession().setAttribute("intArray5", new int[] { 1, 2, 3, 4, 5 });
		
		ModelAndView mav = new ModelAndView("/admin/grade_cards/grade_cards");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/get_absence", method = RequestMethod.GET) 
	public ModelAndView getAbsence(HttpServletRequest request) {

		Integer id = Integer.parseInt((String) request.getParameter("idToUpdate"));
		request.getSession().setAttribute("absence", absenceRepository.findById(id).get());
		
		ModelAndView mav = new ModelAndView("/admin/grade_cards/absence_note");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/get_students_for_absences", method = RequestMethod.GET) 
	public ModelAndView getStudentsForAbsences(HttpServletRequest request) {
		
		Integer id = Integer.parseInt((String) request.getParameter("sessionId"));
		
		SessionEntity session = sessionRepository.findById(id).get();
		LectureEntity lecture = session.getLecture(); 
		
		List<StudentEntity> students = (List<StudentEntity>) studentRepository.findAll();
		
		students = students.stream()
			.filter(s -> s.isTakingLecture(lecture.getId()))
			.filter(s -> !s.hasAbsenceForSession(session))
			.collect(Collectors.toList());
		
		request.getSession().setAttribute("selectedSession", session);
		request.getSession().setAttribute("students", students);
		
		return new ModelAndView("/admin/sessions/students");
	}
	
	
	// =-=-=-= GET =-=-=-=
	
	
	@RequestMapping(path = "/update_grade", method = RequestMethod.GET	) 
	public ModelAndView updateGrade(HttpServletRequest request) {
		
		Integer id = Integer.parseInt(request.getParameter("gradeId"));
		Integer newGrade = Integer.parseInt(request.getParameter("newGrade"));
		
		GradeEntity grade = gradeRepository.findById(id).get();
		grade.setGrade(newGrade);
		gradeRepository.save(grade);

		request.getSession().setAttribute("student", 
				studentRepository.findById(grade.getGradeCard().getStudent().getId()).get());
		
		return new ModelAndView("/admin/grade_cards/grade_cards");
	}
	
	
	// =-=-=-= POST =-=-=-=
	
	
	@RequestMapping(path = "/add_absence_note", method = RequestMethod.POST) 
	public ModelAndView addAbsenceNote(HttpServletRequest request) {
		
		AbsenceEntity absence = (AbsenceEntity) request.getSession().getAttribute("absence");
		String absence_note = request.getParameter("absence_note");
		
		absence.setNote(absence_note);
		absenceRepository.save(absence);
		
		List<UserEntity> users = StreamSupport
				  .stream(userRepository.findAll().spliterator(), false)
				  .collect(Collectors.toList());
		
		request.setAttribute("users", users);
		
		return new ModelAndView("/admin/users");
	}
	
	
	@RequestMapping(path = "/add_absence", method = RequestMethod.GET) 
	public ModelAndView addAbsence(HttpServletRequest request) {
		
		Integer id = Integer.parseInt((String) request.getParameter("studentId"));
		
		StudentEntity student = studentRepository.findById(id).get();
		SessionEntity session = (SessionEntity) request.getSession().getAttribute("selectedSession");
		
		System.out.println("lectureID ::: " + session.getLecture().getId());
		System.out.println(student);
		
		GradeCardEntity gradeCard = student.getGradeCards().stream()
				.filter(gc -> gc.getLecture().getId() == session.getLecture().getId())
				.findFirst().get();
		
		AbsenceEntity absence = new AbsenceEntity();
		
		absence.setDate(session.getDate());
		absence.setGradeCard(gradeCard);
		absence.setNote("");
		
		absenceRepository.save(absence);
		
		
		
		
		request.setAttribute("sessions", ((List<SessionEntity>) sessionRepository.findAll()).stream()
				.filter(s -> s.getLecture().getId() == session.getLecture().getId())
				.sorted(new Comparator<SessionEntity>() {
				@Override public int compare(SessionEntity o1, SessionEntity o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
			}).collect(Collectors.toList()));
		
		
		
		return new ModelAndView("/admin/sessions/list");
	}
	
	
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
