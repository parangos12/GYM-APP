package com.epam.gym.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epam.gym.entities.Role;
import com.epam.gym.entities.User;

@SpringBootTest
@WebAppConfiguration
class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;

	private User user;

	MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		user=new User(2L, "Jose", "Alberto", "Jose.Arango", "giqXPD782", true, Role.TRAINEE);
		userRepository.save(user);

	}

	@Test
	void testFindByUsername() {
		Optional<User> optUser=userRepository.findByUsername(user.getUsername());
		assertNotNull(optUser);
	}

}
