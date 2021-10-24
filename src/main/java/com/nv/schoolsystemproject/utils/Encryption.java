package com.nv.schoolsystemproject.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encryption {

	public static String getPassEncoded(String pass) {
		return new BCryptPasswordEncoder().encode(pass);
	}
	
	public static boolean validatePassword(String pass, String encodedPass) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.matches(pass, encodedPass);
	}
	
	public static void main(String[] args) {
		System.out.println(getPassEncoded("adMIN1234!"));
	}
}
