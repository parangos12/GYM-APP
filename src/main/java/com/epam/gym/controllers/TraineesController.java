package com.epam.gym.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.payloads.TraineeProfileDTO;
import com.epam.gym.payloads.TrainerDTO;
import com.epam.gym.payloads.TrainingDTO;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.services.TraineeService;
import com.epam.gym.services.TrainingService;
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
	public ResponseEntity<TraineeProfileDTO> getTraineeByUsername(@PathVariable String username){
		return new ResponseEntity<>(traineeService.getTraineeProfile(username), HttpStatus.OK) ;
	}
	
	@PutMapping("/{username}")
	public ResponseEntity<TraineeProfileDTO> updateTraineeProfile(@PathVariable String username,@RequestBody TraineeProfileDTO traineeDTO){
		TraineeProfileDTO traineeUpdated=this.traineeService.updateTraineeProfile(username, traineeDTO);
		return new ResponseEntity<TraineeProfileDTO>(traineeUpdated, HttpStatus.CREATED) ;
	}
	
	@DeleteMapping("/{username}")
	public ApiResponse deletePost(@PathVariable String username) {
		this.traineeService.deleteTrainee(username);
		return new ApiResponse("Username is successfully deleted !!", true);
	}
	
	@PutMapping("/{username}/trainers")
	public ResponseEntity<List<TrainerDTO>> updateTraineTrainers(@PathVariable String username,@RequestBody Map<String,List<String>> trainerUsernames){
		List<String> trainerUsernamesList= trainerUsernames.get("trainersList");
		List<TrainerDTO> traineesTrainersUpdated=this.traineeService.updateTraineTrainers(username, trainerUsernamesList);
		return new ResponseEntity<>(traineesTrainersUpdated, HttpStatus.OK) ;
	}
	
	@GetMapping("/trainers")
	public ResponseEntity<List<Object[]>> getTraineeTrainingList(@RequestParam String username,
			@RequestParam(value = "periodFrom", required = false) LocalDate periodFrom,
			@RequestParam(value = "periodTo", required = false) LocalDate periodTo,
			@RequestParam(value = "trainerName", required = false) String trainerName,
			@RequestParam(value = "trainingTypeName", required = false) TrainingTypeEnum trainingType) {

		return new ResponseEntity<>(traineeService.findTraineeTrainingList(username, periodFrom, periodTo, trainerName, trainingType),HttpStatus.OK);
	}

	@PostMapping("/trainings")
	public ApiResponse saveTraineeTrainings(@RequestBody TrainingDTO trainingDTO){
		this.trainingService.saveTraining(trainingDTO);
		return new ApiResponse("Training saved sucesfully !!", true);
	}
	
}
