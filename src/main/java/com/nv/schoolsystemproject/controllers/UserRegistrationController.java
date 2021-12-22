package com.nv.schoolsystemproject.controllers;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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

import com.nv.schoolsystemproject.controllers.dto.AdminRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.ParentRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.StudentRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.TeacherRegisterDTO;
import com.nv.schoolsystemproject.controllers.factory.UserFactory;
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
@RequestMapping(path = "/api/v1/project/registration")
public class UserRegistrationController {
	
	
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
	
	
	@RequestMapping(path = "/home", method = RequestMethod.GET) 
	public ModelAndView getRegistrationHome(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/register/home");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/register_admin", method = RequestMethod.GET) 
	public ModelAndView getRegisterAdmin(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/register/admin");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/register_teacher", method = RequestMethod.GET) 
	public ModelAndView getRegisterTeacher(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/register/teacher");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/register_parent", method = RequestMethod.GET) 
	public ModelAndView getRegisterParent(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/register/parent");
		
		return mav;
	}
	
	
	@RequestMapping(path = "/register_student", method = RequestMethod.GET) 
	public ModelAndView getRegisterStudent(HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView("/admin/register/student");
		
		return mav;
	}
	
	
	// =-=-=-= POST
	

	@RequestMapping(method = RequestMethod.POST, value = "/admins")
	public ModelAndView addNewAdmin(
//			@Valid @RequestBody AdminRegisterDTO adminDTO, BindingResult result
			HttpServletRequest request
			) {
		
		AdminRegisterDTO adminDTO = new AdminRegisterDTO();
		ModelAndView mav = new ModelAndView("/admin/register/admin");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyUser(request, EUserRole.ADMIN, error);
		
		if (foundError) {
			request.setAttribute("adminRegisterMsg", error.toString());
			return mav;
		}
		
		adminDTO.setUsername(request.getParameter("username"));
		adminDTO.setPassword(Encryption.getPassEncoded(request.getParameter("password")));
		adminDTO.setConfirmPassword(Encryption.getPassEncoded(request.getParameter("confirmPassword")));
		adminDTO.setRole(EUserRole.ADMIN);
						
		AdminEntity admin = (AdminEntity) UserFactory.createUser(adminDTO);
		userRepository.save(admin);
		
		logger.info(admin.toString() + " : created.");
		request.setAttribute("adminRegisterSuccessMsg", "Admin je uspešno registrovan!");
		
		return mav;

//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
//		
//		if (result.hasErrors())
//			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
//		else
//			userValidator.validate(adminDTO, result);
//		
//		AdminEntity admin = (AdminEntity) UserFactory.createUser(adminDTO);
//		userRepository.save(admin);
//		
//		logger.info(admin.toString() + " : created.");
//		
//		return new ResponseEntity<>(admin, HttpStatus.OK);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/teachers")
	public ModelAndView addNewTeacher(
//			@Valid @RequestBody TeacherRegisterDTO teacherDTO, BindingResult result,
			HttpServletRequest request
			) {
		
		TeacherRegisterDTO teacherDTO = new TeacherRegisterDTO();
		ModelAndView mav = new ModelAndView("/admin/register/teacher");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyUser(request, EUserRole.TEACHER, error);
		
		if (foundError) {
			request.setAttribute("teacherRegisterMsg", error.toString());
			return mav;
		}
		
		teacherDTO.setFirstName(request.getParameter("firstName"));
		teacherDTO.setLastName(request.getParameter("lastName"));
		teacherDTO.setEmail(request.getParameter("email"));
		teacherDTO.setUsername(request.getParameter("username"));
		teacherDTO.setPassword(Encryption.getPassEncoded(request.getParameter("password")));
		teacherDTO.setConfirmPassword(Encryption.getPassEncoded(request.getParameter("password")));
		teacherDTO.setRole(EUserRole.TEACHER);
						
		TeacherEntity teacher = (TeacherEntity) UserFactory.createUser(teacherDTO);
		userRepository.save(teacher);
		
		logger.info(teacher.toString() + " : created.");
		request.setAttribute("teacherRegisterSuccessMsg", "Nastavnik je uspešno registrovan!");
		
		return mav;
		
//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
//
//		if (teacherRepository.findByEmail(teacherDTO.getEmail()).isPresent())
//			return new ResponseEntity<>("Email must be unique.", HttpStatus.BAD_REQUEST);
//		else if (result.hasErrors())
//			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
//		else
//			userValidator.validate(teacherDTO, result);
//			
//		teacherDTO.setRole(EUserRole.TEACHER);
//		
//		TeacherEntity teacher = (TeacherEntity) UserFactory.createUser(teacherDTO);
//		userRepository.save(teacher);
//		
//		logger.info(teacher.toString() + " : created.");
//		
//		return new ResponseEntity<>(teacher, HttpStatus.OK);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/parents")
	public ModelAndView addNewParent(
//			@Valid @RequestBody ParentRegisterDTO parentDTO, 
//			BindingResult result,
			HttpServletRequest request) {
		
		ParentRegisterDTO parentDTO = new ParentRegisterDTO();
		ModelAndView mav = new ModelAndView("/admin/register/parent");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyUser(request, EUserRole.PARENT, error);
		
		if (foundError) {
			request.setAttribute("parentRegisterMsg", error.toString());
			return mav;
		}
		
		parentDTO.setFirstName(request.getParameter("firstName"));
		parentDTO.setLastName(request.getParameter("lastName"));
		parentDTO.setEmail(request.getParameter("email"));
		parentDTO.setUsername(request.getParameter("username"));
		parentDTO.setPassword(Encryption.getPassEncoded(request.getParameter("password")));
		parentDTO.setConfirmPassword(Encryption.getPassEncoded(request.getParameter("password")));
		parentDTO.setRole(EUserRole.PARENT);
						
		ParentEntity parent = (ParentEntity) UserFactory.createUser(parentDTO);
		userRepository.save(parent);
		
		logger.info(parent.toString() + " : created.");
		request.setAttribute("parentRegisterSuccessMsg", "Roditelj je uspešno registrovan!");
		
		return mav;

//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
//
//		if (parentRepository.findByEmail(parentDTO.getEmail()).isPresent())
//			return new ResponseEntity<>("Email must be unique.", HttpStatus.BAD_REQUEST);
//		else if (result.hasErrors())
//			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
//		else
//			userValidator.validate(parentDTO, result);
//		
//		ParentEntity parent = (ParentEntity) UserFactory.createUser(parentDTO);
//		userRepository.save(parent);
//		
//		logger.info(parent.toString() + " : created.");
//		
//		return new ResponseEntity<>(parent, HttpStatus.OK);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/students")
	public ModelAndView addNewStudent(
//			@Valid @RequestBody StudentRegisterDTO studentDTO, BindingResult result
			HttpServletRequest request) {


		StudentRegisterDTO studentDTO = new StudentRegisterDTO();
		ModelAndView mav = new ModelAndView("/admin/register/student");
		
		StringBuilder error = new StringBuilder();
		boolean foundError = verifyUser(request, EUserRole.STUDENT, error);
		
		if (foundError) {
			request.setAttribute("studentRegisterMsg", error.toString());
			return mav;
		}
		
		studentDTO.setFirstName(request.getParameter("firstName"));
		studentDTO.setLastName(request.getParameter("lastName"));
		studentDTO.setJmbg(request.getParameter("jmbg"));
		studentDTO.setUsername(request.getParameter("username"));
		studentDTO.setPassword(Encryption.getPassEncoded(request.getParameter("password")));
		studentDTO.setConfirmPassword(Encryption.getPassEncoded(request.getParameter("password")));
		studentDTO.setRole(EUserRole.STUDENT);
						
		StudentEntity student = (StudentEntity) UserFactory.createUser(studentDTO);
		userRepository.save(student);
		
		logger.info(student.toString() + " : created.");
		request.setAttribute("studentRegisterSuccessMsg", "Učenik je uspešno registrovan!");
		
		return mav;
		
//		if (!userServiceImpl.isAuthorizedAs(EUserRole.ADMIN))
//			return new ResponseEntity<RESTError>(
//					new RESTError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request."), HttpStatus.UNAUTHORIZED);
//
//		if (studentRepository.findByJmbg(studentDTO.getJmbg()).isPresent())
//			return new ResponseEntity<>("Personal ID number must be unique.", HttpStatus.BAD_REQUEST);
//		else if (result.hasErrors())
//			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
//		else
//			userValidator.validate(studentDTO, result);
//		
//		StudentEntity student = (StudentEntity) UserFactory.createUser(studentDTO);
//		userRepository.save(student);
//		
//		logger.info(student.toString() + " : created.");
//		
//		return new ResponseEntity<>(student, HttpStatus.OK);
	}
	

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
	}
	
	
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
		
		// email
		if (role == EUserRole.PARENT || role == EUserRole.TEACHER) {
			String email = request.getParameter("email");
			if ("".equals(email)) {
				error.append("Email mora biti unet. ");
				foundError = true;
			} else if (role == EUserRole.TEACHER && teacherRepository.findByEmail(email).isPresent()) {
				error.append("Email je zauzet. ");
				foundError = true;
			} else if (role == EUserRole.PARENT && parentRepository.findByEmail(email).isPresent()) {
				error.append("Email je zauzet. ");
				foundError = true;
			}
		}
		
		// jmbg
		if (role == EUserRole.STUDENT) {
			String jmbg = request.getParameter("jmbg");
			if ("".equals(jmbg)) {
				error.append("JMBG mora biti unet. ");
				foundError = true;
			}
		}
		
		// username
		String username = request.getParameter("username");
		if ("".equals(username)) {
			error.append("Korisničko ima mora biti uneto. ");
			foundError = true;
		} else if (username.length() < 5 || 15 < username.length()) {
			error.append("Korisničko ime mora biti dužine od 5 do 15 karaktera. ");
			foundError = true;
		} else if (userRepository.findByUsername(username).isPresent()) {
			error.append("Korisničko ime je zauzeto. ");
			foundError = true;
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
