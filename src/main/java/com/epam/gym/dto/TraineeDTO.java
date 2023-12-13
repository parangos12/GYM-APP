package com.epam.gym.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeDTO {
	
	private String firstName;
	
	private String lastName;
	
	private LocalDate dateOfBirth;
	
	private String address;

	
	private Boolean isActive;
	
	private List<TraineeTrainerDTO> trainers;

}
