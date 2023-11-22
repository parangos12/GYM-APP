package com.epam.gym.controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gym.dto.AuthenticationResponse;
import com.epam.gym.dto.ChangePasswordRequest;
import com.epam.gym.dto.AuthenticationDTO;
import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.exceptions.LogginDeniedException;
import com.epam.gym.services.AuthenticationService;
import com.epam.gym.services.TraineeService;
import com.epam.gym.services.TrainingService;
import com.epam.gym.services.impl.LogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthenticationService auhtService;
	private final LogoutService logoutService;

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticateTrainee(@Valid @RequestBody AuthenticationDTO authTraineeDTO) throws LogginDeniedException{
		AuthenticationResponse userCredentialsDTO=this.auhtService.authenticate(authTraineeDTO);
		return new ResponseEntity<>(userCredentialsDTO, HttpStatus.CREATED);
	}
	
	@PatchMapping("{username}/changePassword")
	public ResponseEntity<?> changePassword(@PathVariable String username, @RequestBody ChangePasswordRequest request,
			Principal connectedUser
			){
		auhtService.changePassword(username,request, connectedUser);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		logoutService.logout(request, response, authentication);
		return ResponseEntity.ok().build();
	}


}














