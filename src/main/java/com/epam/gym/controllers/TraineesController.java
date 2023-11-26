package com.epam.gym.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.epam.gym.dto.AuthenticationResponse;
import com.epam.gym.dto.AuthenticationDTO;
import com.epam.gym.dto.TrainingDTO;
import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.dto.trainee.TraineeCreateDTO;
import com.epam.gym.dto.trainee.TraineeDTO;
import com.epam.gym.dto.trainee.TraineeTrainerDTO;
import com.epam.gym.dto.trainee.TraineeUpdateDTO;
import com.epam.gym.dto.trainee.TraineeUpdateTrainersDTO;
import com.epam.gym.dto.trainee.TrainingRequest;
import com.epam.gym.dto.trainee.TrainingsFilterDTO;
import com.epam.gym.dto.trainer.TrainerDTO;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.exceptions.AccessDeniedException;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.services.AuthenticationService;
import com.epam.gym.services.TraineeService;
import com.epam.gym.services.TrainerService;
import com.epam.gym.services.TrainingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.epam.gym.payloads.ApiResponse;


@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
public class TraineesController {

	private final TraineeService traineeService;
	private final TrainerService trainerService;
	private final TrainingService trainingService;
	private final AuthenticationService auhtService;
	private final QueueMessagingTemplate queueMessagingTemplate;
	
	@Value("${amazon.sqs.endpoint}")
	private String sqsEndpoint;

	
	@GetMapping("/{username}")
	public ResponseEntity<TraineeDTO> getTraineeByUsername(@PathVariable String username) throws ResourceNotFoundException,AccessDeniedException{
		if(!auhtService.hasPermission(username, "TRAINEE")) {
			throw new AccessDeniedException(auhtService.actualUsername(),"to get profile of user",username);
		}
		return new ResponseEntity<>(traineeService.getTraineeProfile(username), HttpStatus.OK) ;
	}
	
	@PutMapping("/{username}")
	public ResponseEntity<TraineeDTO> updateTraineeProfile(@PathVariable String username,@Valid @RequestBody TraineeUpdateDTO traineeUpdateDTO) throws ResourceNotFoundException,AccessDeniedException{
		if(!auhtService.hasPermission(username, "TRAINEE")) {
			throw new AccessDeniedException(auhtService.actualUsername(),"to update profile of user",username);
		}
		TraineeDTO traineeUpdated=this.traineeService.updateTraineeProfile(username, traineeUpdateDTO);
		return new ResponseEntity<>(traineeUpdated, HttpStatus.CREATED);}
	
	
	@PostMapping("/auth/register")
	public ResponseEntity<UserCredentialsDTO> registerTrainee(@Valid @RequestBody TraineeCreateDTO traineeCreateDTO){
		UserCredentialsDTO authenticationResponse=this.traineeService.registerNewTrainee(traineeCreateDTO);
		return new ResponseEntity<>(authenticationResponse, HttpStatus.CREATED);
	}
	
	
	@DeleteMapping("/{username}")
	public ApiResponse deleteTrarinee(@PathVariable String username) throws ResourceNotFoundException,AccessDeniedException {
		if(!auhtService.hasPermission(username, "TRAINEE")) {
			throw new AccessDeniedException(auhtService.actualUsername(),"to delete profile of user",username);
		}
		this.traineeService.deleteTrainee(username);
		return new ApiResponse("Username is successfully deleted !!", true);
	}
	
	@PutMapping("/{username}/trainers")
	public ResponseEntity<List<TraineeTrainerDTO>> updateTraineTrainers(@PathVariable String username, @Valid @RequestBody TraineeUpdateTrainersDTO traineeUpdateTrainersDTO)throws ResourceNotFoundException,AccessDeniedException{
		if(!auhtService.hasPermission(username, "TRAINEE")) {
			throw new AccessDeniedException(auhtService.actualUsername(),"to update trainers of user",username);
		}
		List<TraineeTrainerDTO> traineesTrainersUpdated=this.traineeService.updateTraineTrainers(username, traineeUpdateTrainersDTO);
		return new ResponseEntity<>(traineesTrainersUpdated, HttpStatus.OK) ;
	}
	
	@GetMapping("/{username}/trainings")
	public ResponseEntity<List<Object[]>> getTraineeTrainingList(@PathVariable String username, @Valid @ModelAttribute TrainingsFilterDTO trainingsFilterDTO)throws ResourceNotFoundException,AccessDeniedException { 
		if(!auhtService.hasPermission(username, "TRAINEE")) {
			throw new AccessDeniedException(auhtService.actualUsername(),"to get trainings list of user",username);
		}
		return new ResponseEntity<>(traineeService.findTraineeTrainingList(username,trainingsFilterDTO),HttpStatus.OK);
	}

	@PostMapping("/trainings")
	public ApiResponse addTraineeTrainings(@Valid @RequestBody TrainingDTO trainingDTO)throws ResourceNotFoundException,AccessDeniedException, JsonProcessingException{
		if(!auhtService.hasPermission(trainingDTO.getTraineeUsername(), "TRAINEE")) {
			throw new AccessDeniedException(auhtService.actualUsername(),"to update trainings list of user",trainingDTO.getTraineeUsername());
		}
		this.trainingService.saveTraining(trainingDTO);
		sendToTrainerReport(trainingDTO);
		return new ApiResponse("Training saved sucesfully !!", true);
	}
	
	public void sendToTrainerReport(TrainingDTO trainingDTO) throws JsonProcessingException {
		TrainerDTO trainer=this.trainerService.getTrainerProfile(trainingDTO.getTrainerUsername());
		TrainingRequest trainingRequest=new TrainingRequest(trainingDTO.getTrainerUsername(), 
				trainer.getFirstName(), trainer.getLastName(), trainer.getIsActive(), trainingDTO.getTrainingDate(), trainingDTO.getTrainingDuration(), trainingDTO.getSpecialization(), "ADD");
		queueMessagingTemplate.send(sqsEndpoint,MessageBuilder.withPayload(trainingRequest.toString()).build());
	}
	

}
