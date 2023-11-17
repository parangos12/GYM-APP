package com.epam.gym.dto.trainee;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.epam.gym.entities.TrainingTypeEnum;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record TrainingsFilterDTO(
		
		@DateTimeFormat(pattern="yyyy-mm-dd")
		@Past(message="Date must be in the past")
		LocalDate periodFrom,
		
		@DateTimeFormat(pattern="yyyy-mm-dd")
		@Past(message="Date must be in the past")
		LocalDate periodTo,
		
		String trainerName,

		String traineeName,
		
	    @Pattern(regexp = "fitness|yoga|zumba|stretching|resistance", message = "Invalid training type")
		String specialization
		
){}
