package ca.uvic.lscholte.listeners;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;

public class ChatListener implements Listener {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	//private ConfigValues config;
	
	public ChatListener(AdminAid instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		misc = new MiscUtilities(plugin);
		//config = new ConfigValues(plugin);
		
		Player player = event.getPlayer();
		//File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		//YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration config = FileUtilities.loadYamlConfiguration(plugin, player.getUniqueId());
		
		/* Checks for chat from players with StaffChat enabled */
		if(AdminAid.staffChat.contains(player.getName())) {
			String prefix = ChatColor.translateAlternateColorCodes('&', ConfigConstants.STAFF_CHAT_COLOR + "[StaffChat] " + player.getName() + ": " + ChatColor.WHITE);
			String message = event.getMessage();
			Bukkit.getServer().getConsoleSender().sendMessage(prefix + message);
			for(Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
				if(onlinePlayer.hasPermission("adminaid.staffmember")) {
					onlinePlayer.sendMessage(prefix + message);
				}
			}
			event.setCancelled(true);
		}
		
		/* Checks for chat from muted players */
		if(misc.isPermaMuted(player)) {
			event.setCancelled(true);
			String defaultMessage = "permanently muted";
			player.sendMessage(ChatColor.RED + "You are " + config.getString("PermaMuteReason", defaultMessage));
		}
		else if(misc.isTempMuted(player)) {
			String defaultMessage = "temporarily muted";
			if(System.currentTimeMillis()/1000 >= config.getDouble("TempMuteEnd")) {
				config.set("TempMuted", null);
				config.set("TempMuteReason", null);
				config.set("TempMuteEnd", null);
				//FileUtilities.saveYamlFile(config, file);
				FileUtilities.saveYamlConfiguration(plugin, config, player.getUniqueId());
			}
			else {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are " + config.getString("TempMuteReason", defaultMessage));
			}
		}
	}
}
