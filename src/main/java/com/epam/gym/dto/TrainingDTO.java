package com.epam.gym.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingDTO {

		@NotBlank(message = "Trainee username cannot be blank")
	    @NotNull(message = "Trainee username cannot be null")
	    @Size(min = 1, max = 50, message = "Trainee username must be between 1 and 50 characters")
		private String traineeUsername;
		
		@NotBlank(message = "Trainer username cannot be blank")
	    @NotNull(message = "Trainer username cannot be null")
	    @Size(min = 1, max = 50, message = "Trainer username must be between 1 and 50 characters")
		private String trainerUsername;
		
	    @NotNull(message="Specialization is required")
	    @Pattern(regexp = "fitness|yoga|zumba|stretching|resistance", message = "Invalid training type")
		private String specialization;
		
		@NotBlank(message = "Training date cannot be blank")
	    @NotNull(message = "Training date cannot be null")
	    @Size(min = 1, max = 50, message = "Training date must be between 1 and 50 characters")
		private String trainingName;
		
		@DateTimeFormat(pattern="yyyy-mm-dd")
		@FutureOrPresent(message="Date can't be in the past")
		private LocalDate trainingDate;
		
	    @NotNull(message = "Training duration is required")
	    @Min(value = 0, message = "Training duration must be greater than or equal to 0")
	    @Max(value = 24, message = "Training duration must be less than or equal to 24")
		private Float trainingDuration;
		
	}


