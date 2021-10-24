package com.nv.schoolsystemproject.controllers.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)			// if true, generated setters return this instead of void
@NoArgsConstructor
public class SubjectRegisterDTO {
	
	@NotBlank(message = "Name must be provided.")
	@Size(min = 5, max = 25, message = "Username must be between {min} and {max} characters long.")
	private String name;
	
	@NotNull(message = "Total hours must be provided.")
	private double totalHours;
	
	@NotNull(message = "Year accredited must be provided.")
	@Min(value = 1900, message = "Year accredited must be greater than {value}.")
	@Max(value = 2100, message = "Year accredited must be lesser than than {value}.")
	private Integer yearAccredited;
}
