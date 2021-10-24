package com.nv.schoolsystemproject.controllers.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class SessionRegisterDTO {

	@NotBlank(message = "Topic must be provided.")
	@Size(min = 5, max = 50, message = "Topic must be between {min} and {max} characters long.")
	private String topic;
}
