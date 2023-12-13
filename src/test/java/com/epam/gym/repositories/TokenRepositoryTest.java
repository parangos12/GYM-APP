package com.epam.gym.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
import com.epam.gym.entities.Token;
import com.epam.gym.entities.TokenType;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.User;

@SpringBootTest
@WebAppConfiguration
class TokenRepositoryTest {
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private static final String TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
	private User user;
	private Token token;

	MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@BeforeEach
	void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		user=new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINEE);
		token=new Token(1L,TOKEN, TokenType.BEARER,false,false,user);

		}
	
	@Test
	void testFindAllValidTokenByUser() {
		userRepository.save(user);
		Token tokenSaved=tokenRepository.save(token);
		assertThat(tokenSaved).isNotNull();
	}

	@Test
	void testFindByToken() {
		tokenRepository.delete(token);
		userRepository.delete(user);
		userRepository.save(user);
		Token tokenSaved=tokenRepository.save(token);
		Optional<Token> tokenFound=tokenRepository.findByToken(tokenSaved.getToken());
		assertThat(tokenFound).isNotNull();
	}

}
