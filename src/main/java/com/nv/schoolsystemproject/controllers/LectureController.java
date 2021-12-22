package com.nv.schoolsystemproject.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.controllers.dto.LectureRegisterDTO;
import com.nv.schoolsystemproject.controllers.util.RESTError;
import com.nv.schoolsystemproject.entities.ESemester;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.GradeEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.SessionEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.SubjectEntity;
import com.nv.schoolsystemproject.entities.TeacherEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.models.EmailObject;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.GradeRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.SessionRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.repositories.SubjectRepository;
import com.nv.schoolsystemproject.repositories.TeacherRepository;
import com.nv.schoolsystemproject.services.EmailService;
import com.nv.schoolsystemproject.services.UserServiceImpl;

@RestController
@RequestMapping(path = "/api/v1/project/lectures")
public class LectureController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired GradeRepository gradeRepository;
	@Autowired GradeCardRepository gradeCardRepository;
	@Autowired LectureRepository lectureRepository;
	@Autowired SessionRepository sessionRepository;
	@Autowired StudentRepository studentRepository;
	@Autowired SubjectRepository subjectRepository;
	@Autowired TeacherRepository teacherRepository;

	@Autowired private UserServiceImpl userServiceImpl;
	
	@Autowired private EmailService emailService;
	
	
	// =-=-=-= GET : ROUTES
	
	
	@RequestMapping(path = "/list", method = RequestMethod.GET) 
	public ModelAndView getAllLectures(HttpServletRequest request) {
		
		request.setAttribute("lectures", (List<LectureEntity>) lectureRepository.findAll());
		
		ModelAndView mav = new ModelAndView("/admin/lectures/list");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/register_lecture", method = RequestMethod.GET) 
	public ModelAndView getSubjectsRegister(HttpServletRequest request) {
		
		request.getSession().setAttribute("teachers", (List<TeacherEntity>) teacherRepository.findAll());
		request.getSession().setAttribute("subjects", (List<SubjectEntity>) subjectRepository.findAll());
		
		ModelAndView mav = new ModelAndView("/admin/lectures/register");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/update_lecture", method = RequestMethod.GET) 
	public ModelAndView getUpdateLecture(HttpServletRequest request) {

		Integer idToUpdate = Integer.parseInt((String) request.getParameter("idToUpdate"));
		LectureEntity lectureToUpdate = lectureRepository.findById(idToUpdate).get();

		request.getSession().setAttribute("lectureToUpdate", lectureToUpdate);
		
		request.setAttribute("teachers", (List<TeacherEntity>) teacherRepository.findAll());
		request.setAttribute("subjects", (List<SubjectEntity>) subjectRepository.findAll());
		
		ModelAndView mav = new ModelAndView("/admin/lectures/update");
		
		return mav;
	}
	
	
	// =-=-=-= PUT
	
	
//	@RequestMapping(method = RequestMethod.PUT, value = "/register/{subjectId}/into/{teacherId}")
	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public ModelAndView connectSubjectWithTeacher(
//			@PathVariable Integer subjectId, @PathVariable Integer teacherId, @Valid @RequestBody LectureRegisterDTO lectureDTO, BindingResult result
			HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/lectures/register");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyLecture(request, error);
		
		if (foundError) {
			request.setAttribute("lectureRegisterMsg", error.toString());
			return mav;
		}
		
		SubjectEntity subject = subjectRepository.findById(Integer.parseInt(request.getParameter("idSubject"))).get();
		TeacherEntity teacher = teacherRepository.findById(Integer.parseInt(request.getParameter("idTeacher"))).get();
		
		ESemester semester = request.getParameter("semester").toUpperCase().equals("WINTER") ? ESemester.WINTER : ESemester.SUMMER;
		
		int year = Integer.parseInt(request.getParameter("year"));
		
		LectureEntity lecture = new LectureEntity();
		
		lecture.setSubject(subject);
		lecture.setTeacher(teacher);
		lecture.setSemester(semester);
		lecture.setYear(year);
		
		lectureRepository.save(lecture);
		
		logger.info("Lecture #" + lecture.getId() + " : created.");
		request.setAttribute("lectureRegisterSuccessMsg", "Predavanje je uspešno registrovano!");
		
		return mav;
		
//		try {
//		
//			Optional<SubjectEntity> subjectOpt = subjectRepository.findById(subjectId);
//			Optional<TeacherEntity> teacherOpt = teacherRepository.findById(teacherId);
//			
//			if (!subjectOpt.isPresent())
//				return new ResponseEntity<RESTError>(
//						new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found."), HttpStatus.NOT_FOUND);
//			
//			if (!teacherOpt.isPresent())
//				return new ResponseEntity<RESTError>(
//						new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found."), HttpStatus.NOT_FOUND);
//			
//			SubjectEntity subject = subjectOpt.get();
//			TeacherEntity teacher = teacherOpt.get();
//			
//			Optional<LectureEntity> lectureOpt = lectureRepository.findBySubjectAndTeacher(subject, teacher);
//			
//			if (lectureOpt.isPresent())
//				return new ResponseEntity<RESTError>(
//						new RESTError(HttpStatus.BAD_REQUEST.value(), "Lecture already exists."), HttpStatus.BAD_REQUEST);
//			
//			LectureEntity lecture = new LectureEntity();
//			lecture.setSubject(subject);
//			lecture.setTeacher(teacher);
//			lecture.setYear(lectureDTO.getYear());
//			lecture.setSemester(lectureDTO.getSemester());
//			lectureRepository.save(lecture);
//			
//			subject.getLectures().add(lecture);
//			subjectRepository.save(subject);
//			
//			teacher.getLectures().add(lecture);
//			teacherRepository.save(teacher);
//			
//			logger.info("Lecture #" + lecture.getId() + " : created.");
//			
//			return new ResponseEntity<>(lecture, HttpStatus.OK);
//			
//		} catch (Exception e) {
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
//							"Internal server error. Error: " + e.getMessage()), 
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
	}
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/insert/{studentId}/into/{lectureId}")
	public ResponseEntity<?> connectStudentWithLecture(@PathVariable Integer studentId, @PathVariable Integer lectureId) {
		
		try {
		
			Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
			Optional<LectureEntity> lectureOpt = lectureRepository.findById(lectureId);
			
			if (!studentOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Student not found."), HttpStatus.NOT_FOUND);
			
			if (!lectureOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Lecture not found."), HttpStatus.NOT_FOUND);			
			
			StudentEntity student = studentOpt.get();
			LectureEntity lecture = lectureOpt.get();
			
			Optional<GradeCardEntity> gradeCardOpt = gradeCardRepository.findByLectureAndStudent(lecture, student);
			
			if (gradeCardOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Such grade card already exists."), HttpStatus.NOT_FOUND);
			
			GradeCardEntity gradeCard = new GradeCardEntity();
			gradeCard.setLecture(lecture);
			gradeCard.setStudent(student);
			gradeCard.setPresent(0);
			gradeCard.setAbsent(0);
			gradeCardRepository.save(gradeCard);
			
			student.getGradeCards().add(gradeCard);
			studentRepository.save(student);
			
			lecture.getGradeCards().add(gradeCard);
			lectureRepository.save(lecture);
			
			logger.info("Lecture #" + lecture.getId() + " : student " + student.getUsername() + " added.");
			
			return new ResponseEntity<>(lecture, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/grade/{studentId}/in/{lectureId}/with/{grade}")
	public ResponseEntity<?> gradeStudentInLecture(
			@PathVariable Integer studentId, @PathVariable Integer lectureId, @PathVariable Integer grade) {					
		
		try {
			
			// if grade is invalid return
			if (!(0 < grade && grade <= 5))
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.BAD_REQUEST.value(), "Invalid grade."), HttpStatus.BAD_REQUEST);
		
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
			
			// only admin and teacher teaching the subject may input grades
			boolean isAdmin = userServiceImpl.isAuthorizedAs(EUserRole.ADMIN);
			boolean isTeacher = userServiceImpl.getLoggedInUsername().isPresent() && 
								userServiceImpl.getLoggedInUsername().get().equals(lecture.getTeacher().getUsername());  
			
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

			// create new grade
			GradeEntity newGrade = new GradeEntity();
			newGrade.setDate(LocalDate.now());
			newGrade.setGrade(grade);
			newGrade.setGradeCard(gradeCard);
			gradeRepository.save(newGrade);
			
			gradeCard.getGrades().add(newGrade);
			gradeCardRepository.save(gradeCard);
			
			logger.info("Lecture #" + lecture.getId() + " : student " + student.getUsername() + " graded " + grade);
			
			// !!! OVDE IDE POZIVANJE METODA KOJI SALJE EMAIL RODITELJU
			
			EmailObject emailObject = new EmailObject();
			emailObject.setSubject(String.format("%s %s - new grade in subject '%s'", 
					student.getLastName(), student.getFirstName(), 
					newGrade.getGradeCard().getLecture().getSubject().getName()));
			
			emailObject.setText(String.format("Your child was graded %d today.", newGrade.getGrade()));
			
			Optional<ParentEntity> parentOpt = student.getParents().stream().findFirst();
			
			if (parentOpt.isPresent()) {
				
				emailObject.setTo(parentOpt.get().getEmail());
				emailService.sendSimpleMessage(emailObject);
				
				logger.info("Lecture #" + lecture.getId() + " : parent notified.");
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
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/unregister/{subjectId}/into/{teacherId}")
	public ResponseEntity<?> disconnectSubjectWithTeacher(@PathVariable Integer subjectId, @PathVariable Integer teacherId, @Valid @RequestBody LectureRegisterDTO lectureDTO, BindingResult result) {
		
		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
		
		try {
		
			Optional<SubjectEntity> subjectOpt = subjectRepository.findById(subjectId);
			Optional<TeacherEntity> teacherOpt = teacherRepository.findById(teacherId);
			
			if (!subjectOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Subject not found."), HttpStatus.NOT_FOUND);
			
			if (!teacherOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.NOT_FOUND.value(), "Teacher not found."), HttpStatus.NOT_FOUND);
			
			SubjectEntity subject = subjectOpt.get();
			TeacherEntity teacher = teacherOpt.get();
			
			Optional<LectureEntity> lectureOpt = lectureRepository.findBySubjectAndTeacher(subject, teacher);
			
			if (!lectureOpt.isPresent())
				return new ResponseEntity<RESTError>(
						new RESTError(HttpStatus.BAD_REQUEST.value(), "Such lecture does not exist."), HttpStatus.BAD_REQUEST);
			
			LectureEntity lecture = lectureOpt.get();
			
			subject.getLectures().remove(lecture);
			subjectRepository.save(subject);
			
			teacher.getLectures().remove(lecture);
			teacherRepository.save(teacher);
			
			lectureRepository.delete(lecture);
			
			logger.info("Lecture #" + lecture.getId() + " : deleted.");
			
			return new ResponseEntity<>(lecture, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<RESTError>(
					new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Internal server error. Error: " + e.getMessage()), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	// =-=-=-= UPDATE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/update")
	public ModelAndView update(
//			@PathVariable Integer id, @Valid @RequestBody SubjectRegisterDTO subjectDTO, BindingResult result
			HttpServletRequest request
			) {
		
		LectureEntity lecture = (LectureEntity) request.getSession().getAttribute("lectureToUpdate");
		
		ModelAndView mav = new ModelAndView("/admin/lectures/update");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyLecture(request, error);
		
		SubjectEntity subject = subjectRepository.findById(Integer.parseInt(request.getParameter("idSubject"))).get();
		TeacherEntity teacher = teacherRepository.findById(Integer.parseInt(request.getParameter("idTeacher"))).get();
		
		ESemester semester = request.getParameter("semester").toUpperCase().equals("WINTER") ? ESemester.WINTER : ESemester.SUMMER;
		
		int year = Integer.parseInt(request.getParameter("year"));
		
		if (foundError) {

			request.setAttribute("lectureUpdateMsg", error.toString());

			request.setAttribute("teachers", (List<TeacherEntity>) teacherRepository.findAll());
			request.setAttribute("subjects", (List<SubjectEntity>) subjectRepository.findAll());
			
			return mav;
		}
		
		lecture.setSubject(subject);
		lecture.setTeacher(teacher);
		lecture.setSemester(semester);
		lecture.setYear(year);
		
		lectureRepository.save(lecture);

		request.setAttribute("lectureUpdateMsg", error.toString());

		request.getSession().setAttribute("lectureToUpdate", lecture);
		request.setAttribute("teachers", (List<TeacherEntity>) teacherRepository.findAll());
		request.setAttribute("subjects", (List<SubjectEntity>) subjectRepository.findAll());
		
		logger.info("Lecture #" + lecture.getId() + " : updated.");
		request.setAttribute("lectureUpdateSuccessMsg", "Predavanje je uspešno ažurirano!");
		
		return mav;
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
//	@RequestMapping(method = RequestMethod.DELETE, value ="/{id}")
	@RequestMapping(method = RequestMethod.GET, value = "/delete")
	public ModelAndView delete(
//			@PathVariable Integer id
			HttpServletRequest request
			) {
		
		Integer idToDelete = Integer.parseInt(request.getParameter("idToDelete"));
		LectureEntity lecture = lectureRepository.findById(idToDelete).get();
		
		for (SessionEntity s : lecture.getSessions())
			sessionRepository.delete(s);
		
		for (GradeCardEntity g : lecture.getGradeCards())
			gradeCardRepository.delete(g);
		
//		logger.info(userServiceImpl.getLoggedInUsername() + " : deleted lecture " + lecture.getId());
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + 
				String.format(" : deleted lecture %s held by %s %s.", lecture.getSubject().getName(), lecture.getTeacher().getFirstName(), lecture.getTeacher().getLastName()));
		
		request.setAttribute("deleteSuccessMsg", String.format("Predavanje predmeta %s koje je držao %s %s je uspešno obrisano!", 
				lecture.getSubject().getName(), lecture.getTeacher().getFirstName(), lecture.getTeacher().getLastName()));
		
		lectureRepository.delete(lecture);
		
		request.setAttribute("lectures", (List<LectureEntity>) lectureRepository.findAll());
		
		return new ModelAndView("/admin/lectures/list");
	}
	
	
	private boolean verifyLecture(HttpServletRequest request, StringBuilder error) {
		
		boolean foundError = false;
		
		try {			
			Integer.parseInt(request.getParameter("idSubject"));
		} catch (NumberFormatException e) {
			error.append("Morate odabrati predmet. ");
			foundError = true;
		}
		
		try {
			Integer.parseInt(request.getParameter("idTeacher"));
		} catch (NumberFormatException e) {
			error.append("Morate odabrati nastavnika. ");
			foundError = true;
		}
		
		if (request.getParameter("semester") == null) {
			error.append("Morate odabrati semestar. ");
			foundError = true;
		}
		
		try {
			Integer.parseInt(request.getParameter("year"));
		} catch (NumberFormatException e) {
			error.append("Morate uneti celobrojnu vrednost.");
			foundError = true;
		}
		
		return foundError;
	}
}
