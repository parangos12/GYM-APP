package com.epam.gym.payloads;

import java.time.LocalDate;

import com.epam.gym.entities.TrainingTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingDTO {

		
		private String traineeUsername;
		
		private String trainerUsername;
		
		private TrainingTypeEnum trainingType;

		private String trainingName;
		
		private LocalDate trainingDate;
		
		private Float trainingDuration;
		
	}


