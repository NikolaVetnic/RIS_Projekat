package com.nv.schoolsystemproject.controllers;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
	
	
	// =-=-=-= GET : ROUTES
	
	
	@RequestMapping(path = "/home", method = RequestMethod.GET) 
	public ModelAndView getSubjectsHome(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/subjects/home");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/list", method = RequestMethod.GET) 
	public ModelAndView getAllSubjects(HttpServletRequest request) {
		
		request.setAttribute("subjects", (List<SubjectEntity>) subjectRepository.findAll());
		
		ModelAndView mav = new ModelAndView("/admin/subjects/list");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/register_subject", method = RequestMethod.GET) 
	public ModelAndView getSubjectsRegister(HttpServletRequest request) {
		
		request.setAttribute("teachers", (List<TeacherEntity>) teacherRepository.findAll());
		
		ModelAndView mav = new ModelAndView("/admin/subjects/register");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/update_subject", method = RequestMethod.GET) 
	public ModelAndView getUpdateSubject(HttpServletRequest request) {

		Integer idToUpdate = Integer.parseInt((String) request.getParameter("idToUpdate"));
		SubjectEntity subjectToUpdate = subjectRepository.findById(idToUpdate).get();

		request.getSession().setAttribute("subjectToUpdate", subjectToUpdate);
		
		ModelAndView mav = new ModelAndView("/admin/subjects/update");
		
		return mav;
	}
	
	
	// =-=-=-= POST
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public ModelAndView addNewSubject(
//			@Valid @RequestBody SubjectRegisterDTO subjectDTO, BindingResult result
			HttpServletRequest request) {
		
		SubjectEntity subject = new SubjectEntity();
		ModelAndView mav = new ModelAndView("/admin/subjects/register");

		StringBuilder error = new StringBuilder();
		boolean foundError = verifySubject(request, error);
		
		if (foundError) {
			request.setAttribute("subjectUpdateMsg", error.toString());
			return mav;
		}
		
		subject.setName(request.getParameter("name"));
		subject.setYearAccredited(Integer.parseInt(request.getParameter("yearAccredited")));
		subject.setTotalHours(Double.parseDouble(request.getParameter("totalHours")));
		
		subjectRepository.save(subject);
		
		logger.info(subject.getName() + " : created.");
		request.setAttribute("subjectRegisterSuccessMsg", "Predmet je uspešno registrovan!");
		
		return mav;
		
//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
//		
//		if (result.hasErrors())
//			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
//		else
//			subjectValidator.validate(subjectDTO, result);
//		
//		SubjectEntity subject = new SubjectEntity();
//		
//		subject.setName(subjectDTO.getName());
//		subject.setTotalHours(subjectDTO.getTotalHours());
//		subject.setYearAccredited(subjectDTO.getYearAccredited());
//		
//		subjectRepository.save(subject);
//		
//		logger.info(subject.getName() + " : created.");
//		
//		return new ResponseEntity<>(subject, HttpStatus.OK);
	}
	

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
	
	
	// =-=-=-= PUT =-=-=-=
	
	
//	@RequestMapping(method = RequestMethod.PUT, value = "/update/{id}")
	@RequestMapping(method = RequestMethod.POST, value = "/update")
	public ModelAndView update(
//			@PathVariable Integer id, @Valid @RequestBody SubjectRegisterDTO subjectDTO, BindingResult result
			HttpServletRequest request
			) {
		
		SubjectEntity subject = (SubjectEntity) request.getSession().getAttribute("subjectToUpdate");
		
		ModelAndView mav = new ModelAndView("/admin/subjects/update");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifySubject(request, error);
		
		if (foundError) {
			request.setAttribute("subjectUpdateMsg", error.toString());
			return mav;
		}
		
		subject.setName(request.getParameter("name"));
		subject.setYearAccredited(Integer.parseInt(request.getParameter("yearAccredited")));
		subject.setTotalHours(Double.parseDouble(request.getParameter("totalHours")));
						
		subjectRepository.save(subject);
		
		request.getSession().setAttribute("subjectToUpdate", subject);
		
//		logger.info(userServiceImpl.getLoggedInUsername() + " : updated subject " + subject.getName());
		request.setAttribute("subjectUpdateSuccessMsg", 
				String.format("Predmet %s je uspešno ažuriran!", subject.getName()));
		
		return mav;
		
//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
//		
//		if (result.hasErrors())
//			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
//		else
//			subjectValidator.validate(subjectDTO, result);
//		
//		Optional<SubjectEntity> subjectOpt = subjectRepository.findById(id);
//		
//		if (!subjectOpt.isPresent())
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found."), HttpStatus.NOT_FOUND);
//		
//		SubjectEntity subject = subjectOpt.get();
//		
//		subject.setName(subjectDTO.getName());
//		subject.setTotalHours(subjectDTO.getTotalHours());
//		subject.setYearAccredited(subjectDTO.getYearAccredited());
//		
//		subjectRepository.save(subject);
//		
//		logger.info(userServiceImpl.getLoggedInUsername() + " : updated subject " + subject.getName());
//		
//		return new ResponseEntity<>(subject, HttpStatus.OK);
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
//	@RequestMapping(method = RequestMethod.DELETE, value ="/{id}")
	@RequestMapping(method = RequestMethod.GET, value = "/delete")
	public ModelAndView delete(
//			@PathVariable Integer id
			HttpServletRequest request
			) {
		
		Integer idToDelete = Integer.parseInt(request.getParameter("idToDelete"));
		SubjectEntity subject = subjectRepository.findById(idToDelete).get();
		
		for (LectureEntity l : subject.getLectures()) {

			TeacherEntity currTeacher = l.getTeacher();
			currTeacher.getLectures().remove(l);
			teacherRepository.save(currTeacher);

			Set<GradeCardEntity> currGC = l.getGradeCards();

			Iterator<GradeCardEntity> it = currGC.iterator();

			while (it.hasNext()) {

				GradeCardEntity gc = it.next();

				gc.setLecture(null);
				gradeCardRepository.save(gc);
			}

			lectureRepository.delete(l);
		}

		subjectRepository.delete(subject);
		
//		logger.info(userServiceImpl.getLoggedInUsername() + " : deleted subject " + subject.getName());
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + " : deleted subject " + subject.getName());
		
		request.setAttribute("deleteSuccessMsg", String.format("Predmet %s je uspešno obrisan!", 
				subject.getName()));
		
		return getModelAndViewToSubjects(request);
		
//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
//		
//		try {
//		
//			Optional<SubjectEntity> subjectOpt = subjectRepository.findById(id);
//			
//			if (!subjectOpt.isPresent())
//				return new ResponseEntity<RESTError>(
//						new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found."), HttpStatus.NOT_FOUND);
//			
//			SubjectEntity subject = subjectOpt.get();
//			
//			for (LectureEntity l : subject.getLectures()) {
//				
//				TeacherEntity currTeacher = l.getTeacher();
//				currTeacher.getLectures().remove(l);
//				teacherRepository.save(currTeacher);
//				
//				Set<GradeCardEntity> currGC = l.getGradeCards();
//				
//				Iterator<GradeCardEntity> it = currGC.iterator();
//				
//				while(it.hasNext()){
//					
//					GradeCardEntity gc = it.next();
//					
//					gc.setLecture(null);
//					gradeCardRepository.save(gc);
//				}
//				
//				lectureRepository.delete(l);
//			}
//			
//			subjectRepository.delete(subject);
//			
//			logger.info(userServiceImpl.getLoggedInUsername() + " : deleted subject " + subject.getName());
//			
//			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
//			
//		} catch (Exception e) {
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error. Error: " + e.getMessage()), 
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
	}
	
	
	private ModelAndView getModelAndViewToSubjects(HttpServletRequest request) {
		
		String path = "/admin/subjects/list";
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			path = "/admin/error";
		
		List<SubjectEntity> subjects = (List<SubjectEntity>) subjectRepository.findAll();
		
		request.setAttribute("subjects", subjects);
		
//		logger.info(userServiceImpl.getLoggedInUsername() + " : viewed all users.");
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + " : viewed all subjects.");
		
		ModelAndView mav = new ModelAndView(path);
		
		return mav;
	}
	
	
	private boolean verifySubject(HttpServletRequest request, StringBuilder error) {
		
		boolean foundError = false;
		
		String name = request.getParameter("name");
		if (name.length() < 5 || 25 < name.length()) {
			error.append("Ime predmeta mora biti dužine od 5 do 25 karaktera. ");
			foundError = true;
		}
		
		try {
			Integer.parseInt(request.getParameter("yearAccredited"));
		} catch (NumberFormatException e) {
			error.append("Morate uneti celobrojnu vrednost. ");
			foundError = true;
		}
		
		try {
			Double.parseDouble(request.getParameter("totalHours"));
		} catch (NumberFormatException e) {
			error.append("Morate uneti realnu vrednost. ");
			foundError = true;
		}
		
		return foundError;
	}
}
