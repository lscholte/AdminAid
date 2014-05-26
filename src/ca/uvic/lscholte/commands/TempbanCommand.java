package ca.uvic.lscholte.commands;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.OnTimeUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class TempbanCommand implements CommandExecutor {

	private AdminAid plugin;
	private ConfigConstants constants;
	
	public TempbanCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Tempban") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "tempban");	
		}
		else {
			PluginCommand com = plugin.getCommand("tempban");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("adminaid.tempban")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tempban <player> <time> <reason> " + ChatColor.RED + "to tempban player");
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
		
		if(userFile.getBoolean("BanExempt") == true && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is exempt from being banned");
			return true;
		}
		
		if(MiscUtilities.isPermaBanned(plugin, targetPlayer)) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is already permanently banned");
			return true;
		}
		
		double unbanTime = 0;
		Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)([wdhms]{1})");
		Matcher matcher = pattern.matcher(args[1]);
		
		if(matcher.matches()) {
			int pos = matcher.start(2);
			String number = args[1].substring(0, pos);
			String letter = args[1].substring(pos);
			double i = Double.parseDouble(number);
			if(letter.equalsIgnoreCase("w")) {
				unbanTime = i*604800;
			}
			if(letter.equalsIgnoreCase("d")) {
				unbanTime = i*86400;
			}
			if(letter.equalsIgnoreCase("h")) {
				unbanTime = i*3600;
			}
			if(letter.equalsIgnoreCase("m")) {
				unbanTime = i*60;
			}
			if(letter.equalsIgnoreCase("s")) {
				unbanTime = i;
			}		
		}		
		
		if(unbanTime <= 0) {
			sender.sendMessage(ChatColor.RED + "That is an invalid time argument");
			sender.sendMessage(ChatColor.RED + "Time must be a number followed by a w, d, h, m, or s meaning weeks, days, hours, minutes, or seconds respectively");
			return true;
		}
		
		Date unbanDateUnformatted = new Date((long) (System.currentTimeMillis() + unbanTime*1000));
		String unbanDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a z").format(unbanDateUnformatted);
		
		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 2);
		
		Bukkit.getBanList(Type.NAME).addBan(targetPlayer.getName(), message, unbanDateUnformatted, sender.getName());
		
		userFile.set("TempBanned", true);
		userFile.set("TempBanReason", "tempbanned until " + unbanDate + " for this reason: " + message);
		userFile.set("TempBanEnd", (System.currentTimeMillis()/1000) + unbanTime);
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been tempbanned until " + unbanDate + " for this reason: " + message);
		if(targetPlayer.isOnline()) {
			Bukkit.getServer().getPlayer(args[0]).kickPlayer("You are tempbanned until " + unbanDate + " for this reason: " + message);
		}
		
		if(constants.BROADCAST_TEMPBANS == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been tempbanned for " + OnTimeUtilities.splitSeconds(unbanTime) + " for this reason: " + message);
		}
		
		if(constants.AUTO_RECORD_TEMPBANS == true) {
			noteList.add(prefix + "has been tempbanned for " + OnTimeUtilities.splitSeconds(unbanTime) + " for this reason: " + message);
			MiscUtilities.addStringStaffList(plugin, prefix + targetPlayer.getName() + " has been tempbanned for " + OnTimeUtilities.splitSeconds(unbanTime) + " for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		return true;
	}
}
