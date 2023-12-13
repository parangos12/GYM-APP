package com.epam.gym.repositories;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epam.gym.entities.Role;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;

@SpringBootTest
@WebAppConfiguration
class TrainingRepositoryTest {
	
    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    
	@Autowired
	private TraineeRepository traineeRepository;
	
	@Autowired
	private TrainerRepository trainerRepository;

	private Trainee trainee;
	private Trainer trainer;
	private User user_trainer;
	private User user_trainee;
	private TrainingType trainingType;
    private Training training;

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		trainingType=new TrainingType(5L, TrainingTypeEnum.stretching);
		user_trainer=new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
		user_trainee=new User(2L, "Jose", "Alberto", "Jose.Arango", "giqXPD782", true, Role.TRAINEE);
		trainee=new Trainee(1L, user_trainee, LocalDate.of(2000, 02, 12), "Bello, Antioquia");
		trainer=new Trainer(1L, user_trainer, trainingType);

		trainingTypeRepository.save(trainingType);
		userRepository.save(user_trainer);
		userRepository.save(user_trainee);
        traineeRepository.save(trainee);
        trainerRepository.save(trainer);
        training = new Training(1L,trainee,trainer,trainingType,"yoga",LocalDate.of(2024, 12, 23),1.5f);
        trainingRepository.save(training);
	}
	
	@AfterEach
	void tearDown() {
		trainingRepository.delete(training);
		traineeRepository.delete(trainee);
		trainerRepository.delete(trainer);
		userRepository.delete(user_trainee);
		userRepository.delete(user_trainer);
		trainingTypeRepository.delete(trainingType);
	}


    @Test
    void testFindTraineeTrainingList() {

        // Perform the test
        List<Object[]> result = trainingRepository.findTraineeTrainingList(
                trainee.getUser().getUsername(),
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1),
                trainer.getUser().getUsername(),
                trainingType.getTrainingTypeEnum()
        );

        // Assert the result
        assertThat(result).isEmpty();
    }

    @Test
    void testFindTrainerTrainingList() {

        // Perform the test
        List<Object[]> result = trainingRepository.findTrainerTrainingList(
                trainer.getUser().getUsername(),
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1),
                trainee.getUser().getUsername()
        );

        // Assert the result
        assertThat(result).isEmpty();
    }

}
