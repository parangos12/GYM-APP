package com.epam.gym.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long>{
	
	@Query("select t from Trainer t where t.user.username =:username")
	Optional<Trainer> findTrainerByUsername(@Param("username") String username);

}
