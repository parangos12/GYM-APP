package com.epam.gym.config;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginAttemptCacheEntry {
	
    private int loginAttempts;
    private long lastLoginAttemptTimestamp;

    public LoginAttemptCacheEntry() {
        this.loginAttempts = 0;
        this.lastLoginAttemptTimestamp = 0L;
    }

}
