package com.nv.schoolsystemproject.entities;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nv.schoolsystemproject.utils.MathUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Entity
@Table(name = "grade_cards")
public class GradeCardEntity implements Comparable<GradeCardEntity> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "present", nullable = false)
	@NotNull(message = "Present must be provided.")
	@Min(value = 0, message = "Present must be greater than {value}.")
	private Integer present;
	
	@Column(name = "absent", nullable = false)
	@NotNull(message = "Absent must be provided.")
	@Min(value = 0, message = "Absent must be greater than {value}.")
	private Integer absent;
	
	@JsonManagedReference
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture")
	private LectureEntity lecture;
	
	@JsonManagedReference
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "student")
	private StudentEntity student;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "gradeCard", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@OrderBy("grade")
	protected Set<GradeEntity> grades = new TreeSet<>();
	
	@JsonManagedReference
	@OneToMany(mappedBy = "gradeCard", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@OrderBy("date")
	protected Set<AbsenceEntity> absences = new TreeSet<>();

	@Version
	private Integer version;

	@Override
	public int compareTo(GradeCardEntity o) {
		
		String l0 = student.getLastName();
		String l1 = o.getStudent().getLastName();
		
		String f0 = student.getFirstName();
		String f1 = o.getStudent().getFirstName();
		
		return l0.compareTo(l1) != 0 ? l0.compareTo(l1) : f0.compareTo(f1);
	}
	
	public double average() {
		return MathUtils.round(grades.stream()
				.mapToDouble(GradeEntity::getGrade)
				.average().orElse(0.0), 1);
	}
}
