package com.nv.schoolsystemproject.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)			// if true, generated setters return this instead of void
@NoArgsConstructor
@Entity
@Table(name = "subjects")
public class SubjectEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name", nullable = false)
	@NotBlank(message = "Name must be provided.")
	@Size(min = 5, max = 25, message = "Username must be between {min} and {max} characters long.")
	private String name;
	
	@Column(name = "totalHours", nullable = false)
	@NotNull(message = "Total hours must be provided.")
	private double totalHours;
	
	@Column(name = "year_accredited", nullable = false)
	@NotNull(message = "Year accredited must be provided.")
	@Min(value = 1900, message = "Year accredited must be greater than {value}.")
	@Max(value = 2100, message = "Year accredited must be lesser than than {value}.")
	private Integer yearAccredited;

	@Version
	private Integer version;
	
	@JsonBackReference
	@OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	protected List<LectureEntity> lectures = new ArrayList<>();
}
