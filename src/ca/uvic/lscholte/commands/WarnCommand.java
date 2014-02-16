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
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigValues;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.OnTimeUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class WarnCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	private ConfigValues config;
	
	public WarnCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("warn").setExecutor(this);
		if(plugin.getConfig().getBoolean("DisableCommand.Warn") == true) {
			PluginCommand warn = plugin.getCommand("warn");
			CommandUtilities.unregisterBukkitCommand(warn);
		}
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		
		misc = new MiscUtilities(plugin);
		config = new ConfigValues(plugin);

		if(!sender.hasPermission("adminaid.warn")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/warn <playername> <reason> " + ChatColor.RED + "to warn player");
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
		List<String> mailListNew = userFile.getStringList("NewMail");
		
		String prefix = new ConfigValues(plugin).getPrefix(sender);		
		String message = StringUtilities.buildString(args, 1);
		
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " has been warned for this reason: " + message);
		FileUtilities.createNewFile(file);
		
		if(config.broadcastWarns() == true) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + targetPlayer.getName() + " has been warned for this reason: " + message);
		}
		
		if(config.autoRecordWarns() == true) {
			noteList.add(prefix + "has been warned for this reason: " + message);
			misc.addStringStaffList(prefix + targetPlayer.getName() + " has been warned for this reason: " + message);
			userFile.set("Notes", noteList);
		}
		if(targetPlayer.isOnline()) {
			Player player = Bukkit.getServer().getPlayer(args[0]);
			player.sendMessage(ChatColor.RED + "You have been warned for this reason: " + message);
			
			if(config.freezePlayersOnWarns() == true) {
				int freezeTime = config.getFreezeTime();
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*freezeTime, 128));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*freezeTime, 128));
				player.sendMessage(ChatColor.RED + "You will be unfrozen after " + OnTimeUtilities.splitSeconds(freezeTime));
			}
		}
			
		mailListNew.add(prefix + ChatColor.RED + "You have been warned for this reason: " + message);
		userFile.set("NewMail", mailListNew);
		FileUtilities.saveYamlFile(userFile, file);
		return true;
	}
}
