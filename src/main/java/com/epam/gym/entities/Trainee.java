package com.epam.gym.entities;

import java.time.LocalDate;

import org.springframework.stereotype.Indexed;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="trainees", indexes= {
		@Index(name="idx_trainee_userid",columnList="user_id")})
public class Trainee {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
	private Long id;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="user_id",nullable=false)
	private User user;
	
	@Temporal(TemporalType.DATE)
	@Column(name="date_of_birth",nullable=true,columnDefinition="DATE")
	private LocalDate dateOfBirth;
	
	@Column(name="address",nullable=true,length=255)
	private String address;
	
}






