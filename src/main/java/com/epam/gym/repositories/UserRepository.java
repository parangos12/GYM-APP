package com.epam.gym.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

	User findByUsername(String username);
}
