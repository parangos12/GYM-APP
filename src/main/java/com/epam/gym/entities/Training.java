package com.epam.gym.entities;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="trainings")
public class Training {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="trainee_id",nullable=false)
	private Trainee trainee;
	
	@ManyToOne
	@JoinColumn(name = "trainer_id",nullable=false)
	private Trainer trainer;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="training_type_id")
	private TrainingType trainingType;

	@Column(name="training_name",nullable=false,length=255)
	private String trainingName;
	
	
	@Column(name="training_date",nullable=false,columnDefinition = "DATE")
	private LocalDate trainingDate;
	
	@Column(name="training_duration",nullable=false,length = 10,precision=2)
	private Float trainingDuration;
	
}



















