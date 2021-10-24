package com.nv.schoolsystemproject.controllers.dto;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class AbsenceDTO {

	@Size(max = 200, message = "Note cannot exceed {max} characters.")
	private String note;
}
