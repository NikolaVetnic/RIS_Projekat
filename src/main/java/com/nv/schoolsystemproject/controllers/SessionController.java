package com.nv.schoolsystemproject.controllers;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.SessionEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.AbsenceRepository;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.SessionRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;

@RestController
@RequestMapping(path = "/api/v1/project/sessions")
public class SessionController {

	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired AbsenceRepository absenceRepository;
	@Autowired GradeCardRepository gradeCardRepository;
	@Autowired LectureRepository lectureRepository;
	@Autowired SessionRepository sessionRepository;
	@Autowired StudentRepository studentRepository;
	
	
	// =-=-=-= GET : ROUTES
	
	
	@RequestMapping(path = "/list", method = RequestMethod.GET) 
	public ModelAndView getSessions(HttpServletRequest request) {
		
		LectureEntity selectedLecture = lectureRepository
				.findById(Integer.parseInt((String) request.getParameter("idToUpdate"))).get();
		
		request.getSession().setAttribute("selectedLecture", selectedLecture);
		request.setAttribute("sessions", sessionRepository.findAllByOrderByDate());
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/sessions/list");
	}
	
	
	@RequestMapping(path = "/register_session", method = RequestMethod.GET) 
	public ModelAndView getRegistrationHome(HttpServletRequest request) {
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/sessions/register");
	}
	
	
	// =-=-=-= POST =-=-=-=
	
	
	@RequestMapping(path = "/register", method = RequestMethod.POST) 
	public ModelAndView registerSession(HttpServletRequest request) {
		
		LectureEntity lecture = (LectureEntity) request.getSession().getAttribute("selectedLecture");
		SessionEntity session = new SessionEntity();
		
		session.setTopic(request.getParameter("topic"));
		session.setLecture(lecture);
		session.setDate(LocalDate.parse(request.getParameter("date")));
		
		sessionRepository.save(session);
		
		request.setAttribute("sessionRegisterSuccessMsg", "Sesija registrovana!");
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/sessions/register");
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/delete")
	public ModelAndView delete(HttpServletRequest request) {
		
		Integer idToDelete = Integer.parseInt(request.getParameter("idToDelete"));
		SessionEntity session = sessionRepository.findById(idToDelete).get();
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + 
				String.format(" : deleted session %s from lecture %s (teacher %s %s).", 
						session.getTopic(), session.getLecture().getSubject(),
						session.getLecture().getTeacher().getLastName(), session.getLecture().getTeacher().getFirstName()));		
		request.setAttribute("deleteSuccessMsg", String.format("Sesija %s je uspešno obrisana!", session.getTopic()));
				
		sessionRepository.delete(session);
		
		LectureEntity selectedLecture = (LectureEntity) request.getSession().getAttribute("selectedLecture");
		List<SessionEntity> sessions = lectureRepository.findById(selectedLecture.getId()).get().getSessions();
		
		request.setAttribute("sessions", sessionRepository.findAllByOrderByDate());
		
		System.out.println(sessions);
		
		return new ModelAndView("/admin/sessions/list");
	}
}
