package com.nv.schoolsystemproject.controllers;

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

import com.nv.schoolsystemproject.entities.AdminEntity;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.TeacherEntity;
import com.nv.schoolsystemproject.repositories.AdminRepository;
import com.nv.schoolsystemproject.repositories.ParentRepository;
import com.nv.schoolsystemproject.repositories.StudentRepository;
import com.nv.schoolsystemproject.repositories.TeacherRepository;
import com.nv.schoolsystemproject.repositories.UserRepository;
import com.nv.schoolsystemproject.services.UserServiceImpl;
import com.nv.schoolsystemproject.utils.Encryption;
import com.nv.schoolsystemproject.utils.UserCustomValidator;

@RestController
@RequestMapping(path = "/api/v1/project/update")
public class UserUpdateController {
	
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired AdminRepository adminRepository;
	@Autowired ParentRepository parentRepository;
	@Autowired StudentRepository studentRepository;
	@Autowired TeacherRepository teacherRepository;
	@Autowired UserRepository userRepository;
	
	@Autowired private UserServiceImpl userServiceImpl;
	
	@Autowired UserCustomValidator userValidator;

	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(userValidator);
	}
	
	
	// =-=-=-= GET : ROUTES
	
	
	@RequestMapping(path = "/update_admin", method = RequestMethod.GET) 
	public ModelAndView getUpdateAdmin(HttpServletRequest request) {
		
		Integer idToUpdate = Integer.parseInt((String) request.getParameter("idToUpdate"));
		AdminEntity adminToUpdate = adminRepository.findById(idToUpdate).get();
		
		request.getSession().setAttribute("adminToUpdate", adminToUpdate);
		
		return new ModelAndView("/admin/update/admin");
	}
	
	
	@RequestMapping(path = "/update_teacher", method = RequestMethod.GET) 
	public ModelAndView getUpdateTeacher(HttpServletRequest request) {

		System.out.println("getUpdateTeacher");
		
		Integer idToUpdate = Integer.parseInt((String) request.getParameter("idToUpdate"));
		TeacherEntity teacherToUpdate = teacherRepository.findById(idToUpdate).get();

		request.getSession().setAttribute("teacherToUpdate", teacherToUpdate);
		
		return new ModelAndView("/admin/update/teacher");
	}
	
	
	@RequestMapping(path = "/update_parent", method = RequestMethod.GET) 
	public ModelAndView getUpdateParent(HttpServletRequest request) {

		Integer idToUpdate = Integer.parseInt((String) request.getParameter("idToUpdate"));
		ParentEntity parentToUpdate = parentRepository.findById(idToUpdate).get();

		request.getSession().setAttribute("parentToUpdate", parentToUpdate);
		
		return new ModelAndView("/admin/update/parent");
	}
	
	
	@RequestMapping(path = "/update_student", method = RequestMethod.GET) 
	public ModelAndView getUpdateStudent(HttpServletRequest request) {

		Integer idToUpdate = Integer.parseInt((String) request.getParameter("idToUpdate"));
		StudentEntity studentToUpdate = studentRepository.findById(idToUpdate).get();

		request.getSession().setAttribute("studentToUpdate", studentToUpdate);
		
		return new ModelAndView("/admin/update/student");
	}
	
	
	// =-=-=-= POST
	

	@RequestMapping(method = RequestMethod.POST, value = "/admins")
	public ModelAndView updateAdmin(HttpServletRequest request) {
		
		AdminEntity adminEntity = (AdminEntity) request.getSession().getAttribute("adminToUpdate");
		
		ModelAndView mav = new ModelAndView("/admin/update/admin");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyUser(request, EUserRole.ADMIN, error);
		
		if (foundError) {
			request.setAttribute("adminUpdateMsg", error.toString());
			return mav;
		}
		
		adminEntity.setPassword(Encryption.getPassEncoded(request.getParameter("password")));
						
		userRepository.save(adminEntity);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated admin " + adminEntity.getUsername());
		request.setAttribute("adminUpdateSuccessMsg", 
				String.format("Admin %s je uspešno ažuriran!", adminEntity.getUsername()));
		
		return mav;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/teachers")
	public ModelAndView updateTeacher(HttpServletRequest request) {

		TeacherEntity teacherEntity = (TeacherEntity) request.getSession().getAttribute("teacherToUpdate");
		
		ModelAndView mav = new ModelAndView("/admin/update/teacher");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyUser(request, EUserRole.TEACHER, error);
		
		if (foundError) {
			request.setAttribute("teacherUpdateMsg", error.toString());
			return mav;
		}
		
		teacherEntity.setFirstName(request.getParameter("firstName"));
		teacherEntity.setLastName(request.getParameter("lastName"));
		teacherEntity.setPassword(Encryption.getPassEncoded(request.getParameter("password")));
						
		userRepository.save(teacherEntity);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated teacher " + teacherEntity.getUsername());
		request.setAttribute("teacherUpdateSuccessMsg", 
				String.format("Nastavnik %s %s je uspešno ažuriran!", 
						teacherEntity.getFirstName(), teacherEntity.getLastName()));
		
		return mav;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/parents")
	public ModelAndView updateParent(HttpServletRequest request) {
		
		ParentEntity parentEntity = (ParentEntity) request.getSession().getAttribute("parentToUpdate");
		
		ModelAndView mav = new ModelAndView("/admin/update/parent");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyUser(request, EUserRole.PARENT, error);
		
		if (foundError) {
			request.setAttribute("parentUpdateMsg", error.toString());
			return mav;
		}
		
		parentEntity.setFirstName(request.getParameter("firstName"));
		parentEntity.setLastName(request.getParameter("lastName"));
		parentEntity.setPassword(Encryption.getPassEncoded(request.getParameter("password")));
						
		userRepository.save(parentEntity);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated parent " + parentEntity.getUsername());
		request.setAttribute("parentUpdateSuccessMsg", 
				String.format("Roditelj %s %s je uspešno ažuriran!", 
						parentEntity.getFirstName(), parentEntity.getLastName()));
		
		return mav;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/students")
	public ModelAndView updateStudent(HttpServletRequest request) {

		StudentEntity studentEntity = (StudentEntity) request.getSession().getAttribute("studentToUpdate");
		
		ModelAndView mav = new ModelAndView("/admin/update/student");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyUser(request, EUserRole.PARENT, error);
		
		if (foundError) {
			request.setAttribute("studentUpdateMsg", error.toString());
			return mav;
		}
		
		studentEntity.setFirstName(request.getParameter("firstName"));
		studentEntity.setLastName(request.getParameter("lastName"));
		studentEntity.setPassword(Encryption.getPassEncoded(request.getParameter("password")));
						
		userRepository.save(studentEntity);
		
		logger.info(userServiceImpl.getLoggedInUsername() + " : updated student " + studentEntity.getUsername());
		request.setAttribute("studentUpdateSuccessMsg", 
				String.format("Učenik %s %s je uspešno ažuriran!", 
						studentEntity.getFirstName(), studentEntity.getLastName()));
		
		return mav;
	}
	
	
	// =-=-=-= AUX
	
	
	private boolean verifyUser(HttpServletRequest request, EUserRole role, StringBuilder error) {
		
		boolean foundError = false;
		
		// firstName
		if (role == EUserRole.PARENT || role == EUserRole.TEACHER || role == EUserRole.STUDENT) {
			String firstName = request.getParameter("firstName");
			if ("".equals(firstName)) {
				error.append("Ime mora biti uneto. ");
				foundError = true;
			}
		}
		
		// lastName
		if (role == EUserRole.PARENT || role == EUserRole.TEACHER || role == EUserRole.STUDENT) {
			String lastName = request.getParameter("lastName");
			if ("".equals(lastName)) {
				error.append("Prezime mora biti uneto. ");
				foundError = true;
			}
		}
		
		// password
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		if ("".equals(password)) {
			error.append("Lozinka mora biti uneta. ");
			foundError = true;
		} 
		if ("".equals(confirmPassword)) {
			error.append("Ponovljena lozinka mora biti uneta. ");
			foundError = true;
		} 
		if (password != null && !password.equals(confirmPassword)) {
			error.append("Unete lozinke moraju biti identične.");
			foundError = true;
		}
		
		return foundError;
	}
}
