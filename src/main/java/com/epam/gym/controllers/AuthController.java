package com.epam.gym.controllers;

import java.security.Principal;
import java.util.UUID;

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
import com.epam.gym.exceptions.LogginDeniedException;
import com.epam.gym.services.AuthenticationService;
import com.epam.gym.services.impl.CloudWatchLogger;
import com.epam.gym.services.impl.LogoutService;

import io.swagger.v3.oas.annotations.Operation;
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
    private final CloudWatchLogger cloudWatchLogger;
    private String username;
    
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticateTrainee(@Valid @RequestBody AuthenticationDTO authTraineeDTO) throws LogginDeniedException{
		try {
			AuthenticationResponse userCredentialsDTO=this.auhtService.authenticate(authTraineeDTO);
			cloudWatchLogger.logToCloudWatch(buildLogMessage("authenticateTrainee", "INFO", "User authenticated succesfully", authTraineeDTO.getUsername()));
			this.username=authTraineeDTO.getUsername();
			return new ResponseEntity<>(userCredentialsDTO, HttpStatus.CREATED);
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("authenticateTrainee", "ERROR", "Error ocurred trying to authenticate user", authTraineeDTO.getUsername()));
            throw e;
		}
	}
    
	@PatchMapping("/{username}/changePassword")
	public ResponseEntity<?> changePassword(@PathVariable String username, @RequestBody ChangePasswordRequest request,
			Principal connectedUser
			)throws IllegalStateException{
		try {
			auhtService.changePassword(username,request, connectedUser);
			cloudWatchLogger.logToCloudWatch(buildLogMessage("changePassword", "INFO", "User's password changed succesfully",username));
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("changePassword", "ERROR", "Error ocurred trying to change user's password",username));
            throw e;
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response){
    	try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    		logoutService.logout(request, response, authentication);
			cloudWatchLogger.logToCloudWatch(buildLogMessage("logout", "INFO", "User's session logged out sucesfully",this.username));
    		return ResponseEntity.ok().build();
    	}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("logout", "ERROR", "Error ocurred trying to log user's session out",this.username));
            throw e;
		}
	}
    
    
    private String buildLogMessage(String functionName, String logType, String description, String username) {
        return String.format("UUID |%s| LogType |%s| Description |%s| Classname |%s| FunctionName |%s| Username |%s",
                UUID.randomUUID().toString(), logType, description,getClass().getName(), functionName, username);
    }


}














