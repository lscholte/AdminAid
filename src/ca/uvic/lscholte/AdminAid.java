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

import ca.uvic.lscholte.commands.AdminaidCommand;
import ca.uvic.lscholte.commands.BanCommand;
import ca.uvic.lscholte.commands.ChatspyCommand;
import ca.uvic.lscholte.commands.InfoCommand;
import ca.uvic.lscholte.commands.KickCommand;
import ca.uvic.lscholte.commands.MailCommand;
import ca.uvic.lscholte.commands.MsgCommand;
import ca.uvic.lscholte.commands.MuteCommand;
import ca.uvic.lscholte.commands.NoteCommand;
import ca.uvic.lscholte.commands.PlayerinfoCommand;
import ca.uvic.lscholte.commands.ReplyCommand;
import ca.uvic.lscholte.commands.RulesCommand;
import ca.uvic.lscholte.commands.StaffchatCommand;
import ca.uvic.lscholte.commands.TeleportCommand;
import ca.uvic.lscholte.commands.TempbanCommand;
import ca.uvic.lscholte.commands.TempmuteCommand;
import ca.uvic.lscholte.commands.UnbanCommand;
import ca.uvic.lscholte.commands.UnmuteCommand;
import ca.uvic.lscholte.commands.WarnCommand;
import ca.uvic.lscholte.listeners.ChatListener;
import ca.uvic.lscholte.listeners.CommandListener;
import ca.uvic.lscholte.listeners.PlayerListener;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.UUIDFetcher;

public final class AdminAid extends JavaPlugin {
	
	private static Plugin onTime;
	private static AdminAid plugin;
	
	public static Map<String, String> lastSender;
	public static List<String> staffChat;
	public static List<String> frozenPlayers;
	
	//TODO: IMPORTANT!!! Convert to UUID system
			
	@Override
	public void onEnable() {
		
		onTime = Bukkit.getPluginManager().getPlugin("OnTime");
		plugin = this;
		
		lastSender = new HashMap<String, String>();
		staffChat = new ArrayList<String>();
		frozenPlayers = new ArrayList<String>();
				
		saveDefaultConfig();
				
		final Updater updater = new Updater(this);
		updater.updateConfig();
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			public void run() {
				updater.performVersionCheck();
			}
		});
		
		
		File userDataDir = new File(getDataFolder() + "/userdata/");
		FileUtilities.createNewDir(userDataDir);
		
//		for(String filename : userDataDir.list()) {
//			System.out.println(filename);
//			System.out.println(FileUtilities.removeFileExtension(filename));
//		}
		
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			public void run() {
				try {
					convertFilesToUUID();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
							
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
		plugin = null;
	}
	
	private void convertFilesToUUID() throws Exception {
		
		File dir = new File(this.getDataFolder() + "/userdata/");
		File[] children = dir.listFiles(); //does return null if not a directory, but can probably ignore this
		List<String> names = new ArrayList<String>();
		
		//TODO:
		//if(someConfig.get(someBoolean) == false) return; 
		
		int i = 1;
		List<File> toConvert = new ArrayList<File>();
		for(File f : children) {
			String n = FileUtilities.removeFileExtension(f.getName());
			if(!isUUID(n)) {
				names.add(n);
				toConvert.add(f);
				System.out.println(i);
				++i;
			}
		}
		if(names.isEmpty()) return;		
		
		Map<String, UUID> map = new UUIDFetcher(names).call(); //probably do this (or even whole method) as async task
		//System.out.println(map.size());
		
		//int i = 0;
		for(File f1 : toConvert) {
			String name = FileUtilities.removeFileExtension(f1.getName());
			UUID uuid = map.get(name);
			File f2 = new File(this.getDataFolder() + "/userdata/" + uuid + ".yml");
			//if(f2.exists()) {
				//System.out.println(name + ": " + uuid);
				//++i;
			//}
			f1.renameTo(f2);
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(f2);
			userFile.set("Name", name);
			FileUtilities.saveYamlFile(userFile, f2);
		}
		//System.out.println(i);
		System.out.println("Done");
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
	
	public static AdminAid getPlugin() {
		return plugin;
	}
	
	public static Plugin getOnTime() {
		return onTime;
	}
}
