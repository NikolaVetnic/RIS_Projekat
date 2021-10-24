package com.nv.schoolsystemproject.controllers.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.nv.schoolsystemproject.entities.EUserRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class UserRegisterDTO {

	@NotBlank(message = "Username must be provided.")
	@Size(min = 5, max = 15, message = "Username must be between {min} and {max} characters long.")
	private String username;
	
	@NotBlank(message = "Password must be provided.")
	@Size(min = 5, max = 100, message = "Password must be between {min} and {max} characters long.")
//	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message="Password must contain at a lowercase and an uppercase letter, a number and a special character.")
    private String password;

	@NotBlank(message = "Confirm password must be provided.")
	@Size(min = 5, max = 100, message = "Confirm password must be between {min} and {max} characters long.")
    private String confirmPassword;
	
	private EUserRole role;
}
