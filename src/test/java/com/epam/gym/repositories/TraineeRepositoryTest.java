package com.epam.gym.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
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
import com.epam.gym.entities.User;

@SpringBootTest
@WebAppConfiguration
class TraineeRepositoryTest {

	@Autowired
	private TraineeRepository traineeRepository;
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private Trainee trainee;
	private User user;

	MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@BeforeEach
	void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
}
		
	@AfterEach
	void tearDown() {
		traineeRepository.deleteAll();
		tokenRepository.deleteAll();
		userRepository.deleteAll();
		}	
	
	@Test
	void testFindByUserID() {
		User user=new User(100L, "Jesus", "Arango", "Jessus.Arango", "giqXPD7811", true, Role.TRAINEE);
		Trainee trainee=new Trainee(100L, user, LocalDate.of(2000, 02, 13), "Jardin, Antioquia");
		traineeRepository.save(trainee);
		Optional<Trainee> optTrainee=traineeRepository.findByUserID(1L);
		assertThat(optTrainee).isNotNull();
	}

	@Test
	void testFindByUsername() {
		User user=new User(101L, "Jesusa", "Arango", "Jessusa.Arango", "giqXPD78112", true, Role.TRAINEE);
		Trainee trainee=new Trainee(101L, user, LocalDate.of(2001, 02, 13), "Jardin, Antioquia");
		traineeRepository.save(trainee);
		Optional<Trainee> optTrainee=traineeRepository.findByUsername(trainee.getUser().getUsername());
		assertThat(optTrainee).isNotNull();
	}

}
