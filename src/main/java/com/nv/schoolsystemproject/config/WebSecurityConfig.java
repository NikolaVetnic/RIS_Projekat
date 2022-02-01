package com.nv.schoolsystemproject.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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

	public WebSecurityConfig() {
        super();
    }

	@Bean("authenticationManager")
	@Override
	@PostConstruct
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/*
		 * Zakomentarisane su dve linije koje su funkcionisale dok su se s-
		 * lali zahtevi iz Postman-a, posto sada ne znam kako da izvedem to
		 * zajedno sa frontom odlucio sam se za manuelnu autentikaciju pos-
		 * tavljanjem u SecurityContext.
		 */ 
		
		http.cors().and().csrf().disable()
//			.addFilterAfter(new JWTAuthorizationFilter(secretKey), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/").permitAll()
			.antMatchers(HttpMethod.POST, "/").permitAll() // ovo promeniti samo za login POST
			.antMatchers(HttpMethod.GET, "/api/v1/project/admin/**").hasAuthority("ADMIN")
			.antMatchers(HttpMethod.GET, "/api/v1/project/parent/**").hasAuthority("PARENT")
			.antMatchers(HttpMethod.GET, "/api/v1/project/student/**").hasAuthority("STUDENT")
			.antMatchers(HttpMethod.GET, "/api/v1/project/teacher/**").hasAuthority("TEACHER");
//			.anyRequest().authenticated();
	}
}
