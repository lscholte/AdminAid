package ca.uvic.lscholte.utilities;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public final class FileUtilities {
	
	private FileUtilities() { }
	
	public static void createNewFile(File file) {
		if(!file.exists()) {
			try {
				file.createNewFile();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createNewDir(File dir) {
		if(!dir.exists()) {
			try {
				dir.mkdir();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void saveYamlFile(YamlConfiguration yamlConfig, File file) {
		try {
			yamlConfig.save(file);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
