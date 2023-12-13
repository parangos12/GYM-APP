package com.epam.gym.dto;

import java.util.List;
import com.epam.gym.util.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeUpdateTrainersDTO {
	
	@ValidTrainerUsernames
	@NotNull(message = "Trainer usernames list is required")
	@NotEmpty(message = "Trainer usernames cannot be empty") 
	private List<String> trainerUsernames;
	
}
