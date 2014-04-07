package ca.uvic.lscholte.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.LoginRunnables;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;

public class PlayerListener implements Listener {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	
	public PlayerListener(AdminAid instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
			
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		misc = new MiscUtilities(plugin);

		Player player = event.getPlayer();
		//File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		//YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration config = FileUtilities.loadYamlConfiguration(plugin, player.getUniqueId());
		List<String> mailListNew = config.getStringList("NewMail");
		List<String> mailListRead = config.getStringList("ReadMail");
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		
		List<Runnable> runnables = new ArrayList<Runnable>();
		runnables.add(new LoginRunnables.LoginMessagesRunnable(player));
		runnables.add(new LoginRunnables.MailRunnable(plugin, player));
		
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new LoginRunnables.UpdaterRunnable(plugin, player));
		
		for(Runnable r : runnables) {
			plugin.getServer().getScheduler().runTask(plugin, r);
		}
		
		/* Sets various statuses for the player in their userdata file */
		config.set("Name", player.getName());
		config.set("BanExempt", player.hasPermission("adminaid.banexempt"));
		config.set("MuteExempt", player.hasPermission("adminaid.muteexempt"));
		config.set("KickExempt", player.hasPermission("adminaid.kickexempt"));
		config.set("StaffMember", player.hasPermission("adminaid.staffmember"));
		if(config.get("ChatSpy") == null) config.set("ChatSpy", false);
		config.set("IPAddress", ipAddress);
		config.set("NewMail", mailListNew);
		config.set("ReadMail", mailListRead);
		//FileUtilities.saveYamlFile(config, file);
		FileUtilities.saveYamlConfiguration(plugin, config, player.getUniqueId());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		misc = new MiscUtilities(plugin);
		
		Player player = event.getPlayer();
		//File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		//YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration config = FileUtilities.loadYamlConfiguration(plugin, player.getUniqueId());

		List<String> mailListNew = config.getStringList("NewMail");
		List<String> mailListRead = config.getStringList("ReadMail");
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		Location loc = player.getLocation();
		int xCoord = loc.getBlockX();
		int yCoord = loc.getBlockY();
		int zCoord = loc.getBlockZ();
		String world = loc.getWorld().getName();
	
		/* Sets various statuses for the player in their userdata file */
		config.set("BanExempt", player.hasPermission("adminaid.banexempt"));
		config.set("MuteExempt", player.hasPermission("adminaid.muteexempt"));
		config.set("KickExempt", player.hasPermission("adminaid.kickexempt"));
		config.set("StaffMember", player.hasPermission("adminaid.staffmember"));
		if(config.get("ChatSpy") == null) config.set("ChatSpy", false);
		config.set("IPAddress", ipAddress);
		config.set("Location.X", xCoord);
		config.set("Location.Y", yCoord);
		config.set("Location.Z", zCoord);
		config.set("Location.World", world);
		config.set("NewMail", mailListNew);
		config.set("ReadMail", mailListRead);
		//FileUtilities.saveYamlFile(config, file);
		FileUtilities.saveYamlConfiguration(plugin, config, player.getUniqueId());
		
		/* Removes player from a private message conversation
		 * as player is no longer online */
		while(AdminAid.lastSender.values().remove(player.getName())) { }
		if(AdminAid.staffChat.contains(player.getName())) {
			AdminAid.staffChat.remove(player.getName());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		
		misc = new MiscUtilities(plugin);
		
		Player player = event.getPlayer();
		//File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		//YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration config = FileUtilities.loadYamlConfiguration(plugin, player.getUniqueId());
		
		/* Checks if player is banned or tempbanned */
		if(misc.isPermaBanned(player)) {
			event.setResult(Result.KICK_BANNED);
			String defaultMessage = "permanently banned from this server";
			event.setKickMessage("You are " + config.getString("PermaBanReason", defaultMessage));
		}
		else if(misc.isTempBanned(player)) {
			String defaultMessage = "temporarily banned from this server";
			if(System.currentTimeMillis()/1000 >= config.getDouble("TempBanEnd")) {
				Bukkit.getBanList(Type.NAME).pardon(player.getName());
				config.set("TempBanned", null);
				config.set("TempBanReason", null);
				config.set("TempBanEnd", null);
				//FileUtilities.saveYamlFile(config, file);
				FileUtilities.saveYamlConfiguration(plugin, config, player.getUniqueId());
			}
			else {
				event.setResult(Result.KICK_BANNED);
				event.setKickMessage("You are " + config.getString("TempBanReason", defaultMessage));
			}
		}
	}
}
