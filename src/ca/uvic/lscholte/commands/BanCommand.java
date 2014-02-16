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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigValues;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class BanCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	private ConfigValues config;
	
	public BanCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("ban").setExecutor(this);
		if(plugin.getConfig().getBoolean("DisableCommand.Ban") == true) {
			PluginCommand ban = plugin.getCommand("ban");
			CommandUtilities.unregisterBukkitCommand(ban);
		}
	}
					
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {	
		
		misc = new MiscUtilities(plugin);
		config = new ConfigValues(plugin);
		
		if(!sender.hasPermission("adminaid.ban")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/ban <playername> <reason> " + ChatColor.RED + "to ban player");
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
		
		if(userFile.getBoolean("BanExempt") == true && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is exempt from being banned");
			return true;
		}
		if(misc.isPermaBanned(targetPlayer)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is already permanently banned");
			return true;
		}
		
		FileUtilities.createNewFile(file);
		
		String prefix = new ConfigValues(plugin).getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		Bukkit.getBanList(Type.NAME).addBan(targetPlayer.getName(), message, null, sender.getName());
		
		userFile.set("PermaBanned", true);
		userFile.set("PermaBanReason", "banned for this reason: " + message);
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been banned for this reason: " + message);
		if(targetPlayer.isOnline()) {
			Bukkit.getServer().getPlayer(args[0]).kickPlayer("You are banned for this reason: " + message);
		}
		if(config.broadcastBans() == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been banned for this reason: " + message);
		}
		
		if(config.autoRecordBans() == true) {
			noteList.add(prefix + "has been banned for this reason: " + message);
			misc.addStringStaffList(prefix + targetPlayer.getName() + " has been banned for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlFile(userFile, file);
		return true;
	}
}
