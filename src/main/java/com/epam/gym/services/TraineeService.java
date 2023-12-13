package com.epam.gym.services;

import java.util.List;
import com.epam.gym.dto.TraineeCreateDTO;
import com.epam.gym.dto.TraineeDTO;
import com.epam.gym.dto.TraineeTrainerDTO;
import com.epam.gym.dto.TraineeUpdateDTO;
import com.epam.gym.dto.TraineeUpdateTrainersDTO;
import com.epam.gym.dto.TrainingsFilterDTO;
import com.epam.gym.dto.UserCredentialsDTO;

public interface TraineeService {
	
	TraineeDTO getTraineeProfile(String username);

	TraineeDTO updateTraineeProfile(String username,TraineeUpdateDTO traineeUpdateDTO);
	
	UserCredentialsDTO registerNewTrainee(TraineeCreateDTO traineeCreateDTO);
	
	void deleteTrainee(String username);
	
	List<TraineeTrainerDTO> updateTraineTrainers(String username, TraineeUpdateTrainersDTO traineeUpdateTrainersDTO);
		
	List<Object[]> findTraineeTrainingList(String username,TrainingsFilterDTO trainingsFilterDTO);

}