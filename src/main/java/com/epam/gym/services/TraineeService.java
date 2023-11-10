package com.epam.gym.services;

import com.epam.gym.entities.Trainee;
import com.epam.gym.payloads.TraineeProfileDTO;

public interface TraineeService {
	
	TraineeProfileDTO getTraineeProfile(String username);

}
