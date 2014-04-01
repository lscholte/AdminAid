package ca.uvic.lscholte.listeners;

import java.io.File;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

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
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> mailListNew = userFile.getStringList("NewMail");
		List<String> mailListRead = userFile.getStringList("ReadMail");
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		
		List<Runnable> runnables = new ArrayList<Runnable>();
		runnables.add(new LoginRunnables.LoginMessagesRunnable(plugin, player));
		runnables.add(new LoginRunnables.MailRunnable(plugin, player));
		
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new LoginRunnables.UpdaterRunnable(plugin, player));
		
		for(Runnable r : runnables) {
			plugin.getServer().getScheduler().runTask(plugin, r);
		}
		
		/* Sets various statuses for the player in their userdata file */
		userFile.set("BanExempt", player.hasPermission("adminaid.banexempt"));
		userFile.set("MuteExempt", player.hasPermission("adminaid.muteexempt"));
		userFile.set("KickExempt", player.hasPermission("adminaid.kickexempt"));
		userFile.set("StaffMember", player.hasPermission("adminaid.staffmember"));
		if(userFile.get("ChatSpy") == null) userFile.set("ChatSpy", false);
		userFile.set("IPAddress", ipAddress);
		userFile.set("NewMail", mailListNew);
		userFile.set("ReadMail", mailListRead);
		FileUtilities.saveYamlFile(userFile, file);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		misc = new MiscUtilities(plugin);
		
		Player player = event.getPlayer();
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> mailListNew = userFile.getStringList("NewMail");
		List<String> mailListRead = userFile.getStringList("ReadMail");
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		Location loc = player.getLocation();
		int xCoord = loc.getBlockX();
		int yCoord = loc.getBlockY();
		int zCoord = loc.getBlockZ();
		String world = loc.getWorld().getName();
	
		/* Sets various statuses for the player in their userdata file */
		userFile.set("BanExempt", player.hasPermission("adminaid.banexempt"));
		userFile.set("MuteExempt", player.hasPermission("adminaid.muteexempt"));
		userFile.set("KickExempt", player.hasPermission("adminaid.kickexempt"));
		userFile.set("StaffMember", player.hasPermission("adminaid.staffmember"));
		if(userFile.get("ChatSpy") == null) userFile.set("ChatSpy", false);
		userFile.set("IPAddress", ipAddress);
		userFile.set("Location.X", xCoord);
		userFile.set("Location.Y", yCoord);
		userFile.set("Location.Z", zCoord);
		userFile.set("Location.World", world);
		userFile.set("NewMail", mailListNew);
		userFile.set("ReadMail", mailListRead);
		FileUtilities.saveYamlFile(userFile, file);
		
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
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		
		/* Checks if player is banned or tempbanned */
		if(misc.isPermaBanned(player)) {
			event.setResult(Result.KICK_BANNED);
			String defaultMessage = "permanently banned from this server";
			event.setKickMessage("You are " + userFile.getString("PermaBanReason", defaultMessage));
		}
		else if(misc.isTempBanned(player)) {
			String defaultMessage = "temporarily banned from this server";
			if(System.currentTimeMillis()/1000 >= userFile.getDouble("TempBanEnd")) {
				Bukkit.getBanList(Type.NAME).pardon(player.getName());
				userFile.set("TempBanned", null);
				userFile.set("TempBanReason", null);
				userFile.set("TempBanEnd", null);
				FileUtilities.saveYamlFile(userFile, file);
			}
			else {
				event.setResult(Result.KICK_BANNED);
				event.setKickMessage("You are " + userFile.getString("TempBanReason", defaultMessage));
			}
		}
	}
}
