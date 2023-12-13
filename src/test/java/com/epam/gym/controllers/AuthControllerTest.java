package com.epam.gym.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.ArgumentMatchers.eq;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.epam.gym.dto.TrainingDTO;
import com.epam.gym.dto.AuthenticationDTO;
import com.epam.gym.dto.ChangePasswordRequest;
import com.epam.gym.dto.AuthenticationResponse;
import com.epam.gym.dto.TrainingsFilterDTO;
import com.epam.gym.dto.TraineeUpdateTrainersDTO;
import com.epam.gym.dto.TraineeCreateDTO;
import com.epam.gym.dto.TraineeDTO;
import com.epam.gym.dto.TraineeUpdateDTO;
import com.epam.gym.dto.TraineeTrainerDTO;
import com.epam.gym.entities.Role;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;
import com.epam.gym.exceptions.AccessDeniedException;
import com.epam.gym.repositories.TokenRepository;
import com.epam.gym.repositories.Trainee2TrainerRepository;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.repositories.TrainingTypeRepository;
import com.epam.gym.repositories.UserRepository;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.epam.gym.services.AuthenticationService;
import com.epam.gym.services.impl.AuthenticationServiceImpl;
import com.epam.gym.services.impl.LogoutService;
import com.epam.gym.services.impl.TraineeServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import com.epam.gym.services.impl.CloudWatchLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@WebAppConfiguration
class AuthControllerTest {

    private final static String BASE_URL = "/api/v1/auth";

    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private LogoutService logoutService;
    
    @MockBean
    private CloudWatchLogger cloudWatchLogger;

    
    private ObjectMapper objectMapper=new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService, logoutService,cloudWatchLogger)).build();
    }

    @Test
    void testAuthenticateTrainee() throws Exception {
        // Given
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("username", "password");
        when(authService.authenticate(authenticationDTO)).thenReturn(AuthenticationResponse.builder().token("mockedToken").build());

        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationDTO)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void testChangePassword() throws Exception {
        String username = "testUser";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("giqXPD78", "newPassword", "newPassword");

        // Mock authentication
		 UserDetails user = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINEE);
		 Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

	    doNothing().when(authService).changePassword(eq(username), any(ChangePasswordRequest.class), (Principal) eq(authentication.getPrincipal()));

        MvcResult result = mockMvc.perform(patch(BASE_URL + "/{username}/changePassword", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testLogout() throws Exception {
        MvcResult result = mockMvc.perform(post(BASE_URL + "/logout"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }
}
