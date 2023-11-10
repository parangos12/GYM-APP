package com.epam.gym.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.User;
import com.epam.gym.payloads.TraineeProfileDTO;
import com.epam.gym.payloads.TrainerDTO;
import com.epam.gym.repositories.Trainee2TrainerRepository;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.repositories.UserRepository;
import com.epam.gym.services.TraineeService;

@Service
public class TraineeServiceImpl implements TraineeService{
	
	private final TraineeRepository traineeRepo;
	private final TrainerRepository trainerRepo;
	private final TrainingRepository trainingRepo;
	private final Trainee2TrainerRepository trainee2trainerRepo;
	private final UserRepository userRepo;

	@Autowired
	public TraineeServiceImpl(TraineeRepository traineeRepo,TrainerRepository trainerRepo,TrainingRepository trainingRepo,UserRepository userRepo,Trainee2TrainerRepository trainee2trainerRepo) {
		this.traineeRepo = traineeRepo;
		this.trainerRepo = trainerRepo;
		this.trainingRepo = trainingRepo;
		this.userRepo = userRepo;
		this.trainee2trainerRepo = trainee2trainerRepo;
	}

	@Override
	public TraineeProfileDTO getTraineeProfile(String username) {
		User userFound=this.userRepo.findByUsername(username);  //This is totally necesary.
		Trainee traineeFound=this.traineeRepo.findByUser(userFound);  //Change this for UserId
		List<TrainerDTO> trainerDTOList=new ArrayList<>();
		List<Trainee2Trainer> trainerList= this.trainee2trainerRepo.findAllByTrainee(traineeFound);
				
		for (Trainee2Trainer trainer : trainerList) {
			Trainer traineesTrainer=trainer.getTrainer();
			TrainerDTO trainerDTO=new TrainerDTO(traineesTrainer.getUser().getUsername(),traineesTrainer.getUser().getFirstName(),traineesTrainer.getUser().getLastName(),traineesTrainer.getSpecialization().getTrainingTypeEnum()); 
			trainerDTOList.add(trainerDTO);
		}
		
		TraineeProfileDTO traineeProfile=new TraineeProfileDTO();
		traineeProfile.setFirstName(userFound.getFirstName());
		traineeProfile.setLastName(userFound.getLastName());
		traineeProfile.setIsActive(userFound.getIsActive());
		traineeProfile.setAddress(traineeFound.getAddress());
		traineeProfile.setDateOfBirth(traineeFound.getDateOfBirth());
		traineeProfile.setTrainers(trainerDTOList);

		return traineeProfile;
	}

}






