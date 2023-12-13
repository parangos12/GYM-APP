package com.epam.gym.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TraineeCreateDTO {
	
	@NotBlank(message = "First name cannot be blank")
    @NotNull(message = "First name cannot be null")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
	private String firstName;
	
    @NotNull(message = "Last name cannot be null")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
	@NotBlank(message = "Last name cannot be blank")
	private String lastName;
	
    
	@DateTimeFormat(pattern="yyyy-mm-dd")
	@Past(message="Date must be in the past")
	private LocalDate dateOfBirth;
	
    @Size(min = 2, max = 250, message = "Address must be between 2 and 250 characters")
	private String address;
	
}
