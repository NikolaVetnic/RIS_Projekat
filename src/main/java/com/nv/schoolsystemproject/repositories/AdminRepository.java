package com.nv.schoolsystemproject.repositories;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.AdminEntity;

public interface AdminRepository extends CrudRepository<AdminEntity, Integer> {
	
}
