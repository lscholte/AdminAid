package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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

public class MuteCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public MuteCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Mute") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "mute");	
		}
		else {
			PluginCommand com = plugin.getCommand("mute");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
		
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("adminaid.mute")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mute <player> <reason> " + ChatColor.RED + "to mute player");
			return true;
		}
		
		if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid player name");
			return true;
		}
		
		//TODO: Update for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(args[0]) != null ?
				Bukkit.getServer().getPlayer(args[0]) :
				Bukkit.getServer().getOfflinePlayer(args[0]);
				
		UUID uuid = targetPlayer.getUniqueId();
		
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> noteList = userFile.getStringList("Notes");
		
		if(userFile.getBoolean("MuteExempt") == true && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is exempt from being muted");
			return true;
		}

		if(MiscUtilities.isPermaMuted(plugin, targetPlayer)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is already permanently muted");
			return true;
		}
		
		userFile.set("PermaMuted", true);
		
		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		userFile.set("PermaMuteReason", "muted for this reason: " + message);
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been muted for this reason: " + message);
		
		if(constants.BROADCAST_MUTES == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been muted for this reason: " + message);
		}
		
		if(constants.AUTO_RECORD_MUTES == true) {
			noteList.add(prefix + "has been muted for this reason: " + message);
			MiscUtilities.addStringStaffList(plugin, prefix + targetPlayer.getName() + " has been muted for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		return true;
	}
}
