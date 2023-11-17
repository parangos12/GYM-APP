package com.epam.gym.dto.trainee;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeBasicProfileDTO {
	
	private String username;

	private String firstName;
	
	private String lastName;

}
