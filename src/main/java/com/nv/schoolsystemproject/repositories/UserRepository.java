package com.nv.schoolsystemproject.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
	
	List<UserEntity> findAllByOrderByRole();
	Optional<UserEntity> findByUsername(String username);
}
