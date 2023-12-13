package com.epam.gym.repositories;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;

@SpringBootTest
@WebAppConfiguration
class TrainingTypeRepositoryTest {

	@Autowired
	private TrainingTypeRepository trainingTypeRepository;
	
	@Autowired
	private TrainerRepository trainerRepository;
	
	private TrainingType trainingType;
	
	MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		trainerRepository.deleteAll();
		trainingTypeRepository.deleteAll();
		trainingType=trainingTypeRepository.save(new TrainingType(5L, TrainingTypeEnum.stretching));
	}
	
	@Test
	void testFindTrainingTypeBy() {
		TrainingType optTrainingType=trainingTypeRepository.findTrainingTypeBy(trainingType.getTrainingTypeEnum());
		assertNotNull(trainingType);
	}

}
