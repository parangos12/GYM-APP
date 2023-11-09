package com.epam.gym.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gym.entities.Trainee;
import com.epam.gym.payloads.TraineeDTO;

@RestController
@RequestMapping("/api/v1/trainees")
public class TraineesController {

	
	
	
	@GetMapping("/{username}")
	public ResponseEntity<Trainee> getTraineeByUsername(@PathVariable String username){
		return null;
	}
	
}
