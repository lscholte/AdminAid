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
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.OnTimeUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class WarnCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public WarnCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Warn") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "warn");	
		}
		else {
			PluginCommand com = plugin.getCommand("warn");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("adminaid.warn")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/warn <player> <reason> " + ChatColor.RED + "to warn player");
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
		List<String> mailListNew = userFile.getStringList("NewMail");
		
		String prefix = constants.getPrefix(sender);		
		String message = StringUtilities.buildString(args, 1);
		
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been warned for this reason: " + message);
		
		if(constants.BROADCAST_WARNS == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been warned for this reason: " + message);
		}
		
		if(constants.AUTO_RECORD_WARNS == true) {
			noteList.add(prefix + "has been warned for this reason: " + message);
			MiscUtilities.addStringStaffList(plugin, prefix + targetPlayer.getName() + " has been warned for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		if(targetPlayer.isOnline()) {
			Player player = Bukkit.getServer().getPlayer(args[0]);
			player.sendMessage(ChatColor.RED + "You have been warned for this reason: " + message);
			
			if(constants.FREEZE_PLAYERS_ON_WARNS == true) {
				int freezeTime = constants.FREEZE_TIME;
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*freezeTime, 128));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*freezeTime, 128));
				player.sendMessage(ChatColor.RED + "You will be unfrozen after " + OnTimeUtilities.splitSeconds(freezeTime));
			}
		}
			
		mailListNew.add(prefix + ChatColor.RED + "You have been warned for this reason: " + message);
		userFile.set("NewMail", mailListNew);
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		return true;
	}
}
