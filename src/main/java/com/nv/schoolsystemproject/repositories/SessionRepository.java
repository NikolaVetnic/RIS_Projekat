package com.nv.schoolsystemproject.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.SessionEntity;

public interface SessionRepository extends CrudRepository<SessionEntity, Integer> {

	Optional<SessionEntity> findByDate(LocalDate date);
	
	List<SessionEntity> findAllByOrderByDate();
	
	Optional<SessionEntity> findByLectureAndDate(LectureEntity lecture, LocalDate date);
	
	List<SessionEntity> findByLectureOrderByDate(LectureEntity lecture);
}
