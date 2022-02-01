package com.nv.schoolsystemproject.controllers;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.entities.AbsenceEntity;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.GradeEntity;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.SessionEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.models.EmailObject;
import com.nv.schoolsystemproject.repositories.AbsenceRepository;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.GradeRepository;
import com.nv.schoolsystemproject.repositories.SessionRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.services.EmailService;

@RestController
@RequestMapping(path = "/api/v1/project/grade")
public class GradeController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private AbsenceRepository absenceRepository;
	@Autowired private GradeCardRepository gradeCardRepository;
	@Autowired private GradeRepository gradeRepository;
	@Autowired private SessionRepository sessionRepository;
	@Autowired private StudentRepository studentRepository;
	
	@Autowired private EmailService emailService;
	
	
	// =-=-=-= GET : ROUTES
	
	
	@RequestMapping(path = "/grade_cards", method = RequestMethod.GET) 
	public ModelAndView getStudentForGradeCards(HttpServletRequest request) {

		Integer id = Integer.parseInt((String) request.getParameter("idToUpdate"));
		StudentEntity student = studentRepository.findById(id).get();
		
		request.getSession().setAttribute("student", student);
		request.getSession().setAttribute("intArray5", new int[] { 1, 2, 3, 4, 5 });
		
		setGradeCardsAsRequestAttribute(request, student);
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/grade_cards/grade_cards");
	}
	
	
	@RequestMapping(path = "/get_absence", method = RequestMethod.GET) 
	public ModelAndView getAbsence(HttpServletRequest request) {

		Integer id = Integer.parseInt((String) request.getParameter("idToUpdate"));
		request.getSession().setAttribute("absence", absenceRepository.findById(id).get());
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/grade_cards/absence_note");
	}
	
	
	@RequestMapping(path = "/get_students_for_absences", method = RequestMethod.GET) 
	public ModelAndView getStudentsForAbsences(HttpServletRequest request) {
		
		SessionEntity session = sessionRepository.findById(
				Integer.parseInt((String) request.getParameter("sessionId"))).get();
		
		request.getSession().setAttribute("selectedSession", session);
		request.getSession().setAttribute("students", 
				studentRepository.findBySessionWithoutAbsence(session.getLecture(), session.getDate()));
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/sessions/students");
	}
	
	
	// =-=-=-= GET =-=-=-=
	
	
	@RequestMapping(path = "/update_grade", method = RequestMethod.GET	) 
	public ModelAndView updateGrade(HttpServletRequest request) {
		
		Integer id = Integer.parseInt(request.getParameter("gradeId"));
		Integer newGrade = Integer.parseInt(request.getParameter("newGrade"));
		
		GradeEntity grade = gradeRepository.findById(id).get();
		grade.setGrade(newGrade);
		gradeRepository.save(grade);
		
		StudentEntity student = studentRepository.findById(grade.getGradeCard().getStudent().getId()).get();

		request.getSession().setAttribute("student", student);
		
		setGradeCardsAsRequestAttribute(request, student);
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/grade_cards/grade_cards");
	}
	
	
	// =-=-=-= POST =-=-=-=
	
	
	@RequestMapping(path = "/add_absence_note", method = RequestMethod.POST) 
	public ModelAndView addAbsenceNote(HttpServletRequest request) {
		
		AbsenceEntity absence = (AbsenceEntity) request.getSession().getAttribute("absence");
		String absence_note = request.getParameter("absence_note");
		
		absence.setNote(absence_note);
		absenceRepository.save(absence);
		
		setStudentsAsRequestAttribute(request);
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/users");
	}
	
	
	@RequestMapping(path = "/add_absence", method = RequestMethod.GET) 
	public ModelAndView addAbsence(HttpServletRequest request) {
		
		Integer id = Integer.parseInt((String) request.getParameter("studentId"));
		
		StudentEntity student = studentRepository.findById(id).get();
		SessionEntity session = (SessionEntity) request.getSession().getAttribute("selectedSession");
		GradeCardEntity gradeCard = gradeCardRepository.findByLectureAndStudent(session.getLecture(), student).get();
		
		AbsenceEntity absence = new AbsenceEntity();
		
		absence.setDate(session.getDate());
		absence.setGradeCard(gradeCard);
		absence.setNote("");
		
		absenceRepository.save(absence);
		
		request.setAttribute("sessions", sessionRepository.findByLectureOrderByDate(session.getLecture()));
		
		notifyParentOfAbsence(student, session);
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/sessions/list");
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/delete")
	public ModelAndView delete(HttpServletRequest request) {
		
		Integer gradeCardId = Integer.parseInt(request.getParameter("gradeCardId"));
		GradeCardEntity gradeCardEntity = gradeCardRepository.findById(gradeCardId).get();
		
		Integer gradeId = Integer.parseInt(request.getParameter("gradeId"));
		GradeEntity gradeEntity = gradeRepository.findById(gradeId).get();
		
		gradeRepository.delete(gradeEntity);
		
		setGradeCardsAsRequestAttribute(request, gradeCardEntity.getStudent());
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/grade_cards/grade_cards");
	}
	
	
	// =-=-=-= AUX
	
	
	private void notifyParentOfAbsence(StudentEntity student, SessionEntity session) {

		EmailObject emailObject = new EmailObject();
		emailObject.setSubject(String.format("%s %s - izostanak sa lekcije '%s' iz predmeta '%s'", 
				student.getLastName(), student.getFirstName(), 
				session.getTopic(),
				session.getLecture().getSubject().getName()));
		
		emailObject.setText(String.format("Vaše dete je danas izostalo sa lekcije '%s' iz predmeta '%s'.", 
				session.getTopic(), session.getLecture().getSubject().getName()));
		
		if (!student.getParents().isEmpty()) {
			
			Iterator<ParentEntity> value = student.getParents().iterator();
			
			while (value.hasNext()) {
				
				ParentEntity parent = value.next();
				
				emailObject.setTo(parent.getEmail());
				emailService.sendSimpleMessage(emailObject);
				
				logger.info("Lecture #" + session.getLecture().getId() + " : parent notified of absence.");
	        }
			
		} else {
			
			logger.info("Lecture #" + session.getLecture().getId() + " : parent not found.");
		}
	}
	
	
	private void setGradeCardsAsRequestAttribute(HttpServletRequest request, StudentEntity student) {
		
		UserEntity currUser = (UserEntity) request.getSession().getAttribute("user");
		
		if (currUser.getRole() == EUserRole.TEACHER)
			request.getSession().setAttribute("gradeCards", 
					gradeCardRepository.findAllByStudentAndTeacher(student, currUser.getUsername()));
		else
			request.getSession().setAttribute("gradeCards", 
					student.getGradeCards());
	}
	
	
	private void setStudentsAsRequestAttribute(HttpServletRequest request) {
		
		UserEntity currUser = (UserEntity) request.getSession().getAttribute("user");
		
		if (currUser.getRole() == EUserRole.TEACHER)
			request.setAttribute("users", studentRepository.findByTeacher(currUser.getUsername()));
		else if (currUser.getRole() == EUserRole.PARENT)
			request.setAttribute("users", studentRepository.findByParent(currUser.getUsername()));
		else
			request.setAttribute("users", studentRepository.findAll());
	}
}
