package com.epam.gym.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.User;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee,Long>{
	
	Trainee findByUser(User user);
}
