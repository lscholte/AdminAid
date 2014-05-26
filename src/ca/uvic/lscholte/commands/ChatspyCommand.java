package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.utilities.FileUtilities;

public class ChatspyCommand implements CommandExecutor {
	
	private AdminAid plugin;
	
	public ChatspyCommand(AdminAid plugin) {
		this.plugin = plugin;
		plugin.getCommand("chatspy").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/chatspy [player] " + ChatColor.RED + "to toggle chatspy");
			return true;
		}

		if(args.length == 1) {
			if(!sender.hasPermission("adminaid.chatspy.others")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to toggle chatspy for other players");
				return true;
			}
			
			// TODO: Update for UUIDs
			OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
			
			UUID uuid = targetPlayer.getUniqueId();
			
			YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);

			
			if(userFile.getBoolean("ChatSpy") == false) {
				userFile.set("ChatSpy", true);
				sender.sendMessage(ChatColor.GREEN + "ChatSpy enabled for " + targetPlayer.getName());
				FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
				return true;
			}
			
			userFile.set("ChatSpy", false);
			sender.sendMessage(ChatColor.GREEN + "ChatSpy disabled for " + targetPlayer.getName());
			FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
			return true;
		}
				
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "The console can already see all private messages");
			return true;
		}
		
		if(!sender.hasPermission("adminaid.chatspy")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		// TODO: Update for UUIDs
		File file = new File(plugin.getDataFolder() + "/userdata/" + ((Player) sender).getUniqueId() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		
		if(userFile.getBoolean("ChatSpy") == false) {
			userFile.set("ChatSpy", true);
			sender.sendMessage(ChatColor.GREEN + "ChatSpy enabled. You will now see everyone's private messages");
			FileUtilities.saveYamlFile(userFile, file);
			return true;
		}
		
		userFile.set("ChatSpy", false);
		sender.sendMessage(ChatColor.GREEN + "ChatSpy disabled. You will no longer see everyone's private messages");
		FileUtilities.saveYamlFile(userFile, file);
		return true;
	}
}
