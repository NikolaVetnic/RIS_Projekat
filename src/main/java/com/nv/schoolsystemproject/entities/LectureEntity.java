package com.nv.schoolsystemproject.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Entity
@Table(name = "lectures")
public class LectureEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "year", nullable = false)
	@NotNull(message = "Year must be provided.")
	@Min(value = 1900, message = "Year must be greater than {value}.")
	@Max(value = 2100, message = "Year must be lesser than than {value}.")
	private Integer year;
	
	@Column(name = "semester")
	@Enumerated(EnumType.STRING)
	private ESemester semester;
	
	@JsonManagedReference
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher")
	private TeacherEntity teacher;
	
	@JsonManagedReference
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "subject")
	private SubjectEntity subject;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	protected List<SessionEntity> sessions = new ArrayList<>();
	
	@JsonBackReference
	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	protected Set<GradeCardEntity> gradeCards = new TreeSet<>();

	@Version
	private Integer version;
}
