package com.epam.gym.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.epam.gym.util.CustomAuthEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private static final String[] WHITE_LIST_URL = {
    		"/api/v1/auth/**",
            "/api/v1/trainees/auth/**",
            "/api/v1/trainers/auth/**",
            "/api/v1/auth/authenticate",
            "/openapiTrainee.yaml",
            "/swagger-ui/**",
            "/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**","/actuator/**"};

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return
			http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(req->
				req.requestMatchers(WHITE_LIST_URL)
				.permitAll()
				.anyRequest()
				.authenticated()
				).exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint())
				.and()
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout->
            		logout.logoutUrl("api/v1/auth/logout")
            			   .addLogoutHandler(logoutHandler)
                           .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
            		)
            .build();
	}
	
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
	   return new CustomAuthEntryPoint(); 
	}

}
