package ca.uvic.lscholte;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ca.uvic.lscholte.commands.*;
import ca.uvic.lscholte.listeners.*;
import ca.uvic.lscholte.utilities.FileUtilities;

public final class AdminAid extends JavaPlugin {
	
	public static Plugin onTime;
	public static AdminAid plugin;
	
	public static Map<String, String> lastSender;
	public static List<String> staffChat;
	public static List<String> frozenPlayers;
	
	/* TODO: try using final?
	/* TODO: fix any other possible inefficiencies */
			
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
}
