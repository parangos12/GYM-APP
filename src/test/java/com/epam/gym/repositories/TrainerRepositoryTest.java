package com.epam.gym.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.entities.User;

@SpringBootTest
@WebAppConfiguration
class TrainerRepositoryTest {


    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private Trainer trainer;
    private User user;
    private TrainingType trainingType;

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        user = new User(1000L, "Sanchez", "Arangos", "Sanchez.Arango", "giqXPD782", true, Role.TRAINER);
        userRepository.save(user);

        trainingType = new TrainingType(1L, TrainingTypeEnum.stretching);
        trainingTypeRepository.save(trainingType);

        trainer = new Trainer(1L, user, trainingType);
        trainerRepository.save(trainer);
    }

    @Test
    void testFindTrainerByUsername() {
        Optional<Trainer> trainerFound = trainerRepository.findTrainerByUsername(user.getUsername());
        assertThat(trainerFound).isNotNull();
        assertThat(trainerFound).isPresent();
        assertThat(trainerFound.get().getUser().getUsername()).isEqualTo(user.getUsername());
    }
}
