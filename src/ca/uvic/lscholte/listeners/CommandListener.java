package ca.uvic.lscholte.listeners;

import java.io.File;
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
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;

public class CommandListener implements Listener {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	
	public CommandListener(AdminAid instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		misc = new MiscUtilities(plugin);
		
		List<String> commands = plugin.getConfig().getStringList("BlockedCommandsWhenMuted");
		List<String> blockedCommands = new ArrayList<String>();
		for(String s : commands) {
			if(Bukkit.getServer().getPluginCommand(s) != null) {
				blockedCommands.addAll(Bukkit.getServer().getPluginCommand(s).getAliases());
			}
			blockedCommands.add(s);
		}
		
		for(String com : blockedCommands) {
			if(event.getMessage().startsWith("/" + com + " ")) {
				Player player = event.getPlayer();
				File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
				YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
				
				if(misc.isPermaMuted(player)) {
					event.setCancelled(true);
					String defaultMessage = "permanently muted";
					player.sendMessage(ChatColor.RED + "You are " + userFile.getString("PermaMuteReason", defaultMessage));
				}
				else if(misc.isTempMuted(player)) {
					String defaultMessage = "temporarily muted";
					if(System.currentTimeMillis()/1000 >= userFile.getDouble("TempMuteEnd")) {
						userFile.set("TempMuted", null);
						userFile.set("TempMuteReason", null);
						userFile.set("TempMuteEnd", null);
						FileUtilities.saveYamlFile(userFile, file);
					}
					else {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You are " + userFile.getString("TempMuteReason", defaultMessage));
					}
				}
				break;
			}
		}	
	}
}
