package ca.uvic.lscholte;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ca.uvic.lscholte.commands.*;
import ca.uvic.lscholte.listeners.*;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.UUIDFetcher;

public final class AdminAid extends JavaPlugin {
	
	private static Plugin onTime;
	
	public static Map<String, String> lastSender;
	public static List<String> staffChat;
	public static List<String> frozenPlayers;
	
	//TODO: IMPORTANT!!! Convert to UUID system
	//Concept:
	//	Do an initial conversion of all names to UUIDs (only do this once)
	//
	//	Whenever a command is issued, get the UUID last associated
	//	with the player name on the server. Note that this may be
	//	different than what would be returned by querying Mojang's servers.
	//	However, this should be fine because we shouldn't care what UUID is
	//	technically associated with a name; we only care about what UUID was
	//	last associated with the name on the server
	//
	//	I need to figure out a quick method for getting the last UUID associated
	//	with a name quickly. I don't want to have to search linearly through every
	// 	file linearly until I find the correct one.
	
	//TODO: Theoretically a better system for giving AdminAid
	//priority to commands:
	//    If command is already registered and command
	//    is not disabled in config, then unregister command
	//	  and then reregister so that AdminAid gets the command
	
	@Override
	public void onEnable() {
				
		onTime = Bukkit.getPluginManager().getPlugin("OnTime");
		
		lastSender = new HashMap<String, String>();
		staffChat = new ArrayList<String>();
		frozenPlayers = new ArrayList<String>();
				
		saveDefaultConfig();
		
		ConfigConstants.getInstance(this);
		
		final Updater updater = new Updater(this);
		updater.updateConfig();
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			public void run() {
				updater.performVersionCheck();
			}
		});
		
		
		File userDataDir = new File(getDataFolder() + "/userdata/");
		FileUtilities.createNewDir(userDataDir);
		
		if(getConfig().getBoolean("MigrateUserData") == true) {
			this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
				public void run() {
					try {
						convertFilesToUUID();
						getConfig().set("MigrateUserData", false);
						saveConfig();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
							
		new AdminaidCommand(this);
		new BanCommand(this);
		new ChatspyCommand(this);
		new InfoCommand(this);
		new KickCommand(this);
		new MailCommand(this);
		new MsgCommand(this);
		new MuteCommand(this);
		new NoteCommand(this);
		new PlayerinfoCommand(this);
		new ReplyCommand(this);
		new RulesCommand(this);
		new StaffchatCommand(this);
		new TeleportCommand(this);
		new TempbanCommand(this);
		new TempmuteCommand(this);
		new UnbanCommand(this);
		new UnmuteCommand(this);
		new WarnCommand(this);
		
		new CommandListener(this);
		new ChatListener(this);
		new PlayerListener(this);
	}
	
	@Override
	public void onDisable() {
		lastSender = null;
		staffChat = null;
		frozenPlayers = null;
		onTime = null;
	}
	
	private void convertFilesToUUID() throws Exception {
		getLogger().info("Migrating user data from player names to UUIDs");
		
		File dir = new File(this.getDataFolder() + "/userdata/");
		File[] children = dir.listFiles();
		List<String> names = new ArrayList<String>();
				
		List<File> toConvert = new ArrayList<File>();
		for(File f : children) {
			String n = FileUtilities.removeFileExtension(f.getName());
			if(!isUUID(n)) {
				names.add(n);
				toConvert.add(f);
			}
		}
		
		getLogger().info("There are " + toConvert.size() + " files to migrate");
		
		if(!names.isEmpty()) {
			Map<String, UUID> map = new UUIDFetcher(names).call();
			
			for(File f1 : toConvert) {
				String name = FileUtilities.removeFileExtension(f1.getName());
				UUID uuid = map.get(name);
				File f2 = new File(this.getDataFolder() + "/userdata/" + uuid + ".yml");
				f1.renameTo(f2);
				YamlConfiguration userFile = YamlConfiguration.loadConfiguration(f2);
				userFile.set("Name", name);
				FileUtilities.saveYamlFile(userFile, f2);
			}
		}
		getLogger().info("Migration of user data is now complete");
	}
	
	private static boolean isUUID(String str) {
		try {
			UUID.fromString(str);
		}
		catch(IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	
	public static Plugin getOnTime() {
		return onTime;
	}
}
