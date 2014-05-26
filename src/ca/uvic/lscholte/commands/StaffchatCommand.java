package ca.uvic.lscholte.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ca.uvic.lscholte.AdminAid;

public class StaffchatCommand implements CommandExecutor {
		
	public StaffchatCommand(AdminAid plugin) {
		plugin.getCommand("staffchat").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("adminaid.staffmember")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/staffchat " + ChatColor.RED + "to toggle staff chat");
			return true;
		}
		
		if(AdminAid.staffChat.contains(sender.getName())) {
			AdminAid.staffChat.remove(sender.getName());
			sender.sendMessage(ChatColor.GREEN + "You have left the staff chat");
			sender.sendMessage(ChatColor.GREEN + "All messages send will be visible to everybody");
			return true;
		}
		AdminAid.staffChat.add(sender.getName());
		sender.sendMessage(ChatColor.GREEN + "You have joined the staff chat");
		sender.sendMessage(ChatColor.GREEN + "All messages sent will only be visible to staff members");
		return true;
	}
}
