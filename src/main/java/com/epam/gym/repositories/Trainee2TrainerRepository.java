package com.epam.gym.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainee2Trainer;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.User;

@Repository
public interface Trainee2TrainerRepository  extends JpaRepository<Trainee2Trainer, Long>{
		
	@Query("select t2t from Trainee2Trainer t2t  where t2t.trainee.id =:traineeID")
	List<Trainee2Trainer> findByTraineeID(@Param("traineeID") Long traineeID);
	
	@Query("select t2t from Trainee2Trainer t2t  where t2t.trainer.id =:trainerID")
	List<Trainee2Trainer> findByTrainerID(@Param("trainerID") Long trainerID);

	
	@Query("select t2t from Trainee2Trainer t2t  where t2t.trainee.user.username =:username")
	Optional<Trainee2Trainer> findByUsername(@Param("username") String username);

    @Modifying
    @Query("delete from Trainee2Trainer t2t " +
            "where t2t.trainee.user.username = :traineeUsername " +
            "and t2t.trainer.user.username = :trainerUsername")
    void deleteByTraineeAndTrainerUsername(
            @Param("traineeUsername") String traineeUsername,
            @Param("trainerUsername") String trainerUsername);
    
    @Query(value = "SELECT t.user.username, t.user.firstName,t.user.lastName,t.specialization.trainingTypeEnum "
    		+ "FROM Trainer t WHERE t.user.isActive=true "
    		+ "AND t.id NOT IN (SELECT t2t.trainer.id FROM Trainee2Trainer t2t)")
    List<Object[]> findActiveTrainersWithNoTrainees();

}
