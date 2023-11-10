package com.epam.gym.payloads;

import java.time.LocalDate;

import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerDTO {
	
	private String username;

	private String firstName;
	
	private String lastName;
	
	private TrainingTypeEnum trainingTypeEnum;

}
