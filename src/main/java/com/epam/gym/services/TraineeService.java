package com.epam.gym.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.repository.query.Param;

import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.payloads.TraineeProfileDTO;
import com.epam.gym.payloads.TrainerDTO;

public interface TraineeService {
	
	TraineeProfileDTO getTraineeProfile(String username);

	TraineeProfileDTO updateTraineeProfile(String username,TraineeProfileDTO traineeProfile);
	
	void deleteTrainee(String username);
	
	List<TrainerDTO> updateTraineTrainers(String username, List<String> trainerUsernames);
	
	List<Object[]> findTraineeTrainingList(String username,LocalDate periodFrom, LocalDate periodTo,
			String trainerName, TrainingTypeEnum trainingType);

	
}