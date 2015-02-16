package edu.cuny.qc.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class BackupSource {
	public static String[] BACKUP_LOCATIONS = new String[] {
			"src/main/java/ac",
			"src/main/java/eu",
			"src/main/java/edu",
			"src/main/resources",
	};
	public static String FOLDER_NAME = "BackupSource";

	public static void backup(File outputFolder) throws IOException {
		File dest = new File(outputFolder, FOLDER_NAME);
		dest.mkdir();
		for (String loc : BACKUP_LOCATIONS) {
			File locDest = new File(dest, loc);
			File src = new File(loc);
			System.out.printf("%s Backing up %s to %s...\n", Utils.detailedLog(), src, locDest);
			if (!src.exists()) {
				throw new IOException("Source folder for backing up doesn't exist: " + src.getAbsolutePath());
			}
			locDest.mkdirs();
			
			FileUtils.copyDirectory(src, locDest);
		}
		
		System.out.printf("%s Done backing up all %s locations.\n", Utils.detailedLog(), BACKUP_LOCATIONS.length);
	}

}
