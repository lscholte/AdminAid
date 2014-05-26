package ca.uvic.lscholte.commands;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class TempmuteCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public TempmuteCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Tempmute") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "tempmute");	
		}
		else {
			PluginCommand com = plugin.getCommand("tempmute");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
		
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("adminaid.tempmute")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tempmute <player> <time> <reason> " + ChatColor.RED + "to tempmute player");
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
		double unmuteTime = 0;

		Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)([wdhms]{1})");
		Matcher matcher = pattern.matcher(args[1]);

		if(matcher.matches()) {
			
			int pos = matcher.start(2);
			String number = args[1].substring(0, pos);
			String letter = args[1].substring(pos);
			double i = Double.parseDouble(number);
			
			if(letter.equalsIgnoreCase("w")) {
				unmuteTime = i*604800;
			}
			if(letter.equalsIgnoreCase("d")) {
				unmuteTime = i*86400;
			}
			if(letter.equalsIgnoreCase("h")) {
				unmuteTime = i*3600;
			}
			if(letter.equalsIgnoreCase("m")) {
				unmuteTime = i*60;
			}
			if(letter.equalsIgnoreCase("s")) {
				unmuteTime = i;
			}				
		}
							
		if(unmuteTime <= 0) {
			sender.sendMessage(ChatColor.RED + "That is an invalid time argument");
			sender.sendMessage(ChatColor.RED + "Time must be a number followed by a w, d, h, m, or s meaning weeks, days, hours, minutes, or seconds respectively");
			return true;
		}
		
		Date unmuteDateUnformatted = new Date((long) (System.currentTimeMillis() + unmuteTime*1000));
		String unmuteDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a z").format(unmuteDateUnformatted);
		
		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 2);
		
		userFile.set("TempMuted", true);
		userFile.set("TempMuteReason", "tempmuted until " + unmuteDate + " for this reason: " + message);
		userFile.set("TempMuteEnd", (System.currentTimeMillis()/1000) + unmuteTime);
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been tempmuted until " + unmuteDate + " for this reason: " + message);
		
		if(constants.BROADCAST_TEMPMUTES == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been tempmuted for " + OnTimeUtilities.splitSeconds(unmuteTime) + " for this reason: " + message);
		}
		
		if(constants.AUTO_RECORD_TEMPMUTES == true) {
			noteList.add(prefix + "has been tempmuted for " + OnTimeUtilities.splitSeconds(unmuteTime) + " for this reason: " + message);
			MiscUtilities.addStringStaffList(plugin, prefix + targetPlayer.getName() + " has been tempmuted for " + OnTimeUtilities.splitSeconds(unmuteTime) + " for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		return true;
	}
}