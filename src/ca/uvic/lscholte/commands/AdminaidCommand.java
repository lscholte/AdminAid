package ca.uvic.lscholte.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ca.uvic.lscholte.AdminAid;

public class AdminaidCommand implements CommandExecutor {
	
	private AdminAid plugin;
	
	public AdminaidCommand(AdminAid plugin) {
		this.plugin = plugin;
		plugin.getCommand("adminaid").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("adminaid.reload")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/adminaid reload " + ChatColor.RED + "to reload the config");
			return true;
		}
		
		if(args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/adminaid reload " + ChatColor.RED + "to reload the config");
			return true;
		}
		
		if(!args[0].equalsIgnoreCase("reload")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/adminaid reload " + ChatColor.RED + "to reload the config");
			return true;	
		}
		
		plugin.reloadConfig();
		sender.sendMessage(ChatColor.GREEN + "AdminAid config reloaded!");
		return true;
	}
}
