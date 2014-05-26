package ca.uvic.lscholte.listeners;

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
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;

public class ChatListener implements Listener {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public ChatListener(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {		
		Player player = event.getPlayer();
		//File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		//YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration config = FileUtilities.loadYamlConfiguration(plugin, player.getUniqueId());
		
		/* Checks for chat from players with StaffChat enabled */
		if(AdminAid.staffChat.contains(player.getName())) {
			String prefix = ChatColor.translateAlternateColorCodes('&', constants.STAFF_CHAT_COLOR + "[StaffChat] " + player.getName() + ": " + ChatColor.WHITE);
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
		if(MiscUtilities.isPermaMuted(plugin, player)) {
			event.setCancelled(true);
			String defaultMessage = "permanently muted";
			player.sendMessage(ChatColor.RED + "You are " + config.getString("PermaMuteReason", defaultMessage));
		}
		else if(MiscUtilities.isTempMuted(plugin, player)) {
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
