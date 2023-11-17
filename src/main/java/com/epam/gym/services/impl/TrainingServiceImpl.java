package com.epam.gym.services.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.gym.dto.TrainingDTO;
import com.epam.gym.entities.Trainee;
import com.epam.gym.entities.Trainer;
import com.epam.gym.entities.Training;
import com.epam.gym.entities.TrainingType;
import com.epam.gym.entities.TrainingTypeEnum;
import com.epam.gym.exceptions.ResourceNotFoundException;
import com.epam.gym.repositories.TraineeRepository;
import com.epam.gym.repositories.TrainerRepository;
import com.epam.gym.repositories.TrainingRepository;
import com.epam.gym.repositories.TrainingTypeRepository;
import com.epam.gym.services.TrainingService;

@Service
public class TrainingServiceImpl implements TrainingService{
	
	
	private final TrainingRepository trainingRepo;
	private final TraineeRepository traineeRepo;
	private final TrainerRepository trainerRepo;
	private final TrainingTypeRepository trainingTypeRepo;

	@Autowired
	public TrainingServiceImpl(TrainingRepository trainingRepo,TraineeRepository traineeRepo,TrainerRepository trainerRepo,TrainingTypeRepository trainingTypeRepo) {
		this.trainingRepo = trainingRepo;
		this.traineeRepo=traineeRepo;
		this.trainerRepo=trainerRepo;
		this.trainingTypeRepo=trainingTypeRepo;
	}

	@Override
	public void saveTraining(TrainingDTO trainingDTO) {
		Trainee trainee=this.traineeRepo.findByUsername(trainingDTO.getTraineeUsername()).orElseThrow(()->new ResourceNotFoundException("User","username" , trainingDTO.getTraineeUsername())); 
		Trainer trainer=this.trainerRepo.findTrainerByUsername(trainingDTO.getTrainerUsername()).orElseThrow(()->new ResourceNotFoundException("User","username" , trainingDTO.getTrainerUsername())); 
		
	    TrainingTypeEnum trainingTypeEnum = TrainingTypeEnum.valueOf(trainingDTO.getSpecialization().toLowerCase());
        TrainingType trainingType = this.trainingTypeRepo.findTrainingTypeBy(trainingTypeEnum);
        trainer.setSpecialization(trainingType);

		
		String trainingName=trainingDTO.getTrainingName();
		LocalDate trainingDate=trainingDTO.getTrainingDate();
		Float trainingDuration=trainingDTO.getTrainingDuration();
		Training trainingToSave=new Training(null, trainee, trainer, trainingType, trainingName, trainingDate, trainingDuration);
		this.trainingRepo.save(trainingToSave);
	}

}
