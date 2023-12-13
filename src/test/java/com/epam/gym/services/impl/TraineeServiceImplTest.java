package com.epam.gym.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.epam.gym.config.JwtService;
import com.epam.gym.dto.TraineeDTO;
import com.epam.gym.dto.TraineeCreateDTO;
import com.epam.gym.repositories.TokenRepository;
import com.epam.gym.repositories.Trainee2TrainerRepository;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.repositories.UserRepository;
import com.epam.gym.dto.TraineeTrainerDTO;
import com.epam.gym.dto.TraineeUpdateDTO;
import com.epam.gym.entities.Role;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;
import com.epam.gym.exceptions.ResourceNotFoundException;

import java.util.*;
import com.epam.gym.dto.TraineeUpdateTrainersDTO;
import com.epam.gym.dto.TrainingsFilterDTO;
import com.epam.gym.dto.UserCredentialsDTO;

class TraineeServiceImplTest {
	
	@InjectMocks
	private TraineeServiceImpl traineeService;
	
	@Mock
	private  TraineeRepository traineeRepo;
	
	@Mock
	private  TrainerRepository trainerRepo;
	
	@Mock
	private  TrainingRepository trainingRepo;
	
	@Mock
	private  Trainee2TrainerRepository trainee2trainerRepo;
	
	@Mock
	private  UserRepository userRepo;
	
	@Mock
	private  PasswordEncoder passwordEncoder;
	
	@Mock
	private  JwtService jwtService;
	
	@Mock
	private  TokenRepository tokenRepository;

	//Global variables for testing methods.
	private TraineeDTO traineeDTO;
	private List<TraineeTrainerDTO> traineeTrainers;
	private Trainee trainee;
	private Trainer trainer;
	private TrainingType trainingType;
	Trainee2Trainer trainee2Trainer;
	private User user;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		traineeTrainers=new ArrayList<>();
		trainingType=new TrainingType(1L, TrainingTypeEnum.stretching);
		traineeDTO=new TraineeDTO("Pedro", "Arango", LocalDate.parse("2000-02-12"), "Bello, Antioquia", true, traineeTrainers);
		user=new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINEE);
		trainee=new Trainee(1L,user,LocalDate.parse("2000-02-12"),"Bello, Antioquia");
		trainer=new Trainer(1L, user, trainingType);
		trainee2Trainer=new Trainee2Trainer(1L, trainee, trainer);
	}

	@Test
	void testGetTraineeProfile_WhenTrainersListIsEmpty() {
		when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
		when(trainee2trainerRepo.findByTraineeID(anyLong())).thenReturn(Collections.emptyList());
		TraineeDTO result=traineeService.getTraineeProfile(anyString());
		assertNotNull(result);
	}
	
	@Test
	void testGetTraineeProfile_WhenTrainersListIsNotEmpty() {
		when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
		List<Trainee2Trainer> trainerList=new ArrayList<>(Arrays.asList(trainee2Trainer));
		when(trainee2trainerRepo.findByTraineeID(anyLong())).thenReturn(trainerList);
		TraineeDTO result=traineeService.getTraineeProfile(anyString());
		assertNotNull(result);
	}

	@Test
	void testGetTraineeProfile_WhenUserNotFound() {
		ResourceNotFoundException expectedException=assertThrows(ResourceNotFoundException.class, ()->{
			traineeService.getTraineeProfile(anyString());
		});
		assertEquals("User not found with username : ",expectedException.getMessage());
	}	
	
	@Test
	void testUpdateTraineeProfile_WhenUserFound() {
		when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
		TraineeUpdateDTO traineeUpdateDTO=new TraineeUpdateDTO("Jesus", "Nazareth", LocalDate.of(1, 1, 24), "Belen rincon", true);
		TraineeDTO traineeUpdated=traineeService.updateTraineeProfile(trainee.getUser().getUsername(), traineeUpdateDTO);
		assertNotNull(traineeUpdated);
	}
	
	@Test
	void testUpdateTraineeProfile_WhenUserNotFound() {
		ResourceNotFoundException expectedException=assertThrows(ResourceNotFoundException.class, ()->{
			traineeService.updateTraineeProfile("Random User", null);
		});
		assertEquals("User not found with username : Random User",expectedException.getMessage());
	}	

	@Test
	void testDeleteTrainee_WhenUserFound() {
	    when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
	    
	    traineeService.deleteTrainee(trainee.getUser().getUsername());
	    
	    verify(traineeRepo).save(trainee);
	    assertFalse(trainee.getUser().getIsActive());
	}
	
	@Test
	void testDeleteTrainee_WhenUserNotFound() {
		ResourceNotFoundException expectedException=assertThrows(ResourceNotFoundException.class, ()->{
			traineeService.deleteTrainee("Random User");
		});
		assertEquals("User not found with username : Random User",expectedException.getMessage());
	}
	

	@Test
	void testUpdateTraineTrainers_WhenHaveTrainersToDelete() {
	    when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
	    when(trainee2trainerRepo.findByTraineeID(anyLong())).thenReturn(List.of(trainee2Trainer));
	    
	    List<String> trainerUsernames = List.of("trainer1");
	    TraineeUpdateTrainersDTO updateDTO = new TraineeUpdateTrainersDTO(trainerUsernames);
	    
	    List<TraineeTrainerDTO> result = traineeService.updateTraineTrainers(trainee.getUser().getUsername(), updateDTO);
	    
	    assertNotNull(result);
	    verify(trainee2trainerRepo).deleteByTraineeAndTrainerUsername(anyString(), anyString());
	}

	@Test
	void testUpdateTraineeProfile_WhenNotHaveTrainersToDelete() {
	    when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
	    when(trainee2trainerRepo.findByTraineeID(anyLong())).thenReturn(List.of(trainee2Trainer));
	    List<String> trainerUsernames =List.of(trainer.getUser().getUsername());
	    TraineeUpdateTrainersDTO updateDTO = new TraineeUpdateTrainersDTO(trainerUsernames);
	    
	    List<TraineeTrainerDTO> result = traineeService.updateTraineTrainers(trainee.getUser().getUsername(), updateDTO);
	    
	    assertNotNull(result);
	}
	
	@Test
	void testUpdateTraineeProfile__WhenUserNotFound() {

	    List<String> trainerUsernames =List.of(trainer.getUser().getUsername());
	    TraineeUpdateTrainersDTO updateDTO = new TraineeUpdateTrainersDTO(trainerUsernames);
		
	    ResourceNotFoundException expectedException=assertThrows(ResourceNotFoundException.class, ()->{
			traineeService.updateTraineTrainers("Random User", updateDTO);
		});
		assertEquals("User not found with username : Random User",expectedException.getMessage());
	}

	@Test
	void testFindTraineeTrainingList_WhenUserFound() {
		when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
		TrainingsFilterDTO trainingsFilterDTO = new TrainingsFilterDTO(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-12-31"), "Trainer","Trainee", "fitness");
	    when(trainingRepo.findTraineeTrainingList(trainee.getUser().getUsername(),
	    		LocalDate.parse("2023-01-01"), LocalDate.parse("2023-12-31"), 
	    		"Trainer", TrainingTypeEnum.fitness)).thenReturn(Collections.emptyList());
		List<Object[]> traineeTrainingsList=traineeService.findTraineeTrainingList(trainee.getUser().getUsername(), trainingsFilterDTO);
		assertNotNull(traineeTrainingsList);
	}
	@Test
	void testFindTraineeTrainingList_WhenSpecializationNull() {
		when(traineeRepo.findByUsername(anyString())).thenReturn(Optional.of(trainee));
		TrainingsFilterDTO trainingsFilterDTO = new TrainingsFilterDTO(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-12-31"), "Trainer","Trainee", null);
	    when(trainingRepo.findTraineeTrainingList(trainee.getUser().getUsername(),
	    		LocalDate.parse("2023-01-01"), LocalDate.parse("2023-12-31"), 
	    		"Trainer", null)).thenReturn(Collections.emptyList());
		List<Object[]> traineeTrainingsList=traineeService.findTraineeTrainingList(trainee.getUser().getUsername(), trainingsFilterDTO);
		assertNotNull(traineeTrainingsList);
	}		

	@Test
	void testRegisterNewTrainee() {
        TraineeCreateDTO traineeCreateDTO = new TraineeCreateDTO("John", "Doe", LocalDate.parse("1990-01-01"), "New York");

        when(traineeRepo.save(any())).thenReturn(new Trainee());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any())).thenReturn("jwtToken");
        UserCredentialsDTO result = traineeService.registerNewTrainee(traineeCreateDTO);

        assertNotNull(result);
        verify(tokenRepository, times(1)).save(any());

	}

	@Test
	void testGenerateUserName_WhenUserNotExist() {
        String firstName = "Pedro";
        String lastName = "Arango";

        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        String result = traineeService.generateUserName(firstName, lastName);

        assertNotNull(result);
	}

	@Test
	void testGenerateUserName_WhenUserExist() {
        String firstName = "Pedro";
        String lastName = "Arango";
        String generatedUserName = "Pedro.Arango";
        
        String result = traineeService.generateUserName(firstName, lastName);
        when(userRepo.findByUsername(generatedUserName)).thenReturn(Optional.of(user));

        result = traineeService.generateUserName(firstName, lastName);

        // Ensure the result is a modified username with a serial number
        assertEquals("Pedro.Arango_1", result);
	}
	
	
	@Test
	void testGenerateRandomPassword() {
        String result = traineeService.generateRandomPassword();

        assertNotNull(result);
        assertEquals(10, result.length());
	}

}
