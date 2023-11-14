package com.epam.gym.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.Trainee;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee,Long>{
		
	@Query("select t from Trainee t where t.user.id =:userId")
	Optional<Trainee> findByUserID(@Param("userId") Long userId);

	@Query("select t from Trainee t where t.user.username =:username")
	Optional<Trainee> findByUsername(@Param("username") String username);

}
