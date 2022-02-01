package com.nv.schoolsystemproject.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nv.schoolsystemproject.entities.SchoolClassEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.UserEntity;
import com.nv.schoolsystemproject.repositories.SchoolClassRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.utils.SchoolClassCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/project/classes")
public class SchoolClassController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Autowired SchoolClassRepository schoolClassRepository;
	@Autowired StudentRepository studentRepository;
	
	@Autowired SchoolClassCustomValidator schoolClassValidator;

	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(schoolClassValidator);
	}
	
	
	// =-=-=-= GET : ROUTES
	
	
	@RequestMapping(path = "/list", method = RequestMethod.GET) 
	public ModelAndView getAllSchoolClasses(HttpServletRequest request) {
		
		request.setAttribute("schoolClasses", (List<SchoolClassEntity>) schoolClassRepository.findAll());
		
		return new ModelAndView("/admin/school_class/list");
	}
	
	
	@RequestMapping(path = "/all_students", method = RequestMethod.GET) 
	public ModelAndView getAllStudents(HttpServletRequest request) {
		
		SchoolClassEntity selectedSchoolClass = schoolClassRepository
				.findById(Integer.parseInt((String) request.getParameter("idToUpdate"))).get();
		
		request.getSession().setAttribute("selectedSchoolClass", selectedSchoolClass);
		request.setAttribute("students", studentRepository.findAllByOrderBySchoolClass());
		
		return new ModelAndView("/admin/school_class/students");
	}
	
	
	@RequestMapping(path = "/class_students", method = RequestMethod.GET) 
	public ModelAndView getClassStudents(HttpServletRequest request) {
		
		SchoolClassEntity selectedSchoolClass = schoolClassRepository
				.findById(Integer.parseInt((String) request.getParameter("idToUpdate"))).get();
		
		request.getSession().setAttribute("selectedSchoolClass", selectedSchoolClass);
		request.getSession().setAttribute("students", studentRepository.findBySchoolClass(selectedSchoolClass));
		
		return new ModelAndView("/admin/school_class/class_students");
	}
	
	
	@RequestMapping(path = "/register_school_class", method = RequestMethod.GET) 
	public ModelAndView getRegistrationHome(HttpServletRequest request) {
		
		request.getSession().setAttribute("intArray8", new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
		request.getSession().setAttribute("generationRange", IntStream.range(2020, 2050).boxed().collect(Collectors.toList()));
		
		return new ModelAndView("/admin/school_class/register");
	}
	
	
	// =-=-=-= POST =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/register")
	public ModelAndView addNewSchoolClass(HttpServletRequest request) {
		
		SchoolClassEntity schoolClass = new SchoolClassEntity();
		ModelAndView mav = new ModelAndView("/admin/school_class/register");

		StringBuilder error = new StringBuilder();
		boolean foundError = verifySchoolClass(request, error);
		
		if (foundError) {
			request.setAttribute("schoolClassRegisterMsg", error.toString());
			return mav;
		}
		
		schoolClass.setClassNo(Integer.parseInt(request.getParameter("classNo")));
		schoolClass.setSectionNo(Integer.parseInt(request.getParameter("sectionNo")));
		schoolClass.setGeneration(Integer.parseInt(request.getParameter("generation")));
		
		schoolClassRepository.save(schoolClass);
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + " : registered class " + 
				schoolClass.getFormattedString());
		request.setAttribute("schoolClassRegisterSuccessMsg", String.format("Odelenje %s je uspešno registrovano!", 
				schoolClass.getFormattedString()));
		
		return mav;
	}
	
	
	// =-=-=-= PUT =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/add_to_class")
	public ModelAndView connectStudentWithClass(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/school_class/students");
		
		SchoolClassEntity schoolClass = (SchoolClassEntity) request.getSession().getAttribute("selectedSchoolClass");
		StudentEntity student = (StudentEntity) studentRepository.findById(Integer.parseInt((String) request.getParameter("idToUpdate"))).get();
		
		student.setSchoolClass(schoolClass);
		studentRepository.save(student);
		
		request.setAttribute("students", studentRepository.findAllByOrderBySchoolClass());
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + " : added student " + 
				student.getUsername() + " to class " + schoolClass.getFormattedString());
		request.setAttribute("updateSuccessMsg", String.format("Učenik %s %s je uspešno dodat u odeljenje %s!", 
				student.getFirstName(), student.getLastName(), schoolClass.getFormattedString()));
		
		return mav;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/remove_from_class")
	public ModelAndView removeStudentFromClass(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/school_class/students");
		
		StudentEntity student = (StudentEntity) studentRepository.findById(
				Integer.parseInt((String) request.getParameter("idToUpdate"))).get();
		
		if (student.getSchoolClass() == null) {
			
			request.setAttribute("updateErrorMsg", "Učenik nije u odelenju!");
			request.setAttribute("students", studentRepository.findAllByOrderBySchoolClass());
			
			return mav;
		}
		
		SchoolClassEntity schoolClass = student.getSchoolClass();
		
		student.setSchoolClass(null);
		studentRepository.save(student);
		
		request.setAttribute("students", studentRepository.findAllByOrderBySchoolClass());
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + " : removed student " + 
				student.getUsername() + " from class " + schoolClass.getFormattedString());
		request.setAttribute("updateSuccessMsg", String.format("Učenik %s %s je uspešno uklonjen iz odeljenja %s!", 
				student.getFirstName(), student.getLastName(), schoolClass.getFormattedString()));
		
		return mav;
	}
	
	
	// =-=-=-= DELETE =-=-=-=
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/delete")
	public ModelAndView delete(HttpServletRequest request) {
		
		Integer idToDelete = Integer.parseInt(request.getParameter("idToDelete"));
		SchoolClassEntity schoolClass = schoolClassRepository.findById(idToDelete).get();
		
		for (StudentEntity s : schoolClass.getStudents())
			s.setSchoolClass(null);
		
		logger.info(((UserEntity) request.getSession().getAttribute("user")).getUsername() + 
				String.format(" : deleted school class %s.", schoolClass.getFormattedString()));
		
		request.setAttribute("deleteSuccessMsg", String.format("Razred %s je uspešno obrisan!", schoolClass.getFormattedString()));
		
		schoolClassRepository.delete(schoolClass);
		
		request.setAttribute("schoolClasses", (List<SchoolClassEntity>) schoolClassRepository.findAll());
		
		return new ModelAndView("/admin/school_class/list");
	}
	
	
	// =-=-=-= AUX
	
	
	private boolean verifySchoolClass(HttpServletRequest request, StringBuilder error) {
		
		boolean foundError = false;
		
		try {
			Integer.parseInt(request.getParameter("classNo"));
		} catch (NumberFormatException e) {
			error.append("Morate odabrati razred. ");
			foundError = true;
		}
		
		try {
			Integer.parseInt(request.getParameter("sectionNo"));
		} catch (NumberFormatException e) {
			error.append("Morate odabrati odelenje. ");
			foundError = true;
		}
		
		try {
			Integer.parseInt(request.getParameter("generation"));
		} catch (NumberFormatException e) {
			error.append("Morate odabrati generaciju. ");
			foundError = true;
		}
		
		return foundError;
	}
}
