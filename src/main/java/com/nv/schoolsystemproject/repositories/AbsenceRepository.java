package com.nv.schoolsystemproject.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nv.schoolsystemproject.entities.AbsenceEntity;
import com.nv.schoolsystemproject.entities.GradeCardEntity;

public interface AbsenceRepository extends CrudRepository<AbsenceEntity, Integer> {

	Optional<AbsenceEntity> findByGradeCardAndDate(GradeCardEntity gradeCard, LocalDate date);
}
