package com.epam.gym.util;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = {TrainerUsernamesValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTrainerUsernames {
	
	  String message() default "Invalid trainer usernames";
	  Class<?>[] groups() default {}; 
	  Class<? extends Payload>[] payload() default {};

}
