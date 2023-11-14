package com.epam.gym.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException{

	String resourceName;
	String fieldName;
	long fieldValue;
	String fieldValueStr;
	
	public ResourceNotFoundException(String resourceName, String fieldName, long fieldValue) {
		super(String.format("%s not found with %s : %s",resourceName,fieldName,fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
	
	public ResourceNotFoundException(String resourceName, String fieldName, String fieldValueStr) {
		super(String.format("%s not found with %s : %s",resourceName,fieldName,fieldValueStr));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValueStr = fieldValueStr;
	}

}
