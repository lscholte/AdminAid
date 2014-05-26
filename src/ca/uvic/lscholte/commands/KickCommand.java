package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class KickCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public KickCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Kick") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "kick");	
		}
		else {
			PluginCommand com = plugin.getCommand("kick");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
			
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {	
		if(!sender.hasPermission("adminaid.kick")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/kick <player> <reason> " + ChatColor.RED + "to kick player");
			return true;
		}
					
		//TODO: May need to update for UUIDs
		//However, targetPlayer in this case is an online player
		//so nothing may need to actually be changed
		Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
		
		if(targetPlayer == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online");
			return true;
		}
		
		UUID uuid = targetPlayer.getUniqueId();
		
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> noteList = userFile.getStringList("Notes");
		
		if(userFile.getBoolean("KickExempt") == true && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is exempt from being kicked");
			return true;
		}
				
		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been kicked for this reason: " + message);
		targetPlayer.kickPlayer("You were kicked for this reason: " + message);
		
		if(constants.BROADCAST_KICKS == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been kicked for this reason: " + message);
		}

		if(constants.AUTO_RECORD_KICKS == true) {
			noteList.add(prefix + "has been kicked for this reason: " + message);
			MiscUtilities.addStringStaffList(plugin, prefix + targetPlayer.getName() + " has been kicked for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		return true;
	}
}