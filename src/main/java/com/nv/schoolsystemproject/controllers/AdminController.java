package com.nv.schoolsystemproject.controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.entities.AdminEntity;
import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.GradeEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.SubjectEntity;
import com.nv.schoolsystemproject.entities.TeacherEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.AdminRepository;
import com.nv.schoolsystemproject.repositories.GradeCardRepository;
import com.nv.schoolsystemproject.repositories.GradeRepository;
import com.nv.schoolsystemproject.repositories.LectureRepository;
import com.nv.schoolsystemproject.repositories.ParentRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.repositories.SubjectRepository;
import com.nv.schoolsystemproject.repositories.TeacherRepository;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.utils.UserCustomValidator;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RestController
@RequestMapping(path = "/api/v1/project/admin")
public class AdminController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired private AdminRepository adminRepository;
	@Autowired private GradeRepository gradeRepository;
	@Autowired private GradeCardRepository gradeCardRepository;
	@Autowired private LectureRepository lectureRepository;
	@Autowired private ParentRepository parentRepository;
	@Autowired private StudentRepository studentRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private TeacherRepository teacherRepository;
	@Autowired private UserRepository userRepository;
	
	@Autowired UserCustomValidator userValidator;
	
	
	// =-=-=-= GET =-=-=-=
	
	@RequestMapping(path = "/home", method = RequestMethod.GET) 
	public ModelAndView getHome(HttpServletRequest request) {
		return new ModelAndView("/admin/home");
	}
	
	
	@RequestMapping(path = "/users", method = RequestMethod.GET) 
	public ModelAndView getAllUsers(HttpServletRequest request) {
		return getModelAndViewToUsers(request);
	}
	
	
	@RequestMapping(path = "/logs", method = RequestMethod.GET)
	public ModelAndView getLogs(HttpServletRequest request) throws IOException {
		
		BufferedReader in = new BufferedReader(
				new FileReader("logs//spring-boot-logging.log"));
		
		List<String> lines = new ArrayList<String>();		
		String line = null;

		while ((line = in.readLine()) != null)
			lines.add(line);
		
		in.close();
		
		request.setAttribute("logs", lines);
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + " : viewed logs.");
		
		return new ModelAndView("/admin/logs");
	}
	
	
	// =-=-=-= REPORT
	
	
	@RequestMapping(path = "/all_students_report", method = RequestMethod.GET)
	public void getAllStudentsReport(HttpServletResponse response) throws Exception{
		
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(studentRepository.findAllByOrderBySchoolClass());
		InputStream inputStream = this.getClass().getResourceAsStream("/jasperreports/SviUceniciReport.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("imeSkole", "Moja škola");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
		inputStream.close();
		
		response.setContentType("application/x-download");
		response.addHeader("Content-disposition", "attachment; filename=SviUcenici.pdf");
		
		OutputStream out = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint,out);
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/delete")
	public ModelAndView delete(HttpServletRequest request) {
		
		Integer idToDelete = Integer.parseInt(request.getParameter("idToDelete"));
		UserEntity userEntity = userRepository.findById(idToDelete).get();
		
		switch (userEntity.getRole()) {
			case ADMIN: 	deleteAdmin		(idToDelete, request); break;
			case PARENT: 	deleteParent	(idToDelete, request); break;
			case STUDENT: 	deleteStudent	(idToDelete, request); break;
			case TEACHER: 	deleteTeacher	(idToDelete, request); break;
		}
		
		return getModelAndViewToUsers(request);
	}
	
	
	private void deleteAdmin(Integer idToDelete, HttpServletRequest request) {
		
		// ne zelim da brisem nultog korisnika
		if (idToDelete == 0)
			return;
		
		AdminEntity admin = adminRepository.findById(idToDelete).get();
		adminRepository.delete(admin);
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + 
				" : deleted admin " + admin.getUsername());
		
		request.setAttribute("deleteSuccessMsg", String.format("Admin %s je uspešno obrisan!", 
				admin.getUsername()));
	}
	
	
	private void deleteParent(Integer idToDelete, HttpServletRequest request) {
		
		ParentEntity parent = parentRepository.findById(idToDelete).get();
		
		for (StudentEntity s : parent.getStudents()) {
			
			s.getParents().remove(parent);
			studentRepository.save(s);
		}
		
		parentRepository.delete(parent);
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + 
				" : deleted parent " + parent.getUsername());
		
		request.setAttribute("deleteSuccessMsg", String.format("Roditelj %s %s je uspešno obrisan!", 
				parent.getFirstName(), parent.getLastName()));
	}
	
	
	private void deleteStudent(Integer idToDelete, HttpServletRequest request) {
		
		StudentEntity student = studentRepository.findById(idToDelete).get();
		
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
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + 
				" : deleted student " + student.getUsername());
		
		request.setAttribute("deleteSuccessMsg", String.format("Učenik %s %s je uspešno obrisan!", 
				student.getFirstName(), student.getLastName()));
	}
	
	
	private void deleteTeacher(Integer idToDelete, HttpServletRequest request) {
		
		TeacherEntity teacher = teacherRepository.findById(idToDelete).get();
		
		for (LectureEntity l : teacher.getLectures()) {
			
			SubjectEntity currSubject = l.getSubject();
			currSubject.getLectures().remove(l);
			subjectRepository.save(currSubject);
			
			Set<GradeCardEntity> currGC = l.getGradeCards();
			
			Iterator<GradeCardEntity> it = currGC.iterator();
			
			while(it.hasNext()){
				
				GradeCardEntity gc = it.next();
				
				gc.setLecture(null);
				gradeCardRepository.save(gc);
			}
			
			lectureRepository.delete(l);
		}
		
		teacherRepository.delete(teacher);
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + 
				" : deleted teacher " + teacher.getUsername());
		
		request.setAttribute("deleteSuccessMsg", String.format("Nastavnik %s %s je uspešno obrisan!", 
				teacher.getFirstName(), teacher.getLastName()));
	}
	
	
	// =-=-=-= AUX
	
	
	private ModelAndView getModelAndViewToUsers(HttpServletRequest request) {
		
		request.setAttribute("users", userRepository.findAllByOrderByRole());
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + " : viewed all users.");
		
		return new ModelAndView("/admin/users");
	}
}
