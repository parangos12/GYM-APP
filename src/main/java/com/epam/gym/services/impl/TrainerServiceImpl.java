package com.epam.gym.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.dto.TraineeBasicProfileDTO;
import com.epam.gym.dto.TrainerDTO;
import com.epam.gym.entities.Trainee;
import com.epam.gym.repositories.Trainee2TrainerRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.services.TrainerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService{
	
	private final TrainerRepository trainerRepo;
	private final Trainee2TrainerRepository trainee2trainerRepo;

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
}
