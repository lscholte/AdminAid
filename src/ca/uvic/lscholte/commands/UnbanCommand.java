package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.List;

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
import ca.uvic.lscholte.ConfigValues;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class UnbanCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	private ConfigValues config;
	
	public UnbanCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("unban").setExecutor(this);
		if(plugin.getConfig().getBoolean("DisableCommand.Unban") == true) {
			PluginCommand unban = plugin.getCommand("unban");
			CommandUtilities.unregisterBukkitCommand(unban);
		}
	}
		
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		
		misc = new MiscUtilities(plugin);
		config = new ConfigValues(plugin);
		
		if(!sender.hasPermission("adminaid.unban")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/unban <playername> <reason> " + ChatColor.RED + "to unban player");
			return true;
		}
		if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
		
		OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
		
		if(!misc.isPermaBanned(targetPlayer) && !misc.isTempBanned(targetPlayer)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is not banned");
			return true;
		}
		
		File file = new File(plugin.getDataFolder() + "/userdata/" + targetPlayer.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> noteList = userFile.getStringList("Notes");
		
		FileUtilities.createNewFile(file);
		
		String prefix = new ConfigValues(plugin).getPrefix(sender);		
		String message = StringUtilities.buildString(args, 1);
							
		Bukkit.getBanList(Type.NAME).pardon(targetPlayer.getName());
		userFile.set("PermaBanned", null);
		userFile.set("PermaBanReason", null);
		userFile.set("TempBanned", null);
		userFile.set("TempBanReason", null);
		userFile.set("TempBanEnd", null);

		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been unbanned for this reason: " + message);
		
		if(config.broadcastUnbans() == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + targetPlayer.getName() + " has been unbanned for this reason: " + message);
		}
		
		if(config.autoRecordUnbans() == true) {
			noteList.add(prefix + "has been unbanned for this reason: " + message);
			misc.addStringStaffList(prefix + targetPlayer.getName() + " has been unbanned for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlFile(userFile, file);
		return true;
	}
}
