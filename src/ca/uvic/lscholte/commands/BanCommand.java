package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class BanCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public BanCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		
		if(plugin.getConfig().getBoolean("DisableCommand.Ban") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "ban");	
		}
		else {
			PluginCommand com = plugin.getCommand("ban");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
					
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {			
		if(!sender.hasPermission("adminaid.ban")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/ban <player> <reason> " + ChatColor.RED + "to ban player");
			return true;
		}
		
		if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid player name");
			return true;
		}

		// TODO: This may need to be updated for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(args[0]) != null ?
				Bukkit.getServer().getPlayer(args[0]) :
				Bukkit.getServer().getOfflinePlayer(args[0]);
				
		UUID uuid = targetPlayer.getUniqueId();
								
				
		// TODO: Update this for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> noteList = userFile.getStringList("Notes");
		
		if(userFile.getBoolean("BanExempt") == true && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is exempt from being banned");
			return true;
		}
		
		if(MiscUtilities.isPermaBanned(plugin, targetPlayer)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is already permanently banned");
			return true;
		}
		
		//FileUtilities.createNewFile(file);
		
		
		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		Bukkit.getBanList(Type.NAME).addBan(targetPlayer.getName(), message, null, sender.getName());
		
		userFile.set("PermaBanned", true);
		userFile.set("PermaBanReason", "banned for this reason: " + message);
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been banned for this reason: " + message);
		
		if(targetPlayer.isOnline()) {
			Bukkit.getServer().getPlayer(args[0]).kickPlayer("You are banned for this reason: " + message);
		}
		
		if(constants.BROADCAST_BANS == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been banned for this reason: " + message);
		}

		if(constants.AUTO_RECORD_BANS == true) {
			noteList.add(prefix + "has been banned for this reason: " + message);
			MiscUtilities.addStringStaffList(plugin, prefix + targetPlayer.getName() + " has been banned for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		//FileUtilities.saveYamlFile(userFile, file);
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		return true;
	}
}
