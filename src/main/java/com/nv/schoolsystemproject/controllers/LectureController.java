package com.nv.schoolsystemproject.controllers;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.entities.AbsenceEntity;
import com.nv.schoolsystemproject.entities.ESemester;
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
import com.nv.schoolsystemproject.repositories.AbsenceRepository;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.GradeRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.SessionRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.repositories.SubjectRepository;
import com.nv.schoolsystemproject.repositories.TeacherRepository;
import com.nv.schoolsystemproject.services.EmailService;

@RestController
@RequestMapping(path = "/api/v1/project/lectures")
public class LectureController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired AbsenceRepository absenceRepository;
	@Autowired GradeRepository gradeRepository;
	@Autowired GradeCardRepository gradeCardRepository;
	@Autowired LectureRepository lectureRepository;
	@Autowired SessionRepository sessionRepository;
	@Autowired StudentRepository studentRepository;
	@Autowired SubjectRepository subjectRepository;
	@Autowired TeacherRepository teacherRepository;
	
	@Autowired private EmailService emailService;
	
	
	// =-=-=-= GET : ROUTES
	
	
	@RequestMapping(path = "/list", method = RequestMethod.GET) 
	public ModelAndView getAllLectures(HttpServletRequest request) {
		
		setLecturesAsRequestAttribute(request);
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/lectures/list");
	}
	
	
	@RequestMapping(path = "/all_students", method = RequestMethod.GET) 
	public ModelAndView getAllStudents(HttpServletRequest request) {
		
		LectureEntity selectedLecture = lectureRepository
				.findById(Integer.parseInt((String) request.getParameter("idToUpdate"))).get();
		request.getSession().setAttribute("selectedLecture", selectedLecture);
		
		if (request.getSession().getAttribute("role").equals("teacher"))
			setStudentsAsRequestAttributeForTeacher(request, selectedLecture);
		else
			setStudentsAsRequestAttributeForAdmin(request);
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/lectures/students");
	}
	
	
	@RequestMapping(path = "/grade_student", method = RequestMethod.GET) 
	public ModelAndView getStudentToGrade(HttpServletRequest request) {
		
		request.getSession().setAttribute("studentToGrade", studentRepository
				.findById(Integer.parseInt((String) request.getParameter("idToUpdate"))).get());
		
		request.getSession().setAttribute("intArray5", new int[] { 1, 2, 3, 4, 5 });
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/lectures/grade_student");
	}
	
	
	@RequestMapping(path = "/register_lecture", method = RequestMethod.GET) 
	public ModelAndView getSubjectsRegister(HttpServletRequest request) {
		
		request.getSession().setAttribute("teachers", (List<TeacherEntity>) teacherRepository.findAll());
		request.getSession().setAttribute("subjects", (List<SubjectEntity>) subjectRepository.findAll());
		
		return new ModelAndView("/admin/lectures/register");
	}
	
	
	@RequestMapping(path = "/update_lecture", method = RequestMethod.GET) 
	public ModelAndView getUpdateLecture(HttpServletRequest request) {

		Integer idToUpdate = Integer.parseInt((String) request.getParameter("idToUpdate"));
		LectureEntity lectureToUpdate = lectureRepository.findById(idToUpdate).get();

		request.getSession().setAttribute("lectureToUpdate", lectureToUpdate);
		
		request.setAttribute("teachers", (List<TeacherEntity>) teacherRepository.findAll());
		request.setAttribute("subjects", (List<SubjectEntity>) subjectRepository.findAll());
		
		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/lectures/update");
	}
	
	
	// =-=-=-= PUT
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public ModelAndView connectSubjectWithTeacher(HttpServletRequest request) {
		
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
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/enroll_or_remove_student")
	public ModelAndView connectStudentWithLecture(HttpServletRequest request) {
		
		Integer idToUpdate = Integer.parseInt((String) request.getParameter("idToUpdate"));
		StudentEntity student = studentRepository.findById(idToUpdate).get();
		
		LectureEntity selectedLecture = (LectureEntity) request.getSession().getAttribute("selectedLecture");
		
		if (student.isTakingLecture(selectedLecture.getId())) {
			
			GradeCardEntity gradeCard = gradeCardRepository.findByLectureAndStudent(selectedLecture, student).get();
			
			for (AbsenceEntity a : gradeCard.getAbsences())
				absenceRepository.delete(a);
			
			for (GradeEntity g : gradeCard.getGrades())
				gradeRepository.delete(g);
			
			student.getGradeCards().remove(gradeCard);
			studentRepository.save(student);

			gradeCardRepository.delete(gradeCard);
			
			request.setAttribute("updateSuccessMsg", "Brisanje uspelo!");
			
		} else {
			
			GradeCardEntity gradeCard = new GradeCardEntity();
			
			gradeCard.setLecture(selectedLecture);
			gradeCard.setStudent(student);
			gradeCard.setPresent(0);
			gradeCard.setAbsent(0);
			
			gradeCardRepository.save(gradeCard);

			student.getGradeCards().add(gradeCard);
			studentRepository.save(student);
			
			request.setAttribute("updateSuccessMsg", "Dodavanje uspelo!");
		}
		
		return new ModelAndView("/admin/lectures/list");
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/grade")
	public ModelAndView gradeStudentInLecture(HttpServletRequest request) {
		
		LectureEntity selectedLecture = (LectureEntity) request.getSession().getAttribute("selectedLecture");
		StudentEntity student = (StudentEntity) request.getSession().getAttribute("studentToGrade");
		
		Integer grade = Integer.parseInt((String) request.getParameter("grade_num"));
		
		System.out.println(student + " :::: " + grade);
		
		GradeCardEntity gradeCard = gradeCardRepository.findByLectureAndStudent(selectedLecture, student).get();
		
		// create new grade
		GradeEntity newGrade = new GradeEntity();
		newGrade.setDate(LocalDate.now());
		newGrade.setGrade(grade);
		newGrade.setGradeCard(gradeCard);
		gradeRepository.save(newGrade);
		
		gradeCard.getGrades().add(newGrade);
		gradeCardRepository.save(gradeCard);
		
		logger.info("Lecture #" + selectedLecture.getId() + " : student " + student.getUsername() + " graded " + grade);
		
		notifyParentOfGrade(student, selectedLecture, newGrade);

		return new ModelAndView("/" + request.getSession().getAttribute("role") + "/lectures/list");
	}
	
	
	// =-=-=-= UPDATE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/update")
	public ModelAndView update(HttpServletRequest request) {
		
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
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/delete")
	public ModelAndView delete(HttpServletRequest request) {
		
		Integer idToDelete = Integer.parseInt(request.getParameter("idToDelete"));
		LectureEntity lecture = lectureRepository.findById(idToDelete).get();
		
		for (SessionEntity s : lecture.getSessions())
			sessionRepository.delete(s);
		
		for (GradeCardEntity g : lecture.getGradeCards())
			gradeCardRepository.delete(g);
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + 
				String.format(" : deleted lecture %s held by %s %s.", lecture.getSubject().getName(), lecture.getTeacher().getFirstName(), lecture.getTeacher().getLastName()));
		
		request.setAttribute("deleteSuccessMsg", String.format("Predavanje predmeta %s koje je držao %s %s je uspešno obrisano!", 
				lecture.getSubject().getName(), lecture.getTeacher().getFirstName(), lecture.getTeacher().getLastName()));
		
		lectureRepository.delete(lecture);
		
		request.setAttribute("lectures", (List<LectureEntity>) lectureRepository.findAll());
		
		return new ModelAndView("/admin/lectures/list");
	}
	
	
	// =-=-=-= AUX
	
	
	private void notifyParentOfGrade(StudentEntity student, LectureEntity selectedLecture, GradeEntity newGrade) {
		
		EmailObject emailObject = new EmailObject();
		emailObject.setSubject(String.format("%s %s - nova ocena iz predmeta '%s'", 
				student.getLastName(), student.getFirstName(), 
				newGrade.getGradeCard().getLecture().getSubject().getName()));
		
		emailObject.setText(String.format("Vaše dete je danas ocenjeno ocenom %d.", newGrade.getGrade()));
		
		if (!student.getParents().isEmpty()) {
			
			Iterator<ParentEntity> value = student.getParents().iterator();
			
			while (value.hasNext()) {
				
				ParentEntity parent = value.next();
				
				emailObject.setTo(parent.getEmail());
				emailService.sendSimpleMessage(emailObject);
				
				logger.info("Lecture #" + selectedLecture.getId() + " : parent notified.");
	        }
			
		} else {
			
			logger.info("Lecture #" + selectedLecture.getId() + " : parent not found.");
		}
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
	
	
	private void setLecturesAsRequestAttribute(HttpServletRequest request) {
		
		String username = ((UserEntity) request.getSession().getAttribute("user")).getUsername();
		List<LectureEntity> lectures = null;
		
		if (request.getSession().getAttribute("role").equals("teacher"))
			lectures = StreamSupport
				.stream(lectureRepository.findAll().spliterator(), false)
				.filter(l -> l.getTeacher().getUsername().equals(username))
				.collect(Collectors.toList());
		else
			lectures = (List<LectureEntity>) lectureRepository.findAll();
		
		request.getSession().setAttribute("lectures", lectures);
	}
	
	
	private void setStudentsAsRequestAttributeForAdmin(HttpServletRequest request) {
		
		List<StudentEntity> students = ((List<StudentEntity>) studentRepository.findAll())
				.stream()
				.sorted(Comparator.comparingInt(StudentEntity::getId))
				.collect(Collectors.toList());
		
		request.setAttribute("students", students);
	}
	
	
	private void setStudentsAsRequestAttributeForTeacher(HttpServletRequest request, LectureEntity selectedLecture) {
		
		List<UserEntity> students = selectedLecture.getGradeCards().stream()
				.map(gc -> gc.getStudent())
				.sorted(Comparator.comparingInt(StudentEntity::getId))
				.collect(Collectors.toList());
		
		request.setAttribute("students", students);
	}
}
