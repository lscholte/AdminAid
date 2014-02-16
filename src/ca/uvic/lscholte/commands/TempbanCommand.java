package ca.uvic.lscholte.commands;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import ca.uvic.lscholte.ConfigValues;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.OnTimeUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class TempbanCommand implements CommandExecutor {

	private AdminAid plugin;
	private MiscUtilities misc;
	private ConfigValues config;
	
	public TempbanCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("tempban").setExecutor(this);
		if(plugin.getConfig().getBoolean("DisableCommand.Tempban") == true) {
			PluginCommand tempban = plugin.getCommand("tempban");
			CommandUtilities.unregisterBukkitCommand(tempban);
		}
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {		
		
		misc = new MiscUtilities(plugin);
		config = new ConfigValues(plugin);

		if(!sender.hasPermission("adminaid.tempban")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tempban <playername> <time> <reason> " + ChatColor.RED + "to tempban player");
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
		
		String prefix = new ConfigValues(plugin).getPrefix(sender);
		String message = StringUtilities.buildString(args, 2);
		
		Bukkit.getBanList(Type.NAME).addBan(targetPlayer.getName(), message, unbanDateUnformatted, sender.getName());
		
		FileUtilities.createNewFile(file);
		userFile.set("TempBanned", true);
		userFile.set("TempBanReason", "tempbanned until " + unbanDate + " for this reason: " + message);
		userFile.set("TempBanEnd", (System.currentTimeMillis()/1000) + unbanTime);
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been tempbanned until " + unbanDate + " for this reason: " + message);
		if(targetPlayer.isOnline()) {
			Bukkit.getServer().getPlayer(args[0]).kickPlayer("You are tempbanned until " + unbanDate + " for this reason: " + message);
		}
		
		if(config.broadcastTempbans() == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been tempbanned for " + OnTimeUtilities.splitSeconds(unbanTime) + " for this reason: " + message);
		}
		
		if(config.autoRecordTempbans() == true) {
			noteList.add(prefix + "has been tempbanned for " + OnTimeUtilities.splitSeconds(unbanTime) + " for this reason: " + message);
			misc.addStringStaffList(prefix + targetPlayer.getName() + " has been tempbanned for " + OnTimeUtilities.splitSeconds(unbanTime) + " for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlFile(userFile, file);
		return true;
	}
}
