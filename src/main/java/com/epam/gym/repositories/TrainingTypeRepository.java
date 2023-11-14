package com.epam.gym.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType,Long>{

	@Query("select t from TrainingType t where t.trainingTypeEnum=:trainingType")
	Optional<TrainingType> findTrainingTypeBy(@Param("trainingType") TrainingTypeEnum  trainingType);

	
}
