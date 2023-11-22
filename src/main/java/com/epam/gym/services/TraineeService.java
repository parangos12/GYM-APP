package com.epam.gym.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.repository.query.Param;

import com.epam.gym.dto.AuthenticationResponse;
import com.epam.gym.dto.UserCredentialsDTO;
import com.epam.gym.dto.trainee.TraineeCreateDTO;
import com.epam.gym.dto.trainee.TraineeDTO;
import com.epam.gym.dto.trainee.TraineeTrainerDTO;
import com.epam.gym.dto.trainee.TraineeUpdateDTO;
import com.epam.gym.dto.trainee.TraineeUpdateTrainersDTO;
import com.epam.gym.dto.trainee.TrainingsFilterDTO;
import com.epam.gym.dto.trainer.TrainerDTO;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;

public interface TraineeService {
	
	TraineeDTO getTraineeProfile(String username);

	TraineeDTO updateTraineeProfile(String username,TraineeUpdateDTO traineeUpdateDTO);
	
	UserCredentialsDTO registerNewTrainee(TraineeCreateDTO traineeCreateDTO);
	
	void deleteTrainee(String username);
	
	List<TraineeTrainerDTO> updateTraineTrainers(String username, TraineeUpdateTrainersDTO traineeUpdateTrainersDTO);
		
	List<Object[]> findTraineeTrainingList(String username,TrainingsFilterDTO trainingsFilterDTO);

}