package com.epam.gym.util;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.epam.gym.dto.trainee.*;
import com.epam.gym.repositories.TrainerRepository;

public class TrainerUsernamesValidator implements ConstraintValidator<ValidTrainerUsernames, List<String>> {

	
	private final TrainerRepository trainerRepo;

	public TrainerUsernamesValidator(TrainerRepository trainerRepo) {
		this.trainerRepo = trainerRepo;}

	@Override
	public boolean isValid(List<String> usernames, ConstraintValidatorContext context) {
	    return usernames.stream()
	            .allMatch(username -> !trainerRepo.findTrainerByUsername(username).isEmpty());
	}

}
