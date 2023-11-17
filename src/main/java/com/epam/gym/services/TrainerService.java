package com.epam.gym.services;

import java.time.LocalDate;
import java.util.List;

import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.dto.trainee.TrainingsFilterDTO;
import com.epam.gym.dto.trainer.TrainerCreateDTO;
import com.epam.gym.dto.trainer.TrainerDTO;
import com.epam.gym.dto.trainer.TrainerUpdateDTO;
import com.epam.gym.dto.trainer.TrainerUpdatedDTO;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.TrainingTypeEnum;

public interface TrainerService {
	
	TrainerDTO getTrainerProfile(String username);
	
	TrainerUpdatedDTO updateTrainerProfile(String username,TrainerUpdateDTO trainerUpdateProfile);

	List<Object[]> getTrainerTrainingList(String username,TrainingsFilterDTO trainingsFilterDTO);
	
	List<Object[]> findActiveTrainersWithNoTrainees();
	
	UserCredentialsDTO registerNewTrainer(TrainerCreateDTO trainerCreateDTO);

}
