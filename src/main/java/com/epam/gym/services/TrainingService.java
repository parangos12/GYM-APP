package com.epam.gym.services;

import com.epam.gym.dto.TrainingDTO;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;

public interface TrainingService {
	
	void saveTraining(TrainingDTO trainingDTO);

}
