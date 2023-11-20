package com.epam.gym.health.indicators;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator{

	private final JdbcTemplate jdbcTemplate;
	
	public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Health health() {
		try {
			int result=jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			if(result==1) {
				return Health.up().withDetail("Database conection ON", result).build();
			}else {
                return Health.down().withDetail("error", "Database not reachable").build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("error", "Database not reachable").build();
        }}

}
