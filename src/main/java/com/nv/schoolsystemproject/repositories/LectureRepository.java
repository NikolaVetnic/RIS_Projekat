package com.nv.schoolsystemproject.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.SubjectEntity;
import com.nv.schoolsystemproject.entities.TeacherEntity;

public interface LectureRepository extends CrudRepository<LectureEntity, Integer> {
	
	Optional<LectureEntity> findBySubjectAndTeacher(SubjectEntity subject, TeacherEntity teacher);
}
