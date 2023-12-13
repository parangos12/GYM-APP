package com.epam.gym.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epam.gym.dto.TraineeDTO;
import com.epam.gym.dto.TrainerDTO;
import com.epam.gym.entities.Role;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.repositories.Trainee2TrainerRepository;
import com.epam.gym.repositories.TrainerRepository;

class TrainerServiceImplTest {

	@InjectMocks
	private TrainerServiceImpl trainerService;
	
	@Mock
	private  TrainerRepository trainerRepo;
	
	@Mock
	private  Trainee2TrainerRepository trainee2trainerRepo;
	
	private Trainee2Trainer trainee2Trainer;
	private Trainee trainee;
	private Trainer trainer;
	private User user_trainer;
	private User user_trainee;
	private TrainingType trainingType;

	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		trainingType=new TrainingType(5L, TrainingTypeEnum.stretching);
		user_trainer=new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
		user_trainee=new User(2L, "Jose", "Alberto", "Jose.Arango", "giqXPD782", true, Role.TRAINEE);
		trainee=new Trainee(1L, user_trainee, LocalDate.of(2000, 02, 12), "Bello, Antioquia");
		trainer=new Trainer(2L, user_trainer, trainingType);
		trainee2Trainer=new Trainee2Trainer(1L, trainee, trainer);
	}

	@Test
	void testGetTrainerProfile_WhenTrainersListIsEmpty() {
		when(trainerRepo.findTrainerByUsername(anyString())).thenReturn(Optional.of(trainer));
		when(trainee2trainerRepo.findByTrainerID(anyLong())).thenReturn(Collections.emptyList());
		TrainerDTO trainerProfile=trainerService.getTrainerProfile(anyString());
		assertNotNull(trainerProfile);
	}
	
	@Test
	void testGetTrainerProfile_WhenTrainersListIsNotEmpty() {
		when(trainerRepo.findTrainerByUsername(anyString())).thenReturn(Optional.of(trainer));
		List<Trainee2Trainer> trainerList=new ArrayList<>(Arrays.asList(trainee2Trainer));
		when(trainee2trainerRepo.findByTrainerID(anyLong())).thenReturn(trainerList);
		TrainerDTO trainerProfile=trainerService.getTrainerProfile(anyString());
		assertNotNull(trainerProfile);
	}

	@Test
	void testGetTrainerProfile_WhenUserNotFound() {
		ResourceNotFoundException expectedException=assertThrows(ResourceNotFoundException.class, ()->{
			trainerService.getTrainerProfile(anyString());
		});
		assertEquals("User not found with username : ",expectedException.getMessage());
	}
	
	

}
