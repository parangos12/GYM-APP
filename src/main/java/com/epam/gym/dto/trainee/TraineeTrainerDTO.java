package com.epam.gym.dto.trainee;

import java.util.List;

import com.epam.gym.dto.trainer.TrainerDTO;
import com.epam.gym.entities.TrainingTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeTrainerDTO {
	
	private String username;
	
	private String firstName;
	
	private String lastName;
	
	private TrainingTypeEnum specialization;
}
