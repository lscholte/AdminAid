package ca.uvic.lscholte.utilities;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * A utility class containing commonly used
 * methods related to files
 */
public final class FileUtilities {
	
	/**
	 * Utility class cannot be instantiated
	 */
	private FileUtilities() { }
	
	/**
	 * Creates a new File if it does not already exist
	 * 
	 * @param file The File to create
	 */
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
	
	/**
	 * Creates a new directory if it does not already exist
	 * 
	 * @param dir The directory to create
	 */
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
	
	/**
	 * Saves a YamlConfiguration. Always call this method after making changes
	 * to a YamlConfiguration
	 * 
	 * @param yamlConfig The YamlConfiguration to save
	 * @param file The File to save the YamlConfiguration to
	 */
	public static void saveYamlFile(YamlConfiguration yamlConfig, File file) {
		try {
			yamlConfig.save(file);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String removeFileExtension(String filename) {
		int pos = filename.lastIndexOf(".");
		String name = pos != -1 ? filename.substring(0, pos) : filename;
		return name;
	}
}
