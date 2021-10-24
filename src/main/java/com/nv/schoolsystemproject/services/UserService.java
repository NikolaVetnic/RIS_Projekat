package com.nv.schoolsystemproject.services;

import java.util.Optional;

import com.nv.schoolsystemproject.entities.EUserRole;

public interface UserService {

	public boolean isAuthorizedAs(EUserRole role);
	public Optional<String> getLoggedInUsername();
}
