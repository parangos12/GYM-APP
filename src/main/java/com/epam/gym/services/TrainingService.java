package com.epam.gym.services;

import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.payloads.TrainingDTO;

public interface TrainingService {
	
	void saveTraining(TrainingDTO trainingDTO);

}
