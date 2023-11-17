package com.epam.gym.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.dto.trainee.TraineeUpdateDTO;
import com.epam.gym.dto.trainee.TrainingsFilterDTO;
import com.epam.gym.dto.trainer.TrainerCreateDTO;
import com.epam.gym.dto.trainer.TrainerDTO;
import com.epam.gym.dto.trainer.TrainerUpdateDTO;
import com.epam.gym.dto.trainer.TrainerUpdatedDTO;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.services.TrainerService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/trainers")
public class TrainersController {

	private final TrainerService trainerService;

	@Autowired
	public TrainersController(TrainerService trainerService) {
		this.trainerService = trainerService;
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<TrainerDTO> getTrainerProfile(@PathVariable String username) throws ResourceNotFoundException{
		return new ResponseEntity<>(this.trainerService.getTrainerProfile(username),HttpStatus.OK);
	}
	
	@PutMapping("/{username}")
	public ResponseEntity<TrainerUpdatedDTO> updateTraineeProfile(@PathVariable String username,@Valid @RequestBody TrainerUpdateDTO trainerUpdateDTO)throws ResourceNotFoundException{
		TrainerUpdatedDTO trainerUpdated=trainerUpdated=this.trainerService.updateTrainerProfile(username, trainerUpdateDTO);
		return new ResponseEntity<>(trainerUpdated, HttpStatus.CREATED) ;
	}
	
	
	@GetMapping("/{username}/trainings")
	public ResponseEntity<List<Object[]>> getTraineeTrainingList(@PathVariable String username, @Valid @ModelAttribute TrainingsFilterDTO trainingsFilterDTO)throws ResourceNotFoundException { 
		return new ResponseEntity<>(trainerService.getTrainerTrainingList(username,trainingsFilterDTO),HttpStatus.OK);
	}

	
	@PostMapping("/register")
	public ResponseEntity<UserCredentialsDTO> registerTrainer(@Valid @RequestBody TrainerCreateDTO trainerCreateDTO){
		UserCredentialsDTO userCredentialsDTO=this.trainerService.registerNewTrainer(trainerCreateDTO);
		return new ResponseEntity<>(userCredentialsDTO, HttpStatus.CREATED);
	}
	
	@GetMapping("/unassigned")
	public ResponseEntity<List<Object[]>> getUnassignedTrainersForTrainee(){
		return new ResponseEntity<>(this.trainerService.findActiveTrainersWithNoTrainees(),HttpStatus.OK);
	}

}

