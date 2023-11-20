package com.epam.gym.health.indicators;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class DiskSpace {

	private final File path;
	
	public DiskSpace(File path) {
		this.path=path;
	}
	
	public long getFreeSpace() {
	    try {
	        return Files.getFileStore(FileSystems.getDefault().getPath(path.getAbsolutePath()))
	                    .getUsableSpace();
	      } catch (Exception e) {
	        return 0L;
	      }

	}
	
}
