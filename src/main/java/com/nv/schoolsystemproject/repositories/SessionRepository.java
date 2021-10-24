package com.nv.schoolsystemproject.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.SessionEntity;

public interface SessionRepository extends CrudRepository<SessionEntity, Integer> {

	Optional<SessionEntity> findByDate(LocalDate date);
}
