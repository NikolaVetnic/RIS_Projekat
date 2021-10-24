package com.nv.schoolsystemproject.controllers.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class UserLoginDTO {
	
	/*
	 * Klasa koja se koristi od strane klijenata za logovanje na serve-
	 * r.
	 */

	private String username;
	private String token;
}
