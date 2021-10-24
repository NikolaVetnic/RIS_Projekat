package com.nv.schoolsystemproject.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.nv.schoolsystemproject.controllers.dto.AdminRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.ParentRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.StudentRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.TeacherRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.UserRegisterDTO;

@Component
public class UserCustomValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> myClass) {
		return UserRegisterDTO.class.equals(myClass) 	||
			   AdminRegisterDTO.class.equals(myClass)	||
			   TeacherRegisterDTO.class.equals(myClass) ||
			   ParentRegisterDTO.class.equals(myClass)	||
			   StudentRegisterDTO.class.equals(myClass)	;
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		
		UserRegisterDTO user = (UserRegisterDTO) target;
		
		if (user.getPassword() != null && !user.getPassword().equals(user.getConfirmPassword()))
			errors.reject("400", "Passwords must be the same.");
	}
}
