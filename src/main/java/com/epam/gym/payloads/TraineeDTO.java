package com.epam.gym.payloads;

import java.time.LocalDate;

import com.epam.gym.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeDTO {
	
	private Long id;
	
	private User user;
	
	private LocalDate dateOfBirth;
	
	private String address;

}
