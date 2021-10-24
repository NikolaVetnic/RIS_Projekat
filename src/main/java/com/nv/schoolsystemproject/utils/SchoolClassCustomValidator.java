package com.nv.schoolsystemproject.utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.nv.schoolsystemproject.controllers.dto.SchoolClassRegisterDTO;
import com.nv.schoolsystemproject.entities.SchoolClassEntity;
import com.nv.schoolsystemproject.repositories.SchoolClassRepository;

@Component
public class SchoolClassCustomValidator implements Validator {
	
	@Autowired SchoolClassRepository schoolClassRepository;
	
	@Override
	public boolean supports(Class<?> myClass) {
		return SchoolClassRegisterDTO.class.equals(myClass);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		
		SchoolClassRegisterDTO schoolClassDTO = (SchoolClassRegisterDTO) target;
		
		Optional<SchoolClassEntity> result = schoolClassRepository.findByClassNoAndSectionNoAndGeneration(
				schoolClassDTO.getClassNo(), schoolClassDTO.getSectionNo(), schoolClassDTO.getGeneration());
		
		if (result.isPresent())
			errors.reject("400", "Such class already exists.");
	}
}
