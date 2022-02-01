package com.nv.schoolsystemproject.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nv.schoolsystemproject.entities.LectureEntity;
import com.nv.schoolsystemproject.entities.SchoolClassEntity;
import com.nv.schoolsystemproject.entities.StudentEntity;

public interface StudentRepository extends CrudRepository<StudentEntity, Integer> {
	
	Optional<StudentEntity> findByJmbg(String jmbg);
	
	List<StudentEntity> findAllByOrderById();
	
	@Query( "select s from StudentEntity s " +
			"order by s.schoolClass nulls last")
	List<StudentEntity> findAllByOrderBySchoolClass();
	
	@Query(	"select s from StudentEntity s " 			+
			"join s.gradeCards gc " 					+
			"where gc.lecture.teacher.username = :username")
	List<StudentEntity> findByTeacher(@Param("username") String username);
	
	@Query( "select s from StudentEntity s "			+
			"join s.parents p "							+
			"where p.username = :username")
	List<StudentEntity> findByParent(@Param("username") String username);
	
	@Query( "select s from StudentEntity s "			+
			"join s.gradeCards gc " 					+
			"where gc.lecture = :lecture")
	List<StudentEntity> findByLecture(@Param("lecture") LectureEntity lecture);
	
	@Query( "select s from StudentEntity s "			+
			"join s.gradeCards gc "						+
			"where gc.lecture = :lecture "		+
			"and not exists ("							+
			"	select a from AbsenceEntity a "			+
			"	where a.gradeCard = gc "				+
			"	and a.date = :date)")
	List<StudentEntity> findBySessionWithoutAbsence(@Param("lecture") LectureEntity lecture, @Param("date") LocalDate date);
	
	@Query( "select s from StudentEntity s "			+
			"where s.schoolClass = :schoolClass "		+
			"order by s.id")
	List<StudentEntity> findBySchoolClass(@Param("schoolClass") SchoolClassEntity schoolClass);
}
