package com.epam.gym.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users", indexes= {
		@Index(name="idx_username",columnList="username",unique=true)
})
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",nullable=false,unique=true)
	private Long id;
	
	@Column(name="first_name",nullable=false,length=255)
	private String firstName;
	
	@Column(name="last_name",nullable=false,length = 255)
	private String lastName;
	
	@Column(name="username",nullable=false, unique=true,length=255)
	private String username;
	
	@Column(name="password",nullable=false,length=255)
	private String password;
	
	@Column(name="is_active",nullable=false,columnDefinition = "BOOLEAN DEFAULT true")
	private Boolean isActive;
	
}

