package com.epam.gym.controllers;

import java.util.List;
import java.util.*;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.epam.gym.dto.TraineeCreateDTO;
import com.epam.gym.dto.TraineeDTO;
import com.epam.gym.dto.TraineeTrainerDTO;
import com.epam.gym.dto.TraineeUpdateDTO;
import com.epam.gym.dto.TraineeUpdateTrainersDTO;
import com.epam.gym.dto.TrainerDTO;
import com.epam.gym.dto.TrainingDTO;
import com.epam.gym.dto.TrainingRequest;
import com.epam.gym.dto.TrainingsFilterDTO;
import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.exceptions.AccessDeniedException;
import com.epam.gym.exceptions.APIResponse;
import com.epam.gym.services.AuthenticationService;
import com.epam.gym.services.TraineeService;
import com.epam.gym.services.TrainerService;
import com.epam.gym.services.TrainingService;
import com.epam.gym.services.impl.CloudWatchLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
public class TraineesController {
	
	private final TraineeService traineeService;
	private final TrainerService trainerService;
	private final TrainingService trainingService;
	private final AuthenticationService auhtService;
	private final QueueMessagingTemplate queueMessagingTemplate;
    private final CloudWatchLogger cloudWatchLogger;
    private final ObjectMapper objectMapper;

	@Value("${amazon.sqs.endpoint}")
	private String sqsEndpoint;
	
	@GetMapping("/{username}")
	public ResponseEntity<TraineeDTO> getTraineeByUsername( @PathVariable String username) throws AccessDeniedException{
		ResponseEntity<TraineeDTO> response;
		try {
		if(!auhtService.hasPermission(username, "TRAINEE")) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("getTraineeByUsername", "ERROR", "Permission denied for getting trainee's profile", username));
			throw new AccessDeniedException(auhtService.actualUsername(),"to get profile of user",username);
		}
			response= new ResponseEntity<>(traineeService.getTraineeProfile(username), HttpStatus.OK) ;
			cloudWatchLogger.logToCloudWatch(buildLogMessage("getTraineeByUsername", "INFO", "Trainee's profile served succesfully", username));
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("getTraineeByUsername", "ERROR", "Error ocurred", username));
            throw e;
		}
		return response;
	}
    
	@PutMapping("/{username}")
    public ResponseEntity<?> updateTraineeProfile(@PathVariable String username,@Valid @RequestBody TraineeUpdateDTO traineeUpdateDTO,BindingResult bindingResult) throws AccessDeniedException{
		ResponseEntity<TraineeDTO> response;
		try {
	    	if(!auhtService.hasPermission(username, "TRAINEE")) {
				cloudWatchLogger.logToCloudWatch(buildLogMessage("updateTraineeProfile", "ERROR", "Permission denied for updating user", username));
				throw new AccessDeniedException(auhtService.actualUsername(),"to update profile of user",username);}
	    	  if (bindingResult.hasErrors()) {
	    		    return ResponseEntity.badRequest().body(buildValidationErrorResponse(bindingResult,username)); 
	    		  }
			TraineeDTO traineeUpdated=this.traineeService.updateTraineeProfile(username, traineeUpdateDTO);
			response= new ResponseEntity<>(traineeUpdated, HttpStatus.CREATED);
			cloudWatchLogger.logToCloudWatch(buildLogMessage("updateTraineeProfile", "INFO", "Update trainee profile", username));
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("updateTraineeProfile", "ERROR", "Error ocurred trying to update profile", username));
            throw e;
		}return response;
		}
	
	@PostMapping("/auth/register")
	public ResponseEntity<UserCredentialsDTO> registerTrainee(@Valid @RequestBody TraineeCreateDTO traineeCreateDTO){
		ResponseEntity<UserCredentialsDTO> response;
		try {
			UserCredentialsDTO authenticationResponse=this.traineeService.registerNewTrainee(traineeCreateDTO);
			cloudWatchLogger.logToCloudWatch(buildLogMessage("registerTrainee", "INFO", "Trainee created", traineeCreateDTO.getFirstName()));
			response= new ResponseEntity<>(authenticationResponse, HttpStatus.CREATED);
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("registerTrainee", "ERROR", "Error ocurred trying to register a trainee", traineeCreateDTO.getFirstName()));
            throw e;
		}
		return response;
	}
	
	@DeleteMapping("/{username}")
	public APIResponse deleteTrarinee(@PathVariable String username) throws ResourceNotFoundException,AccessDeniedException {
		APIResponse response;
		try {
			if(!auhtService.hasPermission(username, "TRAINEE")) {
				cloudWatchLogger.logToCloudWatch(buildLogMessage("deleteTrarinee", "ERROR", "Permission denied for deleting user", username));
				throw new AccessDeniedException(auhtService.actualUsername(),"to delete profile of user",username);
			}
			this.traineeService.deleteTrainee(username);
			cloudWatchLogger.logToCloudWatch(buildLogMessage("deleteTrarinee", "INFO", "User deleted succesfully", username));
			response= new APIResponse("Username is successfully deleted !!", true);
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("deleteTrarinee", "ERROR", "Error ocurred trying to delete a trainee", username));
            throw e;
		}
		return response;
	}
    
	@PutMapping("/{username}/trainers")
	public ResponseEntity<List<TraineeTrainerDTO>> updateTraineTrainers(@PathVariable String username, @Valid @RequestBody TraineeUpdateTrainersDTO traineeUpdateTrainersDTO)throws ResourceNotFoundException,AccessDeniedException{
		ResponseEntity<List<TraineeTrainerDTO>> response;
		try {
			if(!auhtService.hasPermission(username, "TRAINEE")) {
				cloudWatchLogger.logToCloudWatch(buildLogMessage("updateTraineTrainers", "ERROR", "Permission denied to update traine trainers list", username));
				throw new AccessDeniedException(auhtService.actualUsername(),"to update trainers of user",username);
			}
			List<TraineeTrainerDTO> traineesTrainersUpdated=this.traineeService.updateTraineTrainers(username, traineeUpdateTrainersDTO);
			cloudWatchLogger.logToCloudWatch(buildLogMessage("updateTraineTrainers", "INFO", "Traine trainers list updated succesfully", username));
			response= new ResponseEntity<>(traineesTrainersUpdated, HttpStatus.OK) ;
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("updateTraineTrainers", "ERROR", "Error ocurred trying to update traine trainers list", username));
            throw e;
		}
		return response;
	}
    
	@GetMapping("/{username}/trainings")
	public ResponseEntity<List<Object[]>> getTraineeTrainingList(@PathVariable String username, @Valid @ModelAttribute TrainingsFilterDTO trainingsFilterDTO)throws ResourceNotFoundException,AccessDeniedException { 
		ResponseEntity<List<Object[]>> response;
		try {
			if(!auhtService.hasPermission(username, "TRAINEE")) {
				cloudWatchLogger.logToCloudWatch(buildLogMessage("getTraineeTrainingList", "ERROR", "Permission denied to get traine's training list", username));
				throw new AccessDeniedException(auhtService.actualUsername(),"to get trainings list of user",username);
			}
			response= new ResponseEntity<>(traineeService.findTraineeTrainingList(username,trainingsFilterDTO),HttpStatus.OK);
			cloudWatchLogger.logToCloudWatch(buildLogMessage("getTraineeTrainingList", "INFO", "Traine's training list list served succesfully", username));
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("getTraineeTrainingList", "ERROR", "Error ocurred trying to get traine's training list", username));
            throw e;
		}
		return response;
	}
    
	@PostMapping("/trainings")
	public APIResponse addTraineeTrainings(@Valid @RequestBody TrainingDTO trainingDTO,BindingResult bindingResult)throws ResourceNotFoundException,AccessDeniedException, JsonProcessingException{
		APIResponse response;
		try {
			if(!auhtService.hasPermission(trainingDTO.getTraineeUsername(), "TRAINEE")) {
				cloudWatchLogger.logToCloudWatch(buildLogMessage("addTraineeTrainings", "ERROR", "Permission denied to add traine training", trainingDTO.getTrainerUsername()));
				throw new AccessDeniedException(auhtService.actualUsername(),"to update trainings list of user",trainingDTO.getTraineeUsername());
			}
	    	  if (bindingResult.hasErrors()) {
	    		  Map<String,Object> errors= buildValidationErrorResponse(bindingResult,trainingDTO.getTraineeUsername());
	    		  return new APIResponse(errors.toString(), false);
	    		  }

			cloudWatchLogger.logToCloudWatch(buildLogMessage("getTraineeTrainingList", "INFO", "Traine's training saved succesfully", trainingDTO.getTraineeUsername()));
			this.trainingService.saveTraining(trainingDTO);
			sendToTrainerReport(trainingDTO);
			response= new APIResponse("Training saved sucesfully !!", true);
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("addTraineeTrainings", "ERROR", "Error ocurred trying to add traine training", trainingDTO.getTraineeUsername()));
            throw e;
		}
		return response;
	}
	
	public void sendToTrainerReport(TrainingDTO trainingDTO) throws JsonProcessingException {
		try {
			TrainerDTO trainer=this.trainerService.getTrainerProfile(trainingDTO.getTrainerUsername());
			TrainingRequest trainingRequest=new TrainingRequest(trainingDTO.getTrainerUsername(), 
					trainer.getFirstName(), trainer.getLastName(), trainer.getIsActive(), trainingDTO.getTrainingDate(), trainingDTO.getTrainingDuration(), trainingDTO.getSpecialization(), "ADD");
			queueMessagingTemplate.send(sqsEndpoint,MessageBuilder.withPayload(trainingRequest.toString()).build());
			cloudWatchLogger.logToCloudWatch(buildLogMessage("sendToTrainerReport", "INFO", "Training sent to the Report's Microservice Queue", trainingDTO.getTrainerUsername()));
		}catch (Exception e) {
			cloudWatchLogger.logToCloudWatch(buildLogMessage("sendToTrainerReport", "ERROR", "Error ocurred trying send to the Report's Microservice Queue", trainingDTO.getTrainerUsername()));
            throw e;
		}
	}
	
    private String buildLogMessage(String functionName, String logType, String description, String username) {
        return String.format("UUID |%s| LogType |%s| Description |%s| Classname |%s| FunctionName |%s| Username |%s",
                UUID.randomUUID().toString(), logType, description,getClass().getName(), functionName, username);
    }
    
    private Map<String, Object> buildValidationErrorResponse(BindingResult bindingResult,String username) {
    	  
    	  List<FieldError> fieldErrors = bindingResult.getFieldErrors();

    	  List<Map<String, String>> errors = new ArrayList<>();

    	  for (FieldError error : fieldErrors) {
    		    cloudWatchLogger.logToCloudWatch(buildLogMessage("updateTraineeProfile", "ERROR", error.getDefaultMessage(), username)); 
    	    Map<String, String> errorMap = new HashMap<>();
    	    errorMap.put("field", error.getField());
    	    errorMap.put("message", error.getDefaultMessage());

    	    errors.add(errorMap);
    	  }

    	  Map<String, Object> response = new HashMap<>();
    	  response.put("errors", errors);

    	  return response;
    	}

}
