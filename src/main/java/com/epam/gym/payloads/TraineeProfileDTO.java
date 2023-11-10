package com.epam.gym.payloads;

import java.time.LocalDate;
import java.util.List;

import com.epam.gym.entities.Trainer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeProfileDTO {
		
	private String firstName;
	
	private String lastName;
	
	private LocalDate dateOfBirth;
	
	private String address;

	private Boolean isActive;
	
	private List<TrainerDTO> trainers;
	
}
