package ca.uvic.lscholte.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import ca.uvic.lscholte.utilities.StringUtilities;

public class InfoCommand implements CommandExecutor {
		
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public InfoCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Info") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "info");	
		}
		else {
			PluginCommand com = plugin.getCommand("info");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
		if(!sender.hasPermission("adminaid.info")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/info <topic> [page #] " + ChatColor.RED + "for more information");
			return true;
		}
		
		Set<String> topicSet;
		topicSet = sender.hasPermission("adminaid.staffmember") ?
				plugin.getConfig().getConfigurationSection("StaffInfo").getKeys(false) :
				plugin.getConfig().getConfigurationSection("Info").getKeys(false);
		List<String> topicList = new ArrayList<String>(topicSet);	
		
		if(args.length == 0) {
			if(!topicList.isEmpty()) {
				for(String topic : topicList) {
					sender.sendMessage(ChatColor.GREEN + "Use " + ChatColor.WHITE + "/info " + topic.toLowerCase() + " [page #]" + ChatColor.GREEN + " for more information");
				}
				return true;
			}
			
			sender.sendMessage(ChatColor.RED + "There are no info topics");
			return true;
		}
		
		if(!StringUtilities.containsIgnoreCase(args[0], topicList)) {
			sender.sendMessage(ChatColor.RED + "That topic does not exist");
			return true;
		}
		
		int topicIndex = StringUtilities.getIndexOfString(args[0], topicList);
		String topic = topicList.get(topicIndex);
		
		List<String> inputList;
		if(sender.hasPermission("adminaid.staffmember")) {
			inputList = plugin.getConfig().getStringList("StaffInfo." + topic);
		}
		else {
			inputList = plugin.getConfig().getStringList("Info." + topic);
		}
		
		if(inputList == null || inputList.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "There is no information for the topic " + topic);
			return true;
		}
		
		if(args.length == 2 && (!NumberUtilities.isInt(args[1]) || Integer.parseInt(args[1]) <= 0)) {
			sender.sendMessage(ChatColor.RED + "That is an invalid page number");
			return true;
		}
		
		int page = args.length == 1 ? 1 : Integer.parseInt(args[1]);
		int totalPages = MiscUtilities.getTotalPages(inputList, constants.INFO_PER_PAGE);

		if(page > totalPages) {
			sender.sendMessage(ChatColor.RED + "There are only " + totalPages + " pages of info for the topic " + topic);
			return true;
		}
				
		List<String> outputList = MiscUtilities.getListPage(inputList, page, constants.INFO_PER_PAGE);
		sender.sendMessage(ChatColor.GOLD + "Info Page " + page + " of " + totalPages + " for Topic " + topic);
		for(String output : outputList) {
			sender.sendMessage(output);
		}
		return true;
	}
}
