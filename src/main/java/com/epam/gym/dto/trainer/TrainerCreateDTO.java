package com.epam.gym.dto.trainer;

import com.epam.gym.entities.TrainingTypeEnum;

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
public class TrainerCreateDTO {
	
	@NotBlank(message = "First name cannot be blank")
    @NotNull(message = "First name cannot be null")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
	private String firstName;
	
    @NotNull(message = "Last name cannot be null")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
	@NotBlank(message = "Last name cannot be blank")
	private String lastName;
	
    @NotNull(message="Specialization is required")
    @Pattern(regexp = "fitness|yoga|zumba|stretching|resistance", message = "Invalid training type")
	private String specialization;
}
