package com.epam.gym.dto.trainee;

import java.time.LocalDate;
import java.util.List;

import com.epam.gym.dto.trainer.TrainerDTO;

import io.micrometer.common.lang.Nullable;
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
