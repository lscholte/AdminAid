package ca.uvic.lscholte.commands;

import java.io.File;

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
import ca.uvic.lscholte.ConfigValues;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class MsgCommand implements CommandExecutor {
	
	private AdminAid plugin;
	
	public MsgCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("msg").setExecutor(this);
		if(plugin.getConfig().getBoolean("DisableCommand.Msg") == true) {
			PluginCommand msg = plugin.getCommand("msg");
			CommandUtilities.unregisterBukkitCommand(msg);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			
		if(!sender.hasPermission("adminaid.msg")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/msg <playername> <message> " + ChatColor.RED + "to send private message");
			return true;
		}
		if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
		
		final Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
		
		if(targetPlayer == null && !args[0].equalsIgnoreCase("CONSOLE")) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online. Try sending a mail message instead");
			return true;
		}
		
		String senderName;
		if(sender instanceof Player) senderName = sender.getName();
		else if(sender instanceof ConsoleCommandSender) senderName = "CONSOLE";
		else senderName = "CommandBlock";
		
		CommandSender target;
		String targetName;
		if(args[0].equalsIgnoreCase("CONSOLE")) {
			target = plugin.getServer().getConsoleSender();
			targetName = "CONSOLE";
		}
		else {
			target = targetPlayer;
			targetName = targetPlayer.getName();
		}
				
		StringBuilder strBuilder = new StringBuilder();				
		for(int i = 1; i < args.length; ++i) {
			strBuilder.append(args[i] + " ");
		}
		String message = strBuilder.toString().trim();
		
		ConfigValues config = new ConfigValues(plugin);
		
		String spyPrefix = ChatColor.translateAlternateColorCodes('&', config.getChatSpyColor() + "[" + senderName + " to " + targetName + "] ") + ChatColor.WHITE;
		String targetPrefix = ChatColor.translateAlternateColorCodes('&', config.getPrivateMessageColor() + "[" + senderName + " to You] ") + ChatColor.WHITE;
		String senderPrefix = ChatColor.translateAlternateColorCodes('&', config.getPrivateMessageColor() + "[You to " + targetName + "] ") + ChatColor.WHITE;
		target.sendMessage(targetPrefix + message);
		sender.sendMessage(senderPrefix + message);
		
		/* Sends copy of private message to
		 * all players with ChatSpy enabled */
		for(Player spy : Bukkit.getServer().getOnlinePlayers()) {
			File file = new File(plugin.getDataFolder() + "/userdata/" + spy.getName().toLowerCase() + ".yml");
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
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
