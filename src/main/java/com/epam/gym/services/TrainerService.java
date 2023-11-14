package com.epam.gym.services;

import com.epam.gym.payloads.TrainerProfileDTO;

public interface TrainerService {
	
	TrainerProfileDTO getTrainerProfile(String username);
}
