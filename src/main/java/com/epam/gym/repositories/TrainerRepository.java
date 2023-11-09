package com.epam.gym.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long>{

}
