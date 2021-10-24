package com.nv.schoolsystemproject.controllers.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class SchoolClassRegisterDTO {

	@NotNull(message = "Class number must be provided.")
	@Min(value = 1, message = "Class number must be greater than {value}.")
	@Max(value = 8, message = "Class number must be lesser than than {value}.")
	private Integer classNo;

	@NotNull(message = "Section number must be provided.")
	@Min(value = 1, message = "Section number must be greater than {value}.")
	@Max(value = 8, message = "Section number must be lesser than than {value}.")
	private Integer sectionNo;
	
	@NotNull(message = "Generation must be provided.")
	@Min(value = 1900, message = "Generation must be greater than {value}.")
	@Max(value = 2100, message = "Generation must be lesser than than {value}.")
	private Integer generation;
}
