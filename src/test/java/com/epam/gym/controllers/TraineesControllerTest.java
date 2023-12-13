package com.epam.gym.controllers;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.ArgumentMatchers.eq;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
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
import com.epam.gym.dto.TrainingRequest;
import com.epam.gym.dto.TrainerDTO;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.epam.gym.services.AuthenticationService;
import com.epam.gym.services.TraineeService;
import com.epam.gym.services.TrainerService;
import com.epam.gym.services.TrainingService;
import com.epam.gym.services.impl.AuthenticationServiceImpl;
import com.epam.gym.services.impl.TraineeServiceImpl;
import com.epam.gym.services.impl.TrainerServiceImpl;

import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;


@SpringBootTest
@WebAppConfiguration
class TraineesControllerTest {
	
	private final static String BASE_URL="/api/v1/trainees";
	
	MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@InjectMocks
	private AuthenticationServiceImpl authService;
	
	@Autowired
	private TrainingTypeRepository trainingTypeRepository;
	
	@Autowired
	private TrainingRepository trainingRepository;

	@Mock
	private  Trainee2TrainerRepository trainee2trainerRepoMock;

	@InjectMocks
	private TraineeServiceImpl traineeService;
	
	@InjectMocks
	private TrainerServiceImpl trainerService;

	@InjectMocks
	private TraineesController traineesController;

	@Autowired
	private TraineeRepository traineeRepo;
	
	@Autowired
	private TrainerRepository trainerRepo;
	
	@Mock
	private TrainerRepository trainerRepoMock;

	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;
	
	@Value("${amazon.sqs.endpoint}")
	private String sqsEndpoint;

	
	@Mock
	private TraineeRepository traineeRepoMock;

	@Autowired
	private UserRepository userRepository;
	
	@Mock
	private TrainerRepository trainerRepository;

	@Autowired
	private TokenRepository tokenRepository;

	
    @Mock
	private  Trainee2TrainerRepository trainee2trainerRepo;

	@Mock
	private AuthenticationManager authenticationManager;
	
    private ObjectMapper objectMapper=new ObjectMapper().registerModule(new JavaTimeModule());
	private TraineeDTO traineeDTO;
	private List<TraineeTrainerDTO> traineeTrainers;
	private Trainee trainee;
	private Trainer trainer;
	private TrainingType trainingType;
	Trainee2Trainer trainee2Trainer;
	private User user;

	
	@BeforeEach
	void setUp() throws Exception {
		mockMvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		traineeTrainers=new ArrayList<>();
		trainingType=new TrainingType(1L, TrainingTypeEnum.stretching);
		trainingTypeRepository.save(trainingType);
		traineeDTO=new TraineeDTO("Pedro", "Arango", LocalDate.parse("2000-02-12"), "Bello, Antioquia", true, traineeTrainers);
		user=new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINEE);
		trainee=new Trainee(1L,user,LocalDate.parse("2000-02-12"),"Bello, Antioquia");
		trainer=new Trainer(1L, user, trainingType);
		trainee2Trainer=new Trainee2Trainer(1L, trainee, trainer);
		traineeRepo.save(trainee);
		userRepository.save(user);
		trainee2trainerRepo.save(trainee2Trainer);
		trainerRepo.save(trainer);
		
		  // Mock user details
		 UserDetails user = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINEE);
		 Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		 SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@AfterEach
	void tearDown() {
		tokenRepository.deleteAll();
		trainingRepository.deleteAll();
		trainee2trainerRepo.deleteAll();
		trainerRepo.deleteAll();
		trainingTypeRepository.deleteAll();

		traineeRepo.deleteAll();
		userRepository.deleteAll();
	}
	
	@Test
	void testGetTraineeByUsername_withPermision() throws Exception {
		
	    when(trainee2trainerRepo.findByTraineeID(trainee.getId())).thenReturn(Collections.emptyList());
	    MvcResult result = mockMvc.perform(get(BASE_URL + "/{username}", trainee.getUser().getUsername())).andReturn();
	    assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}
	
	@Test
	void testGetTraineeByUsername_withNoPermission() throws Exception {
		UserDetails user = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	    MvcResult result = mockMvc.perform(get(BASE_URL + "/{username}", "someUsername")).andReturn();
	    assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
	}

	@Test
	void testUpdateTraineeProfile_withPermission() throws JsonProcessingException, Exception {
	    when(traineeRepoMock.findByUsername(anyString())).thenReturn(Optional.of(trainee));
	    TraineeUpdateDTO updateDTO = new TraineeUpdateDTO("NewFirstName", "NewLastName", LocalDate.of(1990, 1, 1), "New Address", true);
	    MvcResult result =mockMvc.perform(put(BASE_URL + "/{username}", trainee.getUser().getUsername())
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateDTO)))
	    		.andReturn();
	    assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
	}
	
	@Test
	void testUpdateTraineeProfile_withNoPermission() throws JsonProcessingException, Exception {
	    TraineeUpdateDTO updateDTO = new TraineeUpdateDTO("NewFirstName", "NewLastName", LocalDate.of(1990, 1, 1), "Barbosa", true);
		UserDetails user = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	    MvcResult result =mockMvc.perform(put(BASE_URL + "/{username}", trainee.getUser().getUsername())
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateDTO)))
	    		.andReturn();
	    assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
	}

	@Test
	void testRegisterTrainee_withPermision() throws JsonProcessingException, Exception {
	    TraineeCreateDTO traineeCreateDTO = new TraineeCreateDTO("Pedro","Arango",LocalDate.of(1990, 1, 1), "Barbosa");
	    MvcResult result = mockMvc.perform(post(BASE_URL + "/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(traineeCreateDTO)))
	            .andReturn();
	    assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
	}	

	@Test
	void testDeleteTrarinee_withPermission() throws Exception {
	    MvcResult result = mockMvc.perform(delete(BASE_URL + "/{username}", "Pedro.Arango"))
	            .andReturn();
	    assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());}
	
	@Test
	void testDeleteTrarinee_withNoPermission() throws Exception {
		UserDetails user = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	    MvcResult result = mockMvc.perform(delete(BASE_URL + "/{username}", "Pedro.Arango"))
	            .andReturn();
	    assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());}


	@Test
	void testUpdateTraineTrainers_withPermision() throws JsonProcessingException, Exception {
		List<String> trainerUsernames=new ArrayList<>();
		trainerUsernames.add(trainer.getUser().getUsername());
	    TraineeUpdateTrainersDTO updateTrainersDTO = new TraineeUpdateTrainersDTO(trainerUsernames);
	    MvcResult result = mockMvc.perform(put(BASE_URL + "/{username}/trainers", "Pedro.Arango")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateTrainersDTO)))
	            .andReturn();
	    assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());}

	@Test
	void testUpdateTraineTrainers_withNoPermision() throws JsonProcessingException, Exception {
		List<String> trainerUsernames=new ArrayList<>();
		trainerUsernames.add(trainer.getUser().getUsername());
	    TraineeUpdateTrainersDTO updateTrainersDTO = new TraineeUpdateTrainersDTO(trainerUsernames);
		UserDetails user = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	    MvcResult result = mockMvc.perform(put(BASE_URL + "/{username}/trainers", "Pedro.Arango")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateTrainersDTO)))
	            .andReturn();
	    assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());}

	@Test
	void testGetTraineeTrainingList_withPermision() throws Exception {
	    LocalDate periodFrom = LocalDate.parse("2023-01-01");
	    LocalDate periodTo = LocalDate.parse("2023-12-31");
	    String trainerName = "Jose.Arango";
	    String traineeName = "Pedro.Arango";
	    String specialization = "stretching";
	    
	    TrainingsFilterDTO trainingsFilterDTO = new TrainingsFilterDTO(periodFrom, periodTo, trainerName, traineeName, specialization);

	    MvcResult result = mockMvc.perform(get(BASE_URL + "/{username}/trainings", "Pedro.Arango")
	            .param("trainerName", trainingsFilterDTO.trainerName())
	            .param("traineeName", trainingsFilterDTO.traineeName())
	            .param("specialization", trainingsFilterDTO.specialization()))
	            .andReturn();
	    assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());}
	
	@Test
	void testGetTraineeTrainingList_withNoPermision() throws Exception {
	    LocalDate periodFrom = LocalDate.parse("2023-01-01");
	    LocalDate periodTo = LocalDate.parse("2023-12-31");
	    String trainerName = "Jose.Arango";
	    String traineeName = "Pedro.Arango";
	    String specialization = "stretching";
	    
	    TrainingsFilterDTO trainingsFilterDTO = new TrainingsFilterDTO(periodFrom, periodTo, trainerName, traineeName, specialization);
		
	    UserDetails user = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

	    MvcResult result = mockMvc.perform(get(BASE_URL + "/{username}/trainings", "Pedro.Arango")
	            .param("trainerName", trainingsFilterDTO.trainerName())
	            .param("traineeName", trainingsFilterDTO.traineeName())
	            .param("specialization", trainingsFilterDTO.specialization()))
	            .andReturn();
	    assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());}
	

	@Test
	void testAddTraineeTrainings_withPermision() throws JsonProcessingException, Exception {
		
		User user1 = new User(2L, "Jose", "Arango", "Jose.Arango", "giqXPD728", true, Role.TRAINER);
		Trainer trainer1 = new Trainer(3L, user1, trainingType);
		trainerRepo.save(trainer1);
		userRepository.save(user1);
		trainingTypeRepository.save(trainingType);
		
	    TrainingDTO trainingDTO = new TrainingDTO();
	    trainingDTO.setTraineeUsername("Pedro.Arango");
	    trainingDTO.setTrainerUsername("Jose.Arango");
	    trainingDTO.setSpecialization("stretching");
	    trainingDTO.setTrainingName("stretching");
	    trainingDTO.setTrainingDate(LocalDate.now().plusDays(1));
	    trainingDTO.setTrainingDuration(1.5f);
	    
	    MvcResult result = mockMvc.perform(post(BASE_URL + "/trainings")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(trainingDTO)))
	            .andReturn();
	    assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}
	
	@Test
	void testAddTraineeTrainings_withNoPermision() throws JsonProcessingException, Exception {
		User user1 = new User(2L, "Jose", "Arango", "Jose.Arango", "giqXPD728", true, Role.TRAINER);
		Trainer trainer1 = new Trainer(3L, user1, trainingType);
		trainerRepo.save(trainer1);
		userRepository.save(user1);
		trainingTypeRepository.save(trainingType);
	    TrainingDTO trainingDTO = new TrainingDTO();
	    trainingDTO.setTraineeUsername("Pedro.Arango");
	    trainingDTO.setTrainerUsername("Jose.Arango");
	    trainingDTO.setSpecialization("stretching");
	    trainingDTO.setTrainingName("stretching");
	    trainingDTO.setTrainingDate(LocalDate.now().plusDays(1));
	    trainingDTO.setTrainingDuration(1.5f);
	    
	    UserDetails user = new User(1L, "Pedro", "Arango", "Pedro.Arango", "giqXPD78", true, Role.TRAINER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

	    MvcResult result = mockMvc.perform(post(BASE_URL + "/trainings")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(trainingDTO)))
	            .andReturn();
	    System.out.println("THE RESULT IS: ");
	    System.out.println(result.getResponse().getContentAsString());
	    assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
	}
//	@Test
//	void testSendToTrainerReport() throws JsonProcessingException {
//
//	    TrainingDTO trainingDTO = new TrainingDTO("Pedro.Arango", "Pedro.Arango",
//	            "stretching", "stretching", LocalDate.now().plusDays(1), 1.5f);
//
//	    TrainerDTO trainerDTO = new TrainerDTO("Pedro.Arango", "Arango", TrainingTypeEnum.stretching, true, Collections.emptyList());
//	    when(trainerRepoMock.findTrainerByUsername("Pedro.Arango")).thenReturn(Optional.of(trainer));
//		List<Trainee2Trainer> trainerList=new ArrayList<>(Arrays.asList(trainee2Trainer));
//		when(trainee2trainerRepoMock.findByTrainerID(1L)).thenReturn(trainerList);
//
//	    when(trainerService.getTrainerProfile("Pedro.Arango")).thenReturn(trainerDTO);
//
//	    // Perform the test
//	    traineesController.sendToTrainerReport(trainingDTO);
//	    
//	    // Verify that the queueMessagingTemplate.send method is called with the correct payload
//	    ArgumentCaptor<Message<String>> payloadCaptor = ArgumentCaptor.forClass(Message.class);
//	    verify(queueMessagingTemplate).send(sqsEndpoint, payloadCaptor.capture());
//
//	    // Extract the payload from the captor and convert it back to TrainingRequest for assertions
//	    Message<String> message = payloadCaptor.getValue();
//	    String payload = message.getPayload();
//	    TrainingRequest trainingRequest = objectMapper.readValue(payload, TrainingRequest.class);
//
//
//	    // Add assertions to verify that the TrainingRequest has the expected values
//	    assertEquals(trainingDTO.getTrainerUsername(), trainingRequest.getTrainerUsername());
//	    assertEquals(trainerDTO.getFirstName(), trainingRequest.getTrainerFirstName());
//	    assertEquals(trainerDTO.getLastName(), trainingRequest.getTrainerLastName());
//	    assertEquals(trainerDTO.getIsActive(), trainingRequest.getIsActive());
//	    // Add other assertions based on your TrainingRequest structure
//	}

}
