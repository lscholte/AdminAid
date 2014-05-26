package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.NumberUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class NoteCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public NoteCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		plugin.getCommand("note").setExecutor(this);
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { 
		if(args.length == 0) {
			return showUsage(sender);
		}
		if(args[0].equalsIgnoreCase("add")) {
			if(sender.hasPermission("adminaid.note.add")) {
				return addNote(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("list")) {
			if(sender.hasPermission("adminaid.note.read")) {
				return listPlayers(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("read")) {
			if(sender.hasPermission("adminaid.note.read") ||
					sender.hasPermission("adminaid.note.read.self")) {
				return readNotes(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("remove")) {
			if(sender.hasPermission("adminaid.note.remove")) {
				return removeSingleNote(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("removeall")) {
			if(sender.hasPermission("adminaid.note.remove")) {
				return removeAllNotes(sender, args);
			}
		}
		else return showUsage(sender);
		sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
		return true;
	}
	
	public boolean addNote(CommandSender sender, String[] args) { 
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note add <player> <message> " + ChatColor.RED + "to add note");
			return true;
		}
		
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid player name");
			return true;
		}
		
		//TODO: Update for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(args[1]) != null ?
				Bukkit.getServer().getPlayer(args[1]) :
				Bukkit.getServer().getOfflinePlayer(args[1]);
				
		UUID uuid = targetPlayer.getUniqueId();
		
		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 2);
		
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> notelist = userFile.getStringList("Notes");
		
		notelist.add(prefix + message);
		MiscUtilities.addStringStaffList(plugin, prefix + "Note about " + targetPlayer.getName() + ": " + message);
		userFile.set("Notes", notelist);
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		sender.sendMessage(ChatColor.GREEN + "note added for " + targetPlayer.getName());
		return true;
	}
	
	public boolean listPlayers(CommandSender sender, String[] args) {	
		if(args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note list [page #] " + ChatColor.RED + "to show players with notes");
			return true;
		}
		
		//TODO: Update for UUIDs
		File dir = new File(plugin.getDataFolder() + "/userdata/");
		File[] children = dir.listFiles();
		List<String> inputList = new ArrayList<String>();
		
		for(File f : children) {
			//TODO: Update for UUIDs
			//File childFile = new File(plugin.getDataFolder() + "/userdata/" + f.getName());
			YamlConfiguration childUserFile = YamlConfiguration.loadConfiguration(f);
			List<String> noteList = childUserFile.getStringList("Notes");
			if(childUserFile.contains("Notes") && noteList.size() != 0) {
				String name = childUserFile.getString("Name");
				inputList.add(name);
			}
		}
		
		if(inputList == null || inputList.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "There are no players with notes");
			return true;
		}
		
		if(args.length == 2 && (!NumberUtilities.isInt(args[1]) || Integer.parseInt(args[1]) <= 0)) {
			sender.sendMessage(ChatColor.RED + "That is an invalid page number");
			return true;
		}
		
		int page = args.length == 1 ? 1 : Integer.parseInt(args[1]);
		int totalPages = MiscUtilities.getTotalPages(inputList, constants.NOTES_PER_PAGE);

		if(page > totalPages) {
			sender.sendMessage(ChatColor.RED + "There are only " + totalPages + " pages of players with notes");
			return true;
		}
				
		List<String> outputList = MiscUtilities.getListPage(inputList, page, constants.NOTES_PER_PAGE);
		sender.sendMessage(ChatColor.GOLD + "Players With Notes Page " + page + " of " + totalPages);
		for(String output : outputList) {
			sender.sendMessage(output);
		}
		return true;
	}

	public boolean readNotes(CommandSender sender, String[] args) {	
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note read <player> [page #] " + ChatColor.RED + "to read notes");
			return true;
		}
		
		if(args.length > 3) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note read <player> [page #] " + ChatColor.RED + "to read notes");
			return true;
		}	
		
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid player name");
			return true;
		}
	
		//TODO: Update for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(args[1]) != null ?
				Bukkit.getServer().getPlayer(args[1]) :
				Bukkit.getServer().getOfflinePlayer(args[1]);
				
		UUID uuid = targetPlayer.getUniqueId();

		
		if(!targetPlayer.getName().equalsIgnoreCase(sender.getName()) && !sender.hasPermission("adminaid.note.read")) {
			sender.sendMessage(ChatColor.RED + "You only have permission to read notes about yourself");
			return true;
		}
			
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> inputList = userFile.getStringList("Notes");
		
		if(inputList == null || inputList.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "There are no notes for " + targetPlayer.getName());
			return true;
		}
		
		if(args.length == 3 && (!NumberUtilities.isInt(args[2]) || Integer.parseInt(args[2]) <= 0)) {
			sender.sendMessage(ChatColor.RED + "That is an invalid page number");
			return true;
		}
		
		int page = args.length == 2 ? 1 : Integer.parseInt(args[2]);
		int totalPages = MiscUtilities.getTotalPages(inputList, constants.NOTES_PER_PAGE);

		if(page > totalPages) {
			sender.sendMessage(ChatColor.RED + "There are only " + totalPages + " pages of notes for " + targetPlayer.getName());
			return true;
		}
				
		List<String> outputList = MiscUtilities.getListPage(inputList, page, constants.NOTES_PER_PAGE);
		
		sender.sendMessage(ChatColor.GOLD + "Note Page " + page + " of " + totalPages + " for " + targetPlayer.getName());
		int messageNumberPrefix = ((page*constants.NOTES_PER_PAGE)-constants.NOTES_PER_PAGE)+1;
		
		for(String output : outputList) {
			output = output.replace("<index>", Integer.toString(messageNumberPrefix));
			sender.sendMessage(output);
			++messageNumberPrefix;
		}
		return true;
	}
	
	public boolean removeSingleNote(CommandSender sender, String[] args) { 	
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note remove <player> <note #> " + ChatColor.RED + "to remove note");
			return true;
		}
		
		if(args.length > 3) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note remove <player> <note #> " + ChatColor.RED + "to remove note");
			return true;
		}	
		
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid player name");
			return true;
		}	
		if(!NumberUtilities.isInt(args[2])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid note number");
			return true;
		}
		
		//TODO: Update for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(args[1]) != null ?
				Bukkit.getServer().getPlayer(args[1]) :
				Bukkit.getServer().getOfflinePlayer(args[1]);
				
		UUID uuid = targetPlayer.getUniqueId();

							
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> noteList = userFile.getStringList("Notes");
		int number = Integer.parseInt(args[2]);
		
		if(noteList.isEmpty() || number > noteList.size() || number <= 0) {
			sender.sendMessage(ChatColor.RED + "That note does not exist");
			return true;
		}

		noteList.remove(number-1);
		userFile.set("Notes", noteList);
		sender.sendMessage(ChatColor.GREEN + "note " + args[2] + " for " + targetPlayer.getName() + " has been removed");
		if(noteList.isEmpty()) {
			noteList.clear();
			userFile.set("Notes", noteList);
			sender.sendMessage(ChatColor.GREEN + "There are no more notes for " + targetPlayer.getName());
		}
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		return true;
	}
	
	public boolean removeAllNotes(CommandSender sender, String[] args) { 
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note removeall <player> " + ChatColor.RED + "to remove all notes");
			return true;
		}
		
		if(args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note removeall <player> " + ChatColor.RED + "to remove all notes");
			return true;
		}		
		
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid player name");
			return true;
		}
	
		//TODO: Update for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(args[1]) != null ?
				Bukkit.getServer().getPlayer(args[1]) :
				Bukkit.getServer().getOfflinePlayer(args[1]);
				
		UUID uuid = targetPlayer.getUniqueId();

					
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> noteList = userFile.getStringList("Notes");

		noteList.clear();
		userFile.set("Notes", noteList);
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		sender.sendMessage(ChatColor.GREEN + "All notes for " + targetPlayer.getName() + " have been removed");
		return true;
	}
	
	public boolean showUsage(CommandSender sender) {
		if(sender.hasPermission("adminaid.note.add")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note add <player> <message> " + ChatColor.RED + "to add note");
		}
		if(sender.hasPermission("adminaid.note.read")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note list [page #] " + ChatColor.RED + "to show players with notes");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note read <player> [page #] " + ChatColor.RED + "to read notes");
		}
		if(sender.hasPermission("adminaid.note.read.self") && !sender.hasPermission("adminaid.note.read")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note read <player> [page #] " + ChatColor.RED + "to read notes");
		}
		if(sender.hasPermission("adminaid.note.remove")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note removeall <player> " + ChatColor.RED + "to remove all notes");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note remove <player> <note #> " + ChatColor.RED + "to remove note");
		}
		if(!sender.hasPermission("adminaid.note.add") &&
				!sender.hasPermission("adminaid.note.read") &&
				!sender.hasPermission("adminaid.note.read.self") &&
				!sender.hasPermission("adminaid.note.remove")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
		}
		return true;
	}
}
