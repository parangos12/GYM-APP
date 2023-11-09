package com.epam.gym.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.TrainingType;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType,Long>{

}
