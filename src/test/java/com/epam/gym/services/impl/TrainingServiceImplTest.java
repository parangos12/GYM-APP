package com.epam.gym.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epam.gym.dto.TrainingDTO;
import com.epam.gym.entities.Role;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.repositories.TrainingTypeRepository;

class TrainingServiceImplTest {

	@InjectMocks
	private TrainingServiceImpl trainingService;
	
	@Mock
	private  TrainingRepository trainingRepo;

	@Mock
	private  TrainerRepository trainerRepo;

	@Mock
	private  TraineeRepository traineeRepo;

	@Mock
	private  TrainingTypeRepository trainingTypeRepo;

	
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
		user_trainee=new User(2L, "Jose", "Alberto", "Jose.Arango", "giqXPD789", true, Role.TRAINEE);
		trainee=new Trainee(1L, user_trainee, LocalDate.of(2000, 02, 12), "Bello, Antioquia");
		trainer=new Trainer(2L, user_trainer, trainingType);
	}

	@Test
	void testSaveTraining_WhenTraineeTrainerExist() {
		when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
		when(trainerRepo.findTrainerByUsername(anyString())).thenReturn(Optional.of(trainer));
		TrainingDTO trainingDTO=new TrainingDTO(trainee.getUser().getUsername(),trainer.getUser().getUsername(),
				trainer.getSpecialization().getTrainingTypeEnum().name(),"Cycling class",LocalDate.of(2023, 12, 12),1.5f);
		TrainingTypeEnum trainingTypeEnum = TrainingTypeEnum.valueOf(trainingType.getTrainingTypeEnum().toString());
		when(trainingTypeRepo.findTrainingTypeBy(trainingTypeEnum)).thenReturn(trainingType);
		Training trainingToSave=new Training(1L, trainee, trainer, trainingType, "Cycling class",LocalDate.of(2023, 12, 12),1.5f);
		when(trainingRepo.save(trainingToSave)).thenReturn(trainingToSave);
		trainingService.saveTraining(trainingDTO);
		Training trainingSaved=trainingRepo.save(trainingToSave);
		assertEquals(trainingSaved.getTrainee().getUser().getUsername(), trainingDTO.getTraineeUsername());
	}

	@Test
	void testSaveTraining_WhenTraineeNotExist() {
		TrainingDTO trainingDTO=new TrainingDTO("Random User",trainer.getUser().getUsername(),
				trainer.getSpecialization().getTrainingTypeEnum().name(),"Cycling class",LocalDate.of(2023, 12, 12),1.5f);
		ResourceNotFoundException expectedException=assertThrows(ResourceNotFoundException.class, ()->{
			trainingService.saveTraining(trainingDTO);
		});
		assertEquals("User not found with username : Random User",expectedException.getMessage());
	}
	
	@Test
	void testSaveTraining_WhenTrainerNotExist() {
		when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
		TrainingDTO trainingDTO=new TrainingDTO(trainee.getUser().getUsername(),"Random User",
				trainer.getSpecialization().getTrainingTypeEnum().name(),"Cycling class",LocalDate.of(2023, 12, 12),1.5f);
		ResourceNotFoundException expectedException=assertThrows(ResourceNotFoundException.class, ()->{
			trainingService.saveTraining(trainingDTO);
		});
		assertEquals("User not found with username : Random User",expectedException.getMessage());
	}
	
	
}






