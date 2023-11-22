package com.epam.gym.dto;

import java.time.LocalDate;

import com.epam.gym.dto.trainee.TraineeCreateDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDTO {
	
	private String username;
	private String password;
}
