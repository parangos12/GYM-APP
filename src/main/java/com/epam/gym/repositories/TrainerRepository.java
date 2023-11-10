package com.epam.gym.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.User;
import com.epam.gym.payloads.TrainerDTO;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long>{

	
}
