package com.epam.gym.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.dto.trainee.TraineeBasicProfileDTO;
import com.epam.gym.dto.trainee.TrainingsFilterDTO;
import com.epam.gym.dto.trainer.TrainerCreateDTO;
import com.epam.gym.dto.trainer.TrainerDTO;
import com.epam.gym.dto.trainer.TrainerUpdateDTO;
import com.epam.gym.dto.trainer.TrainerUpdatedDTO;
import com.epam.gym.entities.Trainee;
import com.epam.gym.repositories.Trainee2TrainerRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.repositories.TrainingTypeRepository;
import com.epam.gym.repositories.UserRepository;
import com.epam.gym.services.TrainerService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TrainerServiceImpl implements TrainerService{
	
	private final TrainerRepository trainerRepo;
	private final Trainee2TrainerRepository trainee2trainerRepo;
	private final TrainingTypeRepository trainingTypeRepo;
	private final TrainingRepository trainingRepo;
	private final UserRepository userRepo;

	@Autowired
	public TrainerServiceImpl(TrainerRepository trainerRepo,Trainee2TrainerRepository trainee2trainerRepo,TrainingTypeRepository trainingTypeRepo,TrainingRepository trainingRepo,UserRepository userRepo) {
		this.trainerRepo = trainerRepo;
		this.trainee2trainerRepo=trainee2trainerRepo;
		this.trainingTypeRepo=trainingTypeRepo;
		this.trainingRepo=trainingRepo;
		this.userRepo=userRepo;
	}

	@Override
	public TrainerDTO getTrainerProfile(String username) {
	
		Trainer trainerFound=this.trainerRepo.findTrainerByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		List<TraineeBasicProfileDTO> trainerTraineeList=new ArrayList<>();
		List<Trainee2Trainer> traineesList= this.trainee2trainerRepo.findByTrainerID(trainerFound.getId());
				
		for (Trainee2Trainer trainer : traineesList) {
			Trainee traineesTrainer=trainer.getTrainee();
			TraineeBasicProfileDTO traineeDTO=new TraineeBasicProfileDTO(traineesTrainer.getUser().getUsername(),traineesTrainer.getUser().getFirstName(),traineesTrainer.getUser().getLastName()); 
			trainerTraineeList.add(traineeDTO);
		}
		
		TrainerDTO trainerProfile=new TrainerDTO();
		trainerProfile.setFirstName(trainerFound.getUser().getFirstName());
		trainerProfile.setLastName(trainerFound.getUser().getLastName());
		trainerProfile.setIsActive(trainerFound.getUser().getIsActive());
		trainerProfile.setSpecialization(trainerFound.getSpecialization().getTrainingTypeEnum());
		trainerProfile.setIsActive(trainerFound.getUser().getIsActive());
		trainerProfile.setTrainees(trainerTraineeList);
		
		return trainerProfile;
	}

	@Override
	public TrainerUpdatedDTO updateTrainerProfile(String username, TrainerUpdateDTO trainerUpdateDTO) {
		Trainer trainer=this.trainerRepo.findTrainerByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		List<TraineeBasicProfileDTO> trainerTraineeList=new ArrayList<>();
		List<Trainee2Trainer> traineesList= this.trainee2trainerRepo.findByTrainerID(trainer.getId());
				
		for (Trainee2Trainer trainerAux : traineesList) {
			Trainee traineesTrainer=trainerAux.getTrainee();
			TraineeBasicProfileDTO traineeDTO=new TraineeBasicProfileDTO(traineesTrainer.getUser().getUsername(),traineesTrainer.getUser().getFirstName(),traineesTrainer.getUser().getLastName()); 
			trainerTraineeList.add(traineeDTO);
		}

		trainer.getUser().setFirstName(trainerUpdateDTO.getFirstName());
		trainer.getUser().setLastName(trainerUpdateDTO.getLastName());
		trainer.getUser().setIsActive(trainerUpdateDTO.getIsActive());
		
	    TrainingTypeEnum trainingTypeEnum = TrainingTypeEnum.valueOf(trainerUpdateDTO.getSpecialization().toLowerCase());
        TrainingType trainingType = this.trainingTypeRepo.findTrainingTypeBy(trainingTypeEnum);
        trainer.setSpecialization(trainingType);
        
		this.trainerRepo.save(trainer);
		
		TrainerUpdatedDTO trainerUpdatedDTO=new TrainerUpdatedDTO();
		trainerUpdatedDTO.setUsername(trainer.getUser().getUsername());
		trainerUpdatedDTO.setFirstName(trainer.getUser().getFirstName());
		trainerUpdatedDTO.setLastName(trainer.getUser().getLastName());
		trainerUpdatedDTO.setIsActive(trainer.getUser().getIsActive());
		trainerUpdatedDTO.setSpecialization(trainer.getSpecialization().getTrainingTypeEnum());
		trainerUpdatedDTO.setIsActive(trainer.getUser().getIsActive());
		trainerUpdatedDTO.setTrainees(trainerTraineeList);

		return trainerUpdatedDTO;
	}

	@Override
	public List<Object[]> getTrainerTrainingList(String username,TrainingsFilterDTO trainingsFilterDTO) {
		Trainer trainer=this.trainerRepo.findTrainerByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		LocalDate periodFrom=trainingsFilterDTO.periodFrom();
		LocalDate periodTo=trainingsFilterDTO.periodTo();
		String traineeName=trainingsFilterDTO.traineeName();
		return 	this.trainingRepo.findTrainerTrainingList(username, periodFrom, periodTo, traineeName);
	}

	@Override
	public List<Object[]> findActiveTrainersWithNoTrainees() {
		return this.trainee2trainerRepo.findActiveTrainersWithNoTrainees();
	}

	@Override
	public UserCredentialsDTO registerNewTrainer(TrainerCreateDTO trainerCreateDTO) {
		String firstName=trainerCreateDTO.getFirstName();
		String lastName=trainerCreateDTO.getLastName();
		Boolean isActive=true;
	    TrainingTypeEnum trainingTypeEnum = TrainingTypeEnum.valueOf(trainerCreateDTO.getSpecialization().toLowerCase());
        TrainingType trainingType = this.trainingTypeRepo.findTrainingTypeBy(trainingTypeEnum);

		String username=generateUserName(firstName,lastName);
		String password=generateRandomPassword();

		User newUser=new User(null, firstName, lastName, username, password, isActive);
		Trainer newTrainer=new Trainer(null, newUser, trainingType);
		this.trainerRepo.save(newTrainer);
		
		return new UserCredentialsDTO(username, password);	}
	
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
