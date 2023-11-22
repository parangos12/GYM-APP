package com.epam.gym.config;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 3;
    private final long LOCK_TIME_DURATION = 20 * 1000; // 1 min
    public  ConcurrentHashMap<String, LoginAttemptCacheEntry> loginAttemptCache;

    public LoginAttemptService() {
        this.loginAttemptCache = new ConcurrentHashMap<>();
    }
    
    public void loginSucceeded(String username) {
        if (loginAttemptCache == null) {
            loginAttemptCache = new ConcurrentHashMap<>();
        }

        LoginAttemptCacheEntry entry = loginAttemptCache.get(username);
        if (entry == null) {
            entry = new LoginAttemptCacheEntry();
            loginAttemptCache.put(username, entry);
        }

        entry.setLoginAttempts(0);
    }

    public void loginFailed(String username) {
        int attempts = 0;
        long currentTime = System.currentTimeMillis();
        LoginAttemptCacheEntry entry = loginAttemptCache.get(username);

        if (entry == null) {
            entry = new LoginAttemptCacheEntry();
            loginAttemptCache.put(username, entry);
        }

        attempts = entry.getLoginAttempts() + 1;
        entry.setLoginAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            entry.setLastLoginAttemptTimestamp(currentTime);
        }
    }
    
    public boolean isBlocked(String username) {
        LoginAttemptCacheEntry entry = loginAttemptCache.get(username);
        
        if(entry == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis(); 
        long lastAttemptTime = entry.getLastLoginAttemptTimestamp();   
        
        if(lastAttemptTime > 0 && currentTime < lastAttemptTime + LOCK_TIME_DURATION) {
            return true;
        }else {
            return false;
        }
    }
    
}
