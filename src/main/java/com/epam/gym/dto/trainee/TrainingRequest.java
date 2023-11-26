package com.epam.gym.dto.trainee;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingRequest {
	
	private String trainerUsername;
	
	private String trainerFirstName;

	private String trainerLastName;
	
	private Boolean isActive;

	private LocalDate trainingDate;
	
	private Float trainingDuration;
	
    private String trainingTypeEnum;
	
	private String actionType;

	@Override
	public String toString() {
		return  trainerUsername + "," + trainerFirstName
				+ "," + trainerLastName + "," + isActive + "," + trainingDate
				+ "," + trainingDuration + "," + trainingTypeEnum + ","
				+ actionType;
	}
	
	
}
