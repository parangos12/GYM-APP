package com.epam.gym.dto.trainer;

import java.util.List;

import com.epam.gym.entities.TrainingTypeEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class TrainerUpdateDTO {
	
    @NotNull(message = "First name cannot be null")
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "Firstname must be between 1 and 50 characters")
	private String firstName;
    
    @NotNull(message = "Last name cannot be null")
    @Size(min = 1, max = 50, message = "Lastname must be between 1 and 50 characters")
	private String lastName;
    
    @NotNull(message="Specialization is required")
    @Pattern(regexp = "fitness|yoga|zumba|stretching|resistance", message = "Invalid training type")
	private String specialization;
	
	@NotNull(message = "Active status is required")
	private Boolean isActive;
}





