package com.nv.schoolsystemproject.repositories;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.SessionEntity;

public interface SessionRepository extends CrudRepository<SessionEntity, Integer> {

}
