package com.epam.gym.services.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.epam.gym.entities.Trainee;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.services.TraineeService;

public class TraineeServiceImpl implements TraineeService{
	
	private final TraineeRepository traineeRepo;

	@Autowired
	public TraineeServiceImpl(TraineeRepository traineeRepo) {
		this.traineeRepo = traineeRepo;
	}

	@Override
	public Trainee getTraineeByUsername(String username) {
		return traineeRepo.findByUsername(username);
	}

}
