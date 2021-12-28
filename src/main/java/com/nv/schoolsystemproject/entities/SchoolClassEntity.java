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
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Entity
@Table(name = "classes")
public class SchoolClassEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "class", nullable = false)
	@NotNull(message = "Class number must be provided.")
	@Min(value = 1, message = "Class number must be greater than {value}.")
	@Max(value = 8, message = "Class number must be lesser than than {value}.")
	private Integer classNo;

	@Column(name = "section", nullable = false)
	@NotNull(message = "Section number must be provided.")
	@Min(value = 1, message = "Section number must be greater than {value}.")
	@Max(value = 8, message = "Section number must be lesser than than {value}.")
	private Integer sectionNo;
	
	@Column(name = "generation", nullable = false)
	@NotNull(message = "Generation must be provided.")
	@Min(value = 1900, message = "Generation must be greater than {value}.")
	@Max(value = 2100, message = "Generation must be lesser than than {value}.")
	private Integer generation;

	@Version
	private Integer version;
	
	@JsonBackReference
	@OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	protected List<StudentEntity> students = new ArrayList<>();
	
	public String getFormattedString() {
		
		String c = 	classNo == 1 ? "I" 		:
					classNo == 2 ? "II" 	:
					classNo == 3 ? "III" 	:
					classNo == 4 ? "IV" 	:
					classNo == 5 ? "V" 		:
					classNo == 6 ? "VI" 	:
					classNo == 7 ? "VII" 	: "VIII";
		
		return c + "-" + sectionNo + " (" + generation + ")";
	}
}
