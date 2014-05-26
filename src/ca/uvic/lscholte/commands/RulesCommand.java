package ca.uvic.lscholte.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.NumberUtilities;

public class RulesCommand implements CommandExecutor {
		
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public RulesCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Rules") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "rules");	
		}
		else {
			PluginCommand com = plugin.getCommand("rules");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("adminaid.rules")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/rules [page #] " + ChatColor.RED + "to show the rules");
			return true;
		}
		
		List<String> inputList = plugin.getConfig().getStringList("Rules");
		
		if(inputList == null || inputList.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "No rules have been listed");
			return true;
		}
		
		if(args.length == 1 && (!NumberUtilities.isInt(args[0]) || Integer.parseInt(args[0]) <= 0)) {
			sender.sendMessage(ChatColor.RED + "That is an invalid page number");
			return true;
		}
		
		int page = args.length == 0 ? 1 : Integer.parseInt(args[0]);
		int totalPages = MiscUtilities.getTotalPages(inputList, constants.RULES_PER_PAGE);

		if(page > totalPages) {
			sender.sendMessage(ChatColor.RED + "There are only " + totalPages + " pages of rules");
			return true;
		}
				
		List<String> outputList = MiscUtilities.getListPage(inputList, page, constants.RULES_PER_PAGE);
		sender.sendMessage(ChatColor.GOLD + "Rule Page " + page + " of " + totalPages);
		for(String output : outputList) {
			sender.sendMessage(output);
		}
		return true;
	}
}
