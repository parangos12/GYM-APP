package com.epam.gym.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessDeniedException extends RuntimeException{
	
    private String user;

    private String action;

    private String externalUser;
	
	public AccessDeniedException(String user, String action, String externalUser) {
		super(String.format("%s Unauthorized access %s : %s",user,action,externalUser));
		this.user = user;
		this.action = action;
		this.externalUser = externalUser;
	}
	
    public AccessDeniedException() {
        super();
    }
}
