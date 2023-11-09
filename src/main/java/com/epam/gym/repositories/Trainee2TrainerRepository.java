package com.epam.gym.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainee2Trainer;

@Repository
public interface Trainee2TrainerRepository  extends JpaRepository<Trainee2Trainer, Long>{

}
