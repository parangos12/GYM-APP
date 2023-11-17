package com.epam.gym.dto.trainer;

import java.time.LocalDate;
import java.util.List;

import com.epam.gym.dto.trainee.TraineeBasicProfileDTO;
import com.epam.gym.entities.TrainingTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerUpdatedDTO {
	
	private String username;
	
	private String firstName;
	
	private String lastName;
	
	private TrainingTypeEnum specialization;

	private Boolean isActive;
	
	private List<TraineeBasicProfileDTO> trainees;

}
