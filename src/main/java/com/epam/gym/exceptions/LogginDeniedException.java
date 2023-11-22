package com.epam.gym.exceptions;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogginDeniedException extends RuntimeException{
	
	String user;
	int attempts;
	String msg;
	
	public LogginDeniedException(String user, int attempts) {
		super(String.format("%s has %s : attempts",user,attempts));
		this.user = user;
		this.attempts = attempts;
	}
	
	public LogginDeniedException(String user,String msg) {
		super(msg+" "+user);
		this.user = user;
		this.msg=msg;
	}
}
