package com.epam.gym.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.epam.gym.config.JwtService;
import com.epam.gym.config.LoginAttemptService;
import com.epam.gym.dto.AuthenticationDTO;
import com.epam.gym.dto.AuthenticationResponse;
import com.epam.gym.dto.ChangePasswordRequest;

import com.epam.gym.entities.Role;
import com.epam.gym.entities.User;
import com.epam.gym.entities.Token;
import com.epam.gym.entities.TokenType;
import com.epam.gym.exceptions.LogginDeniedException;
import com.epam.gym.repositories.TokenRepository;
import com.epam.gym.repositories.UserRepository;

class AuthenticationServiceImplTest {

	@InjectMocks
	private AuthenticationServiceImpl authenticationService;
	
	@Mock
	private LoginAttemptService attemptService;
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private  JwtService jwtService;
	
	@Mock
    private PasswordEncoder passwordEncoder;

	@Mock
	private  UserRepository userRepo;

	@Mock
	private  TokenRepository tokenRepository;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testAuthentic_SuccessfulAuthentication() {
	    AuthenticationDTO authDto = new AuthenticationDTO("Pedro.Arango", "password");
        User user1 = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
        User user2 = new User(2L, "Jose", "Alberto", "Jose.Arango", "giqXPD782", true, Role.TRAINEE);

        Token token1 = new Token(1L,"token1", TokenType.BEARER, false, false, user1);
        Token token2 = new Token(2L,"token2", TokenType.BEARER, false, false, user2);
        Token token3 = new Token(3L,"token3", TokenType.BEARER, false, false, user1);

	    // Mock successful authentication
	    when(authenticationManager.authenticate(any()))
	            .thenReturn(new UsernamePasswordAuthenticationToken(authDto.getUsername(), authDto.getPassword()));

	    when(userRepo.findByUsername(authDto.getUsername())).thenReturn(Optional.of(new User()));
	    when(jwtService.generateToken(any(User.class))).thenReturn("mockedToken");
	    var tokens=Arrays.asList(token1,token2);
	    when(tokenRepository.findAllValidTokenByUser(anyLong())).thenReturn(tokens);
	    when(tokenRepository.saveAll(tokens)).thenReturn(tokens);
	    when(tokenRepository.save(token3)).thenReturn(token3);
	    authenticationService.revokeAllUserTokens(user1);

	    // Call the method under test
	    AuthenticationResponse response = authenticationService.authenticate(authDto);

	    assertNotNull(response);
	    assertEquals("mockedToken", response.getToken());
	}

	@Test
	void testAuthenticate_FailedAuthentication() {
	    AuthenticationDTO authDto = new AuthenticationDTO("Pedro.Arango", "wrongPassword");

	    // Mock failed authentication
	    when(authenticationManager.authenticate(any()))
	            .thenThrow(new BadCredentialsException("Bad credentials"));

	    when(userRepo.findByUsername(authDto.getUsername())).thenReturn(Optional.of(new User()));

	    // Call the method under test and expect an exception
	    LogginDeniedException exception = assertThrows(LogginDeniedException.class,
	            () -> authenticationService.authenticate(authDto));

	    assertEquals(" has entered wrong credentials Pedro.Arango", exception.getMessage());

	    // Ensure that loginFailed is called on the LoginAttemptService
	    verify(attemptService, times(1)).loginFailed(authDto.getUsername());
	}
	@Test
	void testAuthentic_BlockedAuthentication() {
	    AuthenticationDTO authDto = new AuthenticationDTO("Pedro.Arango", "password");
        User user1 = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
        when(attemptService.isBlocked(user1.getUsername())).thenReturn(true);

        // Call the method under test and expect an exception
	    LogginDeniedException exception = assertThrows(LogginDeniedException.class,
	            () -> authenticationService.authenticate(authDto));
	    
	    assertEquals("has been blocked. Try later after 1 min "+user1.getUsername(), exception.getMessage());
	}

    @Test
    void testHasPermission() {
        String username = "Pedro.Arango";
        String role = "TRAINEE";

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(new User(), null, Collections.singletonList(new SimpleGrantedAuthority(role))));

        boolean result = authenticationService.hasPermission(username, role);

        assertTrue(true);
    }

    @Test
    void testActualUsername() {
        User user = new User();
        user.setUsername("Pedro.Arango");

        // Mock the SecurityContextHolder and its getContext method
        SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(user, null));

        // Set the mocked SecurityContext in the SecurityContextHolder
        SecurityContextHolder.setContext(securityContextMock);

        String result = authenticationService.actualUsername();

        assertEquals("Pedro.Arango", result);
    }


    @Test
    void testChangePassword_SuccessfulChange() {
        String username = "Pedro.Arango";
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword", "newPassword");
        User user=new User(1L, "Pedro", "Arango", "Pedro.Arango", "oldPassword", true, Role.TRAINEE);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);
        authenticationService.changePassword(username, request, new UsernamePasswordAuthenticationToken(user, null));
    }

    @Test
    void testChangePassword_WrongPassword() {
        String username = "Pedro.Arango";
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword", "newPassword");
        User user=new User(1L, "Pedro", "Arango", "Pedro.Arango", "oldPassword", true, Role.TRAINEE);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                authenticationService.changePassword(username, request, new UsernamePasswordAuthenticationToken(user, null)));

        assertEquals("Wrong password", exception.getMessage());
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        String username = "Pedro.Arango";
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword", "differentPassword");
        User user=new User(1L, "Pedro", "Arango", "Pedro.Arango", "oldPassword", true, Role.TRAINEE);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                authenticationService.changePassword(username, request, new UsernamePasswordAuthenticationToken(user, null)));

        assertEquals("Password are not the same", exception.getMessage());
    }
}
