package com.nv.schoolsystemproject.repositories;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.GradeEntity;

public interface GradeRepository extends CrudRepository<GradeEntity, Integer> {
	
}
