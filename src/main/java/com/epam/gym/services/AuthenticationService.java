package com.epam.gym.services;

import com.epam.gym.dto.AuthenticationResponse;
import com.epam.gym.dto.ChangePasswordRequest;

import java.security.Principal;

import com.epam.gym.dto.AuthenticationDTO;
import com.epam.gym.dto.UserCredentialsDTO;

import jakarta.validation.Valid;

public interface AuthenticationService {

	AuthenticationResponse authenticate(@Valid AuthenticationDTO authTraineeDTO);

	boolean hasPermission(String username,String role);
	
	String actualUsername();
	
	void changePassword(String username,ChangePasswordRequest request, Principal connectedUser);
}
