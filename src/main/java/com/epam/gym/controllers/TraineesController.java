package com.epam.gym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gym.entities.Trainee;
import com.epam.gym.payloads.TraineeProfileDTO;
import com.epam.gym.services.TraineeService;

@RestController
@RequestMapping("/api/v1/trainees")
public class TraineesController {

	private final TraineeService traineeService;
	
	@Autowired
	public TraineesController(TraineeService traineeService) {
		this.traineeService=traineeService;
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<TraineeProfileDTO> getTraineeByUsername(@PathVariable String username){
		return new ResponseEntity<>(traineeService.getTraineeProfile(username), HttpStatus.OK) ;
	}
	
}
