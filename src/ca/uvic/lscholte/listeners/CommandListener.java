package ca.uvic.lscholte.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;

public class CommandListener implements Listener {
	
	private AdminAid plugin;
	
	public CommandListener(AdminAid plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		//TODO: Implement this using ConfigConstants
		List<String> commands = plugin.getConfig().getStringList("BlockedCommandsWhenMuted");
		List<String> blockedCommands = new ArrayList<String>();
		for(String s : commands) {
			if(Bukkit.getServer().getPluginCommand(s) != null) {
				blockedCommands.addAll(Bukkit.getServer().getPluginCommand(s).getAliases());
			}
			blockedCommands.add(s);
		}
		
		for(String com : blockedCommands) {
			if(!event.getMessage().startsWith("/" + com + " ")) continue;
			
			Player player = event.getPlayer();
			//File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
			//YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
			YamlConfiguration config = FileUtilities.loadYamlConfiguration(plugin, player.getUniqueId());
			
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
			break;
		}	
	}
}
