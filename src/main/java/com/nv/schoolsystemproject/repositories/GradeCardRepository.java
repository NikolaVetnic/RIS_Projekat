package com.nv.schoolsystemproject.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nv.schoolsystemproject.entities.GradeCardEntity;
import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;

public interface GradeCardRepository extends CrudRepository<GradeCardEntity, Integer> {
	
	Optional<GradeCardEntity> findByLectureAndStudent(LectureEntity lecture, StudentEntity student);
	
	@Query( "select gc from GradeCardEntity gc "		+
			"where gc.student = :student "				+
			"and gc.lecture.teacher.username = :teacherUsername")
	List<GradeCardEntity> findAllByStudentAndTeacher(
			@Param("student") StudentEntity student, @Param("teacherUsername") String teacherUsername);
}
