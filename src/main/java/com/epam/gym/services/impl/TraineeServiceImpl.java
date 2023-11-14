package com.epam.gym.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.payloads.TraineeProfileDTO;
import com.epam.gym.payloads.TrainerDTO;
import com.epam.gym.repositories.Trainee2TrainerRepository;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.repositories.UserRepository;
import com.epam.gym.services.TraineeService;

import jakarta.transaction.Transactional;

@Service
@Transactional
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
		User userFound=this.userRepo.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username));  
		Trainee traineeFound=this.traineeRepo.findByUserID(userFound.getId()).get();
		List<TrainerDTO> trainerDTOList=new ArrayList<>();
		List<Trainee2Trainer> trainerList= this.trainee2trainerRepo.findByTraineeID(traineeFound.getId());
				
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

	@Override
	public TraineeProfileDTO updateTraineeProfile(String username,TraineeProfileDTO traineeProfile) {
		
		Trainee trainee=this.traineeRepo.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		trainee.getUser().setFirstName(traineeProfile.getFirstName());
		trainee.getUser().setLastName(traineeProfile.getLastName());
		trainee.getUser().setIsActive(traineeProfile.getIsActive());
		trainee.setAddress(traineeProfile.getAddress());
		trainee.setDateOfBirth(traineeProfile.getDateOfBirth());
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
	public List<TrainerDTO> updateTraineTrainers(String username, List<String> trainerUsernames) {
		Trainee trainee=this.traineeRepo.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		List<String> existingTrainers= this.trainee2trainerRepo.findByTraineeID(trainee.getId()).stream().map(t->t.getTrainer().getUser().getUsername()).toList();
		List<TrainerDTO> trainerDTOList=new ArrayList<>();
		
		for (String trainerUsername : existingTrainers) {
			if(!trainerUsernames.contains(trainerUsername)) {
				trainee2trainerRepo.deleteByTraineeAndTrainerUsername(username,trainerUsername);
				trainee2trainerRepo.flush();
			}
		}
		List<Trainee2Trainer> trainerList= this.trainee2trainerRepo.findByTraineeID(trainee.getId());
		for (Trainee2Trainer trainer : trainerList) {
			Trainer traineesTrainer=trainer.getTrainer();
			TrainerDTO trainerDTO=new TrainerDTO(traineesTrainer.getUser().getUsername(),traineesTrainer.getUser().getFirstName(),traineesTrainer.getUser().getLastName(),traineesTrainer.getSpecialization().getTrainingTypeEnum()); 
			trainerDTOList.add(trainerDTO);
		}

		return trainerDTOList;
	}

	@Override
	public List<Object[]> findTraineeTrainingList(String username, LocalDate periodFrom, LocalDate periodTo,
			String trainerName, TrainingTypeEnum trainingType) {
		Trainee trainee=this.traineeRepo.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User","username" , username)); 
		this.trainingRepo.findTraineeTrainingList(username, periodFrom, periodTo, trainerName, trainingType);
		return this.trainingRepo.findTraineeTrainingList(username, periodFrom, periodTo, trainerName, trainingType);
	}

}

