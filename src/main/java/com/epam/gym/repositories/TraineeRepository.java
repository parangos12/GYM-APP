package com.epam.gym.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainee;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee,Long>{
	
	Trainee findByUsername(String username);
}
