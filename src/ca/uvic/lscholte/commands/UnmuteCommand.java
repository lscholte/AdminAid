package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.List;

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

public class UnmuteCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	private ConfigValues config;
	
	public UnmuteCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("unmute").setExecutor(this);
		if(plugin.getConfig().getBoolean("DisableCommand.Unmute") == true) {
			PluginCommand unmute = plugin.getCommand("unmute");
			CommandUtilities.unregisterBukkitCommand(unmute);
		}
	}
		
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		
		misc = new MiscUtilities(plugin);
		config = new ConfigValues(plugin);

		if(!sender.hasPermission("adminaid.unmute")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/unmute <playername> <reason> " + ChatColor.RED + "to unmute player");
			return true;
		}
		if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
		
		OfflinePlayer targetPlayer;
		if(Bukkit.getServer().getPlayer(args[0]) != null) targetPlayer = Bukkit.getServer().getPlayer(args[0]);
		else targetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
		
		if(!misc.isPermaMuted(targetPlayer) && !misc.isTempMuted(targetPlayer)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is not muted");
			return true;
		}
								
		File file = new File(plugin.getDataFolder() + "/userdata/" + targetPlayer.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> noteList = userFile.getStringList("Notes");
		
		userFile.set("PermaMuted", null);
		userFile.set("PermaMuteReason", null);
		userFile.set("TempMuted", null);
		userFile.set("TempMuteReason", null);
		userFile.set("TempMuteEnd", null);
		
		String prefix = new ConfigValues(plugin).getPrefix(sender);		
		String message = StringUtilities.buildString(args, 1);
		
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been unmuted for this reason: " + message);
		
		if(config.broadcastUnmutes() == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + targetPlayer.getName() + " has been unmuted for this reason: " + message);
		}
		
		if(config.autoRecordUnmutes() == true) {
			noteList.add(prefix + "has been unmuted for this reason: " + message);
			misc.addStringStaffList(prefix + targetPlayer.getName() + " has been unmuted for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlFile(userFile, file);
		return true;
	}
}