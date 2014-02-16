package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigValues;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class MuteCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	private ConfigValues config;
	
	public MuteCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("mute").setExecutor(this);
		if(plugin.getConfig().getBoolean("DisableCommand.Mute") == true) {
			PluginCommand mute = plugin.getCommand("mute");
			CommandUtilities.unregisterBukkitCommand(mute);
		}
	}
		
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		
		misc = new MiscUtilities(plugin);
		config = new ConfigValues(plugin);

		if(!sender.hasPermission("adminaid.mute")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mute <playername> <reason> " + ChatColor.RED + "to mute player");
			return true;
		}
		if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
		
		OfflinePlayer targetPlayer;
		if(Bukkit.getServer().getPlayer(args[0]) != null) targetPlayer = Bukkit.getServer().getPlayer(args[0]);
		else targetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
		
		File file = new File(plugin.getDataFolder() + "/userdata/" + targetPlayer.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> noteList = userFile.getStringList("Notes");
		
		if(userFile.getBoolean("MuteExempt") == true && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is exempt from being muted");
			return true;
		}

		if(misc.isPermaMuted(targetPlayer)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is already permanently muted");
			return true;
		}
		
		FileUtilities.createNewFile(file);
		userFile.set("PermaMuted", true);
		
		String prefix = new ConfigValues(plugin).getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		userFile.set("PermaMuteReason", "muted for this reason: " + message);
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been muted for this reason: " + message);
		
		if(config.broadcastMutes() == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been muted for this reason: " + message);
		}
		
		if(config.autoRecordMutes() == true) {
			noteList.add(prefix + "has been muted for this reason: " + message);
			misc.addStringStaffList(prefix + targetPlayer.getName() + " has been muted for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlFile(userFile, file);
		return true;
	}
}
