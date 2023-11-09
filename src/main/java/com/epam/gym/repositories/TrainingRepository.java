package com.epam.gym.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training,Long>{
	
}
