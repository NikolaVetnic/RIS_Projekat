package com.nv.schoolsystemproject.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.SchoolClassEntity;

public interface SchoolClassRepository extends CrudRepository<SchoolClassEntity, Integer> {
	
	Optional<SchoolClassEntity> findByClassNoAndSectionNoAndGeneration(
			Integer classNo, Integer sectionNo, Integer generation);
}
