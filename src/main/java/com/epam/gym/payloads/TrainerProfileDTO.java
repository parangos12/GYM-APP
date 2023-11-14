package com.epam.gym.payloads;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerProfileDTO {
	
	private String firstName;
	
	private String lastName;
	
	private String specialization;

	private Boolean isActive;
	
	private List<TraineeDTO> trainees;

}
