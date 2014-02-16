package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigValues;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.NumberUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class NoteCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	private ConfigValues config;
	
	public NoteCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("note").setExecutor(this);
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { 
		
		misc = new MiscUtilities(plugin);

		if(args.length == 0) showUsage(sender);
		else {
			if(args[0].equalsIgnoreCase("add")) {
				if(sender.hasPermission("adminaid.note.add")) addNote(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("list")) {
				if(sender.hasPermission("adminaid.note.read")) listPlayers(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("read")) {
				if(sender.hasPermission("adminaid.note.read") ||
						sender.hasPermission("adminaid.note.read.self")) readNotes(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("remove")) {
				if(sender.hasPermission("adminaid.note.remove")) removeSingleNote(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("removeall")) {
				if(sender.hasPermission("adminaid.note.remove")) removeAllNotes(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else showUsage(sender);
		}
		return true;
	}
	
	public boolean addNote(CommandSender sender, String[] args) { 
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note add <playername> <message> " + ChatColor.RED + "to add note");
			return true;
		}
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
		
		final OfflinePlayer targetPlayer;
		if(Bukkit.getServer().getPlayer(args[1]) != null) targetPlayer = Bukkit.getServer().getPlayer(args[1]);
		else targetPlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
		
		StringBuilder strBuilder = new StringBuilder();			
		String prefix = new ConfigValues(plugin).getPrefix(sender);
		
		for(int i = 2; i < args.length; ++i) {
			strBuilder.append(args[i] + " ");
		}
		String message = strBuilder.toString().trim();
		
		File file = new File(plugin.getDataFolder() + "/userdata/" + targetPlayer.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> notelist = userFile.getStringList("Notes");
		
		notelist.add(prefix + message);
		misc.addStringStaffList(prefix + "Note about " + targetPlayer.getName() + ": " + message);
		userFile.set("Notes", notelist);
		FileUtilities.saveYamlFile(userFile, file);
		sender.sendMessage(ChatColor.GREEN + "note added for " + targetPlayer.getName());
		return true;
	}
	
	public boolean listPlayers(CommandSender sender, String[] args) {
		
		config = new ConfigValues(plugin);
		
		if(args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note list [page #] " + ChatColor.RED + "to show players with notes");
			return true;
		}
		
		File dir = new File(plugin.getDataFolder() + "/userdata/");
		File[] children = dir.listFiles();
		List<String> inputList = new ArrayList<String>();
		
		if(children != null) {
			for(File f : children) {
				File childFile = new File(plugin.getDataFolder() + "/userdata/" + f.getName());
				YamlConfiguration childUserFile = YamlConfiguration.loadConfiguration(childFile);
				List<String> noteList = childUserFile.getStringList("Notes");
				String fileListWithExt = childFile.getName();
				int pos = fileListWithExt.lastIndexOf(".");
				if(pos != -1) {
					String fileList = fileListWithExt.substring(0, pos);
					if(childUserFile.contains("Notes") && noteList.size() != 0) {
						inputList.add(fileList);
					}
				}
			}
			double configNumber = config.getNotesPerPage();
			List<String> outputList;
			int totalPages = misc.getTotalPages(inputList, configNumber);
			int page;
			try{
				if(args.length == 1) {
					outputList = misc.getListPage(inputList, "1", configNumber);
					page = 1;
				}
				else {
					outputList = misc.getListPage(inputList, args[1], configNumber);
					page = Integer.parseInt(args[1]);
				}
				
				sender.sendMessage(ChatColor.GOLD + "Players With Notes Page " + page + " of " + totalPages);
				for(String output : outputList) {
					sender.sendMessage(output);
				}
			}
			catch(IllegalArgumentException e) { //ie args[1] is not a natural Number (1, 2, 3 etc)
				sender.sendMessage(ChatColor.RED + "That is an invalid page number");
			}
			catch(IllegalStateException e) { //ie the inputList is empty
				sender.sendMessage(ChatColor.RED + "There are no players with notes");
			}
			catch(IndexOutOfBoundsException e) { //ie args[1] is higher than the amount of pages
				sender.sendMessage(ChatColor.RED + "There are only " + totalPages + " pages of players with notes");
			}
		}
		return true;
	}

	public boolean readNotes(CommandSender sender, String[] args) {
		
		config = new ConfigValues(plugin);
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note read <playername> [page #] " + ChatColor.RED + "to read notes");
			return true;
		}
		if(args.length > 3) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note read <playername> [page #] " + ChatColor.RED + "to read notes");
			return true;
		}			
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
	
		OfflinePlayer targetPlayer;
		if(Bukkit.getServer().getPlayer(args[1]) != null) targetPlayer = Bukkit.getServer().getPlayer(args[1]);
		else targetPlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
		
		if(!targetPlayer.getName().equalsIgnoreCase(sender.getName()) && !sender.hasPermission("adminaid.note.read")) {
			sender.sendMessage(ChatColor.RED + "You only have permission to read notes about yourself");
			return true;
		}
			
		File file = new File(plugin.getDataFolder() + "/userdata/" + targetPlayer.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		
		List<String> inputList = userFile.getStringList("Notes");
		double configNumber = config.getNotesPerPage();
		List<String> outputList;
		int totalPages = misc.getTotalPages(inputList, configNumber);
		int page;
		try{
			if(args.length == 2) {
				outputList = misc.getListPage(inputList, "1", configNumber);
				page = 1;
			}
			else {
				outputList = misc.getListPage(inputList, args[2], configNumber);
				page = Integer.parseInt(args[2]);
			}
			
			sender.sendMessage(ChatColor.GOLD + "Note Page " + page + " of " + totalPages + " for " + targetPlayer.getName());
			int messageNumberPrefix = (int) ((page*configNumber)-configNumber)+1;
			for(String output : outputList) {
				output = output.replace("<index>", Integer.toString(messageNumberPrefix));
				sender.sendMessage(output);
				messageNumberPrefix++;
			}
		}
		catch(IllegalArgumentException e) { //ie args[2] is not a natural Number (1, 2, 3 etc)
			sender.sendMessage(ChatColor.RED + "That is an invalid page number");
		}
		catch(IllegalStateException e) { //ie the inputList is empty
			sender.sendMessage(ChatColor.RED + "There are no notes for " + targetPlayer.getName());
		}
		catch(IndexOutOfBoundsException e) { //ie args[2] is higher than the amount of pages
			sender.sendMessage(ChatColor.RED + "There are only " + totalPages + " pages of notes");
		}
		return true;
	}
	
	public boolean removeSingleNote(CommandSender sender, String[] args) { 	
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note remove <playername> <note #> " + ChatColor.RED + "to remove note");
			return true;
		}
		if(args.length > 3) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note remove <playername> <note #> " + ChatColor.RED + "to remove note");
			return true;
		}	
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}	
		if(!NumberUtilities.isInt(args[2])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid note number");
			return true;
		}
		
		OfflinePlayer targetPlayer;
		if(Bukkit.getServer().getPlayer(args[1]) != null) targetPlayer = Bukkit.getServer().getPlayer(args[1]);
		else targetPlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
							
		File file = new File(plugin.getDataFolder() + "/userdata/" + targetPlayer.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> noteList = userFile.getStringList("Notes");
		int number = Integer.parseInt(args[2]);
		
		if(!file.exists() || noteList.isEmpty() || 
				number > noteList.size() || number <= 0) {
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
		FileUtilities.saveYamlFile(userFile, file);
		return true;
	}
	
	public boolean removeAllNotes(CommandSender sender, String[] args) { 
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note removeall <playername> " + ChatColor.RED + "to remove all notes");
			return true;
		}
		if(args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note removeall <playername> " + ChatColor.RED + "to remove all notes");
			return true;
		}			
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
	
		OfflinePlayer targetPlayer;
		if(Bukkit.getServer().getPlayer(args[1]) != null) targetPlayer = Bukkit.getServer().getPlayer(args[1]);
		else targetPlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
					
		File file = new File(plugin.getDataFolder() + "/userdata/" + targetPlayer.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> noteList = userFile.getStringList("Notes");

		noteList.clear();
		userFile.set("Notes", noteList);
		FileUtilities.saveYamlFile(userFile, file);
		sender.sendMessage(ChatColor.GREEN + "All notes for " + targetPlayer.getName() + " have been removed");
		return true;
	}
	
	public void showUsage(CommandSender sender) {
		if(sender.hasPermission("adminaid.note.add")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note add <playername> <message> " + ChatColor.RED + "to add note");
		}
		if(sender.hasPermission("adminaid.note.read")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note list [page #] " + ChatColor.RED + "to show players with notes");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note read <playername> [page #] " + ChatColor.RED + "to read notes");
		}
		if(sender.hasPermission("adminaid.note.read.self") && !sender.hasPermission("adminaid.note.read")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note read <playername> [page #] " + ChatColor.RED + "to read notes");
		}
		if(sender.hasPermission("adminaid.note.remove")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note removeall <playername> " + ChatColor.RED + "to remove all notes");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/note remove <playername> <note #> " + ChatColor.RED + "to remove note");
		}
		if(!sender.hasPermission("adminaid.note.add") &&
				!sender.hasPermission("adminaid.note.read") &&
				!sender.hasPermission("adminaid.note.read.self") &&
				!sender.hasPermission("adminaid.note.remove")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
		}
	}
}
