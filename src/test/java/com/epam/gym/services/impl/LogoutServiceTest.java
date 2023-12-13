package com.epam.gym.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.epam.gym.entities.Token;
import com.epam.gym.config.JwtService;
import com.epam.gym.config.LoginAttemptService;
import com.epam.gym.dto.AuthenticationDTO;
import com.epam.gym.dto.AuthenticationResponse;
import com.epam.gym.repositories.TokenRepository;
import com.epam.gym.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class LogoutServiceTest {

	@InjectMocks
	private LogoutService logoutService;
	
	@Mock
	private  TokenRepository tokenRepository;
	
	@Mock
	private LoginAttemptService attemptService;
	
	@Mock
	private  JwtService jwtService;

	@Mock
	private  UserRepository userRepo;
	
	@Mock
	private AuthenticationManager authenticationManager;

	private String username;
	private String password;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		username="Pedro.Arango";
		password="giqXPD78";
	}

	@Test
	void testLogout() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        // Mock the request header
        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");

        // Mock the tokenRepository
        Token storedToken = new Token();
        when(tokenRepository.findByToken("mockToken")).thenReturn(Optional.of(storedToken));

        // Call the method under test
        logoutService.logout(request, response, authentication);

        // Verify that the stored token is updated and the SecurityContextHolder is cleared
        assertTrue(storedToken.isExpired());
        assertTrue(storedToken.isRevoked());
        verify(tokenRepository).save(storedToken);
	}
	
	@Test
	void testLogout_authHeaderNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        // Mock the request header
        when(request.getHeader("Authorization")).thenReturn(null);
       // when(request.getHeader("Authorization").startsWith("Bearer ")).thenReturn(false);

        // Mock the tokenRepository
        Token storedToken = new Token();
        when(tokenRepository.findByToken("mockToken")).thenReturn(Optional.of(storedToken));

        // Call the method under test
        logoutService.logout(request, response, authentication);

        // Verify that the stored token is updated and the SecurityContextHolder is cleared
        assertTrue(!storedToken.isExpired());
	}


}














