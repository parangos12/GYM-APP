package com.epam.gym.util;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.System.Diskspace;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class MemoryHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		long freeSpace = getFreeDiskPace();
		if (freeSpace < 1_000_000_000L) {
			return Health.down().withDetail("error", "Disk space low").withDetail("free space disk", freeSpace).build();
		}
		return Health.up().withDetail("free space disk", freeSpace).build();
	}

	private long getFreeDiskPace() {
		try { 
			File file = new File("/");
			DiskSpace space = new DiskSpace(file);
			return space.getFreeSpace();

		} catch (Exception e) {
			return 0L;
		}
	}

}
