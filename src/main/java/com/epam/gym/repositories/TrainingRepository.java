package com.epam.gym.repositories;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;

@Repository
public interface TrainingRepository extends JpaRepository<Training,Long>{
	
	@Query("SELECT t.trainingName, t.trainingDate, t.trainingType, t.trainingDuration, "
	        + "t.trainer.user.username FROM Training t WHERE t.trainee.user.username=:username "
	        + "AND (:periodFrom IS NULL OR t.trainingDate>=:periodFrom) "
	        + "AND (:periodTo IS NULL OR t.trainingDate<=:periodTo) "
	        + "AND (:trainerName IS NULL OR t.trainer.user.username=:trainerName) "
	        + "AND (:trainingType IS NULL OR t.trainingType.trainingTypeEnum=:trainingType)")
	List<Object[]> findTraineeTrainingList(@Param("username")String username,
	        @Param("periodFrom") LocalDate periodFrom, @Param("periodTo") LocalDate periodTo,
	        @Param("trainerName") String trainerName, @Param("trainingType") TrainingTypeEnum  trainingType);
	
	@Query("SELECT t.trainingName, t.trainingDate, t.trainingType, t.trainingDuration, "
	        + "t.trainee.user.username FROM Training t "
	        + "WHERE t.trainer.user.username=:username "
	        + "AND (:periodFrom IS NULL OR t.trainingDate>=:periodFrom) "
	        + "AND (:periodTo IS NULL OR t.trainingDate<=:periodTo) "
	        + "AND (:traineeName IS NULL OR t.trainee.user.username=:traineeName) ")
	List<Object[]> findTrainerTrainingList(@Param("username")String username,
	        @Param("periodFrom") LocalDate periodFrom, @Param("periodTo") LocalDate periodTo,
	        @Param("traineeName") String traineeName);
	
	TrainingType findByTrainingType_TrainingTypeEnum(TrainingTypeEnum trainingTypeEnum);

}
