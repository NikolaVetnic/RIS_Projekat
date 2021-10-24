package com.nv.schoolsystemproject.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;

public interface GradeCardRepository extends CrudRepository<GradeCardEntity, Integer> {
	
	Optional<GradeCardEntity> findByLectureAndStudent(LectureEntity lecture, StudentEntity student);
}
