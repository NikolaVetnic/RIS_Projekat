package com.nv.schoolsystemproject.controllers.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RESTError {

	private Integer code;
	private String message;
}
