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
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class UnbanCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public UnbanCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Unban") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "unban");	
		}
		else {
			PluginCommand com = plugin.getCommand("unban");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
		
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {		
		if(!sender.hasPermission("adminaid.unban")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/unban <player> <reason> " + ChatColor.RED + "to unban player");
			return true;
		}
		
		if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid player name");
			return true;
		}
		
		//TODO: Update for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
		
		if(!MiscUtilities.isPermaBanned(plugin, targetPlayer) && !MiscUtilities.isTempBanned(plugin, targetPlayer)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is not banned");
			return true;
		}
		
		UUID uuid = targetPlayer.getUniqueId();
		
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> noteList = userFile.getStringList("Notes");
				
		String prefix = constants.getPrefix(sender);		
		String message = StringUtilities.buildString(args, 1);
							
		Bukkit.getBanList(Type.NAME).pardon(targetPlayer.getName());
		userFile.set("PermaBanned", null);
		userFile.set("PermaBanReason", null);
		userFile.set("TempBanned", null);
		userFile.set("TempBanReason", null);
		userFile.set("TempBanEnd", null);

		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been unbanned for this reason: " + message);
		
		if(constants.BROADCAST_UNBANS == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + targetPlayer.getName() + " has been unbanned for this reason: " + message);
		}
		
		if(constants.AUTO_RECORD_UNBANS == true) {
			noteList.add(prefix + "has been unbanned for this reason: " + message);
			MiscUtilities.addStringStaffList(plugin, prefix + targetPlayer.getName() + " has been unbanned for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		return true;
	}
}
