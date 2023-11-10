package com.epam.gym.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.User;

@Repository
public interface Trainee2TrainerRepository  extends JpaRepository<Trainee2Trainer, Long>{
	
	List<Trainee2Trainer> findAllByTrainee(Trainee trainee);

}
