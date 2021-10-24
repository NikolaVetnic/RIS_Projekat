package com.nv.schoolsystemproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/*
	 * Ova klasa definise prava pristupa nad resursima servera (REST e-
	 * ndpointima) i nasledjuje WebSecurityConfigurerAdapter klasu.
	 * 
	 * @EnableWebSecurity - anotacija na nivou klase, ona omogucuje Sp-
	 * ring Security filter koji autentifikuje zahteve koristeći poslat
	 * token.
	 */
	
	@Value("${spring.security.secret-key}")
	String secretKey;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.cors().and().csrf().disable()
				.addFilterAfter(new JWTAuthorizationFilter(secretKey), UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/v1/project/login")
				.permitAll().anyRequest().authenticated();
	}
}
