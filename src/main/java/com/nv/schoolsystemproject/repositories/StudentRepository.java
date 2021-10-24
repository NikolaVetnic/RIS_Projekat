package com.nv.schoolsystemproject.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.StudentEntity;

public interface StudentRepository extends CrudRepository<StudentEntity, Integer> {
	
	Optional<StudentEntity> findByJmbg(String jmbg);
}
