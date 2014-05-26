package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class ReplyCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public ReplyCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Reply") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "reply");	
		}
		else {
			PluginCommand com = plugin.getCommand("reply");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if(!sender.hasPermission("adminaid.msg")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/reply <message> " + ChatColor.RED + "to reply to private message");
			return true;
		}
				
		if(!AdminAid.lastSender.containsKey(sender.getName())) {
			sender.sendMessage(ChatColor.RED + "There isn't a player you can reply to");
			return true;
		}
		
		String senderName;
		if(sender instanceof Player) senderName = sender.getName();
		else if(sender instanceof ConsoleCommandSender) senderName = "CONSOLE";
		else senderName = "CommandBlock";
		
		String targetName = AdminAid.lastSender.get(senderName);
		CommandSender target;
		if(targetName.equalsIgnoreCase("CONSOLE")) {
			target = plugin.getServer().getConsoleSender();
		}
		else {
			target = Bukkit.getServer().getPlayer(targetName);
		}

		String message = StringUtilities.buildString(args, 0);
		
		String spyPrefix = ChatColor.translateAlternateColorCodes('&', constants.CHAT_SPY_COLOR + "[" + senderName + " to " + targetName + "] ") + ChatColor.WHITE;
		String targetPrefix = ChatColor.translateAlternateColorCodes('&', constants.PRIVATE_CHAT_COLOR + "[" + senderName + " to You] ") + ChatColor.WHITE;
		String senderPrefix = ChatColor.translateAlternateColorCodes('&', constants.PRIVATE_CHAT_COLOR + "[You to " + targetName + "] ") + ChatColor.WHITE;
		target.sendMessage(targetPrefix + message);
		sender.sendMessage(senderPrefix + message);
		
		/* Sends copy of private message to
		 * all players with ChatSpy enabled */
		for(Player spy : Bukkit.getServer().getOnlinePlayers()) {
			//TODO: Update for UUIDs
			UUID uuid = spy.getUniqueId();
			YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
			if(userFile.getBoolean("ChatSpy") == true) {
				if(!spy.getName().equalsIgnoreCase(senderName) &&
						!spy.getName().equalsIgnoreCase(targetName)) {
					spy.sendMessage(spyPrefix + message);
				}
			}
		}
		if(sender instanceof BlockCommandSender) return true;
		AdminAid.lastSender.put(targetName, senderName);
		return true;
	}
}
