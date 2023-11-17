package com.epam.gym.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gym.dto.TrainingDTO;
import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.dto.trainee.TraineeCreateDTO;
import com.epam.gym.dto.trainee.TraineeDTO;
import com.epam.gym.dto.trainee.TraineeTrainerDTO;
import com.epam.gym.dto.trainee.TraineeUpdateDTO;
import com.epam.gym.dto.trainee.TraineeUpdateTrainersDTO;
import com.epam.gym.dto.trainee.TrainingsFilterDTO;
import com.epam.gym.dto.trainer.TrainerDTO;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.services.TraineeService;
import com.epam.gym.services.TrainingService;

import jakarta.validation.Valid;

import com.epam.gym.payloads.ApiResponse;


@RestController
@RequestMapping("/api/v1/trainees")
public class TraineesController {

	private final TraineeService traineeService;
	private final TrainingService trainingService;
	
	@Autowired
	public TraineesController(TraineeService traineeService,TrainingService trainingService) {
		this.traineeService=traineeService;
		this.trainingService=trainingService;
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<TraineeDTO> getTraineeByUsername(@PathVariable String username) throws ResourceNotFoundException{
		return new ResponseEntity<>(traineeService.getTraineeProfile(username), HttpStatus.OK) ;
	}
	
	@PutMapping("/{username}")
	public ResponseEntity<TraineeDTO> updateTraineeProfile(@PathVariable String username,@Valid @RequestBody TraineeUpdateDTO traineeUpdateDTO) throws ResourceNotFoundException{
		TraineeDTO traineeUpdated=this.traineeService.updateTraineeProfile(username, traineeUpdateDTO);
		return new ResponseEntity<>(traineeUpdated, HttpStatus.CREATED) ;
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserCredentialsDTO> registerUser(@Valid @RequestBody TraineeCreateDTO traineeCreateDTO){
		UserCredentialsDTO userCredentialsDTO=this.traineeService.registerNewTrainee(traineeCreateDTO);
		return new ResponseEntity<>(userCredentialsDTO, HttpStatus.CREATED);
	}

	
	@DeleteMapping("/{username}")
	public ApiResponse deletePost(@PathVariable String username) throws ResourceNotFoundException {
		this.traineeService.deleteTrainee(username);
		return new ApiResponse("Username is successfully deleted !!", true);
	}
	
	@PutMapping("/{username}/trainers")
	public ResponseEntity<List<TraineeTrainerDTO>> updateTraineTrainers(@PathVariable String username, @Valid @RequestBody TraineeUpdateTrainersDTO traineeUpdateTrainersDTO)throws ResourceNotFoundException{
		List<TraineeTrainerDTO> traineesTrainersUpdated=this.traineeService.updateTraineTrainers(username, traineeUpdateTrainersDTO);
		return new ResponseEntity<>(traineesTrainersUpdated, HttpStatus.OK) ;
	}
	
	@GetMapping("/{username}/trainings")
	public ResponseEntity<List<Object[]>> getTraineeTrainingList(@PathVariable String username, @Valid @ModelAttribute TrainingsFilterDTO trainingsFilterDTO)throws ResourceNotFoundException { 
		return new ResponseEntity<>(traineeService.findTraineeTrainingList(username,trainingsFilterDTO),HttpStatus.OK);
	}

	@PostMapping("/trainings")
	public ApiResponse saveTraineeTrainings(@Valid @RequestBody TrainingDTO trainingDTO)throws ResourceNotFoundException{
		this.trainingService.saveTraining(trainingDTO);
		return new ApiResponse("Training saved sucesfully !!", true);
	}
	

}
