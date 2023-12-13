package com.epam.gym.services;


import com.epam.gym.dto.TrainerDTO;

public interface TrainerService {
	
	TrainerDTO getTrainerProfile(String username);
	
}
