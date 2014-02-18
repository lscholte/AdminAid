package ca.uvic.lscholte;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.PluginDescriptionFile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Updater {
	
	private AdminAid plugin;
	
    private final String API_HOST = "https://api.curseforge.com";
    private final String API_QUERY = "/servermods/files?projectIds=";
	private final int PROJECT_ID = 60834;
	
	private static final String API_NAME_VALUE = "name";
	
	private String versionName = "";
	
	public Updater(AdminAid instance) {
		plugin = instance;
	}
	
	@SuppressWarnings("serial")
	public class VersionCheckException extends Exception {}
	
	public void updateConfig() {
		
		String currentVersion = plugin.getDescription().getVersion();
		String configVersion = plugin.getConfig().getString("Version");
		if(!currentVersion.equalsIgnoreCase(configVersion)) {
			if(plugin.getConfig().getBoolean("AutoUpdateConfig") == true) {
				
				Map<String, Object> keyValuePairs = new HashMap<String, Object>();
				
				for(String key : plugin.getConfig().getKeys(true)) {
					keyValuePairs.put(key, plugin.getConfig().get(key));
				}
						
				File configFile = new File(plugin.getDataFolder() + "/config.yml");
				configFile.delete();
				
				plugin.getConfig().options().copyDefaults(true);
	
				for(String key : keyValuePairs.keySet()) {
					if(plugin.getConfig().get(key) != null) {
						plugin.getConfig().set(key, keyValuePairs.get(key));
					}
				}
				plugin.getConfig().set("Version", currentVersion);
				plugin.saveConfig();
				
				plugin.getLogger().info("Configuration file was outdated");
				plugin.getLogger().info("Missing configuration keys have now been added!");
			}
			else {
				plugin.getLogger().warning("The configuration file is not up to date!");
			}
		}
	}
	
	public boolean isLatest() throws VersionCheckException {
		URL url = null;
		try {
			url = new URL(API_HOST + API_QUERY + PROJECT_ID);
		}
		catch(MalformedURLException e) {
			throw new VersionCheckException();
		}
		try {
			URLConnection con = url.openConnection();
			
			con.addRequestProperty("User-Agent", "AdminAid (by SnipsRevival)");
			
			final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String response = reader.readLine();
			
			JSONArray array = (JSONArray) JSONValue.parse(response);
			
			if(array.size() <= 0) return false;
			JSONObject latest = (JSONObject) array.get(array.size() - 1);
			versionName = (String) latest.get(API_NAME_VALUE);
			
			String[] updateVersion = versionName.replaceAll("[a-zA-Z ]", "").split("\\.");
			int updateMajorRelease = Integer.parseInt(updateVersion[0]);
			int updateMinorRelease = Integer.parseInt(updateVersion[1]);
			int updateBuild = Integer.parseInt(updateVersion[2]);
	
			PluginDescriptionFile pdf = plugin.getDescription();
			String[] currentVersion = pdf.getVersion().split("\\.");
			int currentMajorRelease = Integer.parseInt(currentVersion[0]);
			int currentMinorRelease = Integer.parseInt(currentVersion[1]);
			int currentBuild = Integer.parseInt(currentVersion[2]);
			
			if(updateMajorRelease > currentMajorRelease) return false;
			if((updateMinorRelease > currentMinorRelease) && updateMajorRelease == currentMajorRelease) return false;
			if((updateBuild > currentBuild) && updateMinorRelease == currentMinorRelease) return false;
			return true;
		}
		catch(IOException e) {
			throw new VersionCheckException();
		}
	}
	
	public void performVersionCheck() {
		if(plugin.getConfig().getBoolean("EnableVersionChecker") == true) {
			plugin.getLogger().info("Checking for newer versions...");
			try {
				if(!isLatest()) {
					plugin.getLogger().warning(getVersionName() + " is available for download!");
				}
				else {
					plugin.getLogger().info("You have the latest version of AdminAid!");
				}
			}
			catch(VersionCheckException e) {
				plugin.getLogger().warning("Something is wrong with the version checker. This can probably be ignored");
			}
		}
	}
	
	public String getVersionName() {
		return versionName;
	}
}
