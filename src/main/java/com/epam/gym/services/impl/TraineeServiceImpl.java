package com.epam.gym.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.security.crypto.password.PasswordEncoder;

import com.epam.gym.config.JwtService;
import com.epam.gym.dto.TraineeCreateDTO;
import com.epam.gym.dto.TraineeDTO;
import com.epam.gym.dto.TraineeTrainerDTO;
import com.epam.gym.dto.TraineeUpdateDTO;
import com.epam.gym.dto.TraineeUpdateTrainersDTO;
import com.epam.gym.dto.TrainingsFilterDTO;
import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.entities.Role;
import com.epam.gym.entities.Token;
import com.epam.gym.entities.TokenType;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.repositories.TokenRepository;
import com.epam.gym.repositories.Trainee2TrainerRepository;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.repositories.UserRepository;
import com.epam.gym.services.TraineeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService{
	
	private final TraineeRepository traineeRepo;
	private final TrainerRepository trainerRepo;
	private final TrainingRepository trainingRepo;
	private final Trainee2TrainerRepository trainee2trainerRepo;
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final TokenRepository tokenRepository;

	@Override
	public TraineeDTO getTraineeProfile(String username) {
		Trainee traineeFound=this.traineeRepo.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		List<TraineeTrainerDTO> trainerDTOList=new ArrayList<>();
		List<Trainee2Trainer> trainerList= this.trainee2trainerRepo.findByTraineeID(traineeFound.getId());
				
		for (Trainee2Trainer trainer : trainerList) {
			Trainer traineesTrainer=trainer.getTrainer();
			TraineeTrainerDTO trainerDTO=new TraineeTrainerDTO(traineesTrainer.getUser().getUsername(),traineesTrainer.getUser().getFirstName(),traineesTrainer.getUser().getLastName(),traineesTrainer.getSpecialization().getTrainingTypeEnum()); 
			trainerDTOList.add(trainerDTO);
		}
		
		TraineeDTO traineeProfile=new TraineeDTO();
		traineeProfile.setFirstName(traineeFound.getUser().getFirstName());
		traineeProfile.setLastName(traineeFound.getUser().getLastName());
		traineeProfile.setIsActive(traineeFound.getUser().getIsActive());
		traineeProfile.setAddress(traineeFound.getAddress());
		traineeProfile.setDateOfBirth(traineeFound.getDateOfBirth());
		traineeProfile.setTrainers(trainerDTOList);

		return traineeProfile;
	}

	@Override
	public TraineeDTO updateTraineeProfile(String username,TraineeUpdateDTO traineeUpdateDTO) {
		
		Trainee trainee=this.traineeRepo.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		trainee.getUser().setFirstName(traineeUpdateDTO.getFirstName());
		trainee.getUser().setLastName(traineeUpdateDTO.getLastName());
		trainee.getUser().setIsActive(traineeUpdateDTO.getIsActive());
		trainee.setAddress(traineeUpdateDTO.getAddress());
		trainee.setDateOfBirth(traineeUpdateDTO.getDateOfBirth());
		this.traineeRepo.save(trainee);
		return this.getTraineeProfile(username);
	}

	@Override
	public void deleteTrainee(String username) {
		Trainee trainee=this.traineeRepo.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		trainee.getUser().setIsActive(false);
		this.traineeRepo.save(trainee);
	}

	@Override
	public List<TraineeTrainerDTO> updateTraineTrainers(String username, TraineeUpdateTrainersDTO traineeUpdateTrainersDTO) {
		Trainee trainee=this.traineeRepo.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		List<String> existingTrainers= this.trainee2trainerRepo.findByTraineeID(trainee.getId()).stream().map(t->t.getTrainer().getUser().getUsername()).toList();
		List<TraineeTrainerDTO> trainerDTOList=new ArrayList<>();
		List<String> newTraineeTrainersDTO=traineeUpdateTrainersDTO.getTrainerUsernames();

		
		for (String trainerUsername : existingTrainers) {
			if(!newTraineeTrainersDTO.contains(trainerUsername)) {
				trainee2trainerRepo.deleteByTraineeAndTrainerUsername(username,trainerUsername);
				trainee2trainerRepo.flush();
			}
		}
		List<Trainee2Trainer> trainerList= this.trainee2trainerRepo.findByTraineeID(trainee.getId());
		for (Trainee2Trainer trainer : trainerList) {
			Trainer traineesTrainer=trainer.getTrainer();
			TraineeTrainerDTO trainerDTO=new TraineeTrainerDTO(traineesTrainer.getUser().getUsername(),traineesTrainer.getUser().getFirstName(),traineesTrainer.getUser().getLastName(),traineesTrainer.getSpecialization().getTrainingTypeEnum()); 
			trainerDTOList.add(trainerDTO);
		}
		return trainerDTOList;
	}

	@Override
	public List<Object[]> findTraineeTrainingList(String username,TrainingsFilterDTO trainingsFilterDTO) {
		LocalDate periodFrom=trainingsFilterDTO.periodFrom();
		LocalDate periodTo=trainingsFilterDTO.periodTo();
		String trainerName=trainingsFilterDTO.trainerName();
		TrainingTypeEnum trainingType=null;
		if(trainingsFilterDTO.specialization()!=null) {
	    trainingType =TrainingTypeEnum.valueOf(trainingsFilterDTO.specialization().toLowerCase());}
		return this.trainingRepo.findTraineeTrainingList(username, periodFrom, periodTo, trainerName, trainingType);
	}

	@Override
	public UserCredentialsDTO registerNewTrainee(TraineeCreateDTO traineeCreateDTO) {
		String firstName=traineeCreateDTO.getFirstName();
		String lastName=traineeCreateDTO.getLastName();
		Boolean isActive=true;
		String address=traineeCreateDTO.getAddress();
		LocalDate dateOfBirth=traineeCreateDTO.getDateOfBirth();

		String username=generateUserName(firstName,lastName);
		String passwordRetrieve=generateRandomPassword();
		String passwordSaved=this.passwordEncoder.encode(passwordRetrieve);

		User newUser=new User(null, firstName, lastName, username, passwordSaved, isActive,Role.TRAINEE);
		Trainee newTrainee=new Trainee(null,newUser,dateOfBirth,address);

		var savedUser=this.traineeRepo.save(newTrainee);
		var jwtToken=jwtService.generateToken(newUser);
		saveUserToken(newUser,jwtToken);	
		return new UserCredentialsDTO(username, passwordRetrieve,jwtToken);
	}
	  private void saveUserToken(User user, String jwtToken) {
		    var token = Token.builder()
		        .user(user)
		        .token(jwtToken)
		        .tokenType(TokenType.BEARER)
		        .expired(false)
		        .revoked(false)
		        .build();
		    tokenRepository.save(token);
		  }

	   public String generateUserName(String firstName,String lastName) {
		   String userName = firstName + "." + lastName; 
		   int serialNumber=0;	
		   
		   while(!this.userRepo.findByUsername(userName).isEmpty()) {
			   serialNumber++;
			   userName=firstName + "." + lastName + "_" + serialNumber;
		   }
		return userName;
	   }
	   
	   public String generateRandomPassword() {
		   int leftLimit = 48; // 0
		   int rightLimit = 122; // z
		   int passwordLength = 10;
		   
		   Random random = new Random();

		   return random.ints(leftLimit, rightLimit + 1)
		       .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
		       .limit(passwordLength)
		       .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		       .toString();}

}

