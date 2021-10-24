package com.nv.schoolsystemproject.controllers.factory;

import com.nv.schoolsystemproject.controllers.dto.ParentRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.StudentRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.TeacherRegisterDTO;
import com.nv.schoolsystemproject.controllers.dto.UserRegisterDTO;
import com.nv.schoolsystemproject.entities.AdminEntity;
import com.nv.schoolsystemproject.entities.EUserRole;
import com.nv.schoolsystemproject.entities.ParentEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;
import com.nv.schoolsystemproject.entities.TeacherEntity;
import com.nv.schoolsystemproject.entities.UserEntity;

public class UserFactory {

	
	public static UserEntity createUser(UserRegisterDTO userDTO) {
		
		if (userDTO.getRole() == EUserRole.ADMIN) {
			
			AdminEntity admin = new AdminEntity();
			
			admin.setUsername(userDTO.getUsername());
			admin.setPassword(userDTO.getPassword());
			admin.setRole(userDTO.getRole());
			
			return admin;
			
		} else if (userDTO.getRole() == EUserRole.TEACHER) {
			
			TeacherRegisterDTO teacherDTO = (TeacherRegisterDTO) userDTO;
			TeacherEntity teacher = new TeacherEntity();
			
			teacher.setFirstName(teacherDTO.getFirstName());
			teacher.setLastName(teacherDTO.getLastName());
			teacher.setEmail(teacherDTO.getEmail());
			teacher.setUsername(teacherDTO.getUsername());
			teacher.setPassword(teacherDTO.getPassword());
			teacher.setRole(teacherDTO.getRole());
			
			return teacher;
			
		} else if (userDTO.getRole() == EUserRole.PARENT) {
			
			ParentRegisterDTO parentDTO = (ParentRegisterDTO) userDTO;
			ParentEntity parent = new ParentEntity();
			
			parent.setFirstName(parentDTO.getFirstName());
			parent.setLastName(parentDTO.getLastName());
			parent.setEmail(parentDTO.getEmail());
			parent.setUsername(parentDTO.getUsername());
			parent.setPassword(parentDTO.getPassword());
			parent.setRole(parentDTO.getRole());
			
			return parent;
			
		} else {
			
			StudentRegisterDTO studentDTO = (StudentRegisterDTO) userDTO;
			StudentEntity student = new StudentEntity();
			
			student.setFirstName(studentDTO.getFirstName());
			student.setLastName(studentDTO.getLastName());
			student.setJmbg(studentDTO.getJmbg());
			student.setUsername(studentDTO.getUsername());
			student.setPassword(studentDTO.getPassword());
			student.setRole(studentDTO.getRole());
			
			return student;
		}
	}
}
