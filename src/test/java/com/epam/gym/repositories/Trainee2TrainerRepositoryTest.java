package com.epam.gym.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epam.gym.entities.Role;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

@SpringBootTest
@WebAppConfiguration
class Trainee2TrainerRepositoryTest {

	@Autowired
	private Trainee2TrainerRepository trainee2TrainerRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TokenRepository tokenRepository;
	
	
	@Autowired
	private TraineeRepository traineeRepository;
	
	@Autowired
	private TrainerRepository trainerRepository;

	@Autowired
	private TrainingTypeRepository trainingTypeRepository;
	
	private Trainee2Trainer trainee2Trainer;
	private Trainee trainee;
	private Trainer trainer;
	private User user_trainer;
	private User user_trainee;
	private TrainingType trainingType;
	private Trainee2Trainer trainee2TrainerSaved;
	
	MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@BeforeEach
	void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		trainingType=new TrainingType(5L, TrainingTypeEnum.stretching);
		user_trainer=new User(1L, "Pedros", "Arangofd", "Pedros.Arango", "giqXPD78", true, Role.TRAINER);
		user_trainee=new User(2L, "Joseq", "Albertoq", "Joseq.Arangoq", "giqXPD7832", true, Role.TRAINEE);
		trainee=new Trainee(1L, user_trainee, LocalDate.of(2000, 02, 12), "Bello, Antioquia");
		trainer=new Trainer(2L, user_trainer, trainingType);
		trainee2Trainer=new Trainee2Trainer(1L, trainee, trainer);

		trainingTypeRepository.save(trainingType);
		userRepository.save(user_trainer);
		userRepository.save(user_trainee);
		trainee2TrainerSaved=trainee2TrainerRepository.save(trainee2Trainer);
		}
	
	@AfterEach
	void tearDown() {
	    tokenRepository.deleteAll();       
	    trainee2TrainerRepository.deleteAll(); 
	    traineeRepository.deleteAll(); 
	    trainerRepository.deleteAll();         
	    userRepository.deleteAll();            
	    trainingTypeRepository.deleteAll();
	}

	@Test
	void testFindByTraineeID() {
		List<Trainee2Trainer> trainee2TrainerList=trainee2TrainerRepository.findByTraineeID(trainee2TrainerSaved.getTrainee().getId());
		assertNotNull(trainee2TrainerList);
	}

	@Test
	void testFindByTrainerID() {
		List<Trainee2Trainer> trainee2TrainerList=trainee2TrainerRepository.findByTrainerID(trainee2TrainerSaved.getTrainer().getId());
		assertNotNull(trainee2TrainerList);
	}

	@Test
	void testFindByUsername() {
		Optional<Trainee2Trainer> optTrainee2Trainer=trainee2TrainerRepository.findByUsername(trainee.getUser().getUsername());
		assertNotNull(optTrainee2Trainer);
	}


	@Test
	void testFindActiveTrainersWithNoTrainees() {
		List<Object[]> activeTrainersWithNoTrainees=trainee2TrainerRepository.findActiveTrainersWithNoTrainees();
		assertThat(activeTrainersWithNoTrainees).isEmpty();
	}
	
	@Test
	void testDeleteByTraineeAndTrainerUsername() {
		trainee2TrainerRepository.delete(trainee2Trainer);
		Optional<Trainee2Trainer> optTrainee2Trainer=trainee2TrainerRepository.findByUsername(trainee.getUser().getUsername());
		assertThat(optTrainee2Trainer).isEmpty();
	}

}
