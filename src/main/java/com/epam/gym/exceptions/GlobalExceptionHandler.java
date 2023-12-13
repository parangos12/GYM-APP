package com.epam.gym.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<APIResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
		String message=ex.getMessage();
		APIResponse apiResponse=new APIResponse(message, false);
		return new ResponseEntity<APIResponse>(apiResponse,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(LogginDeniedException.class)
	public ResponseEntity<APIResponse> logginDeniedException(LogginDeniedException ex){
		String message=ex.getMessage();
		
		APIResponse apiResponse=new APIResponse(message, false);
		return new ResponseEntity<APIResponse>(apiResponse,HttpStatus.FORBIDDEN);
	}

	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<APIResponse> accessDeniedException(AccessDeniedException ex){
		String message=ex.getMessage();
		APIResponse apiResponse=new APIResponse(message, false);
		return new ResponseEntity<APIResponse>(apiResponse,HttpStatus.FORBIDDEN);
	}

	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> handleMethodArgsNotValidException(MethodArgumentNotValidException ex){
		Map<String,String> resp=new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error)->{
			String fieldName=((FieldError) error).getField();
			String message=error.getDefaultMessage();
			resp.put(fieldName, message);});
		return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<APIResponse> handleApiException(ApiException ex){
		String message=ex.getMessage();
		APIResponse apiResponse=new APIResponse(message, true);
		return new ResponseEntity<APIResponse>(apiResponse, HttpStatus.BAD_REQUEST);
	}
		
}
