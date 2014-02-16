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

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.ConfigValues;
import ca.uvic.lscholte.MiscUtilities;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.NumberUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class MailCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private MiscUtilities misc;
	private ConfigValues config;
	
	public MailCommand(AdminAid instance) {
		plugin = instance;
		plugin.getCommand("mail").setExecutor(this);
		if(plugin.getConfig().getBoolean("DisableCommand.Mail") == true) {
			PluginCommand mail = plugin.getCommand("mail");
			CommandUtilities.unregisterBukkitCommand(mail);
		}
	}
			
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
		
		misc = new MiscUtilities(plugin);

		if(args.length == 0) showUsage(sender);
		else {
			if(args[0].equalsIgnoreCase("read")) {
				if(sender.hasPermission("adminaid.mail.read")) readMail(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("remove")) {
				if(sender.hasPermission("adminaid.mail.remove")) removeSingleMail(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("removeall")) {
				if(sender.hasPermission("adminaid.mail.remove")) removeAllMail(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("send")) {
				if(sender.hasPermission("adminaid.mail.send")) sendMail(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("sendall")) {
				if(sender.hasPermission("adminaid.mail.sendall")) sendAllMail(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else if(args[0].equalsIgnoreCase("sendstaff")) {
				if(sender.hasPermission("adminaid.mail.sendstaff")) sendStaffMail(sender, args);
				else sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			}
			else showUsage(sender);
		}
		return true;
	}
	
	public boolean readMail(CommandSender sender, String[] args) {
		
		config = new ConfigValues(plugin);
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "The console does not have a mailbox");
			return true;
		}
		if(args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail read [page #] " + ChatColor.RED + "to read mail");
			return true;
		}
		
		File file = new File(plugin.getDataFolder() + "/userdata/" + sender.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> mailListNew = userFile.getStringList("NewMail");
		List<String> mailListRead = userFile.getStringList("ReadMail");
		
		if(!mailListNew.isEmpty()) {
			sender.sendMessage(ChatColor.GOLD + "All Unread Mail for " + sender.getName());
			for(int i = 0; i < mailListNew.size(); ++i) {
				int noteNumberPrefix = i+1;
				sender.sendMessage(mailListNew.get(i).replace("<index>", Integer.toString(noteNumberPrefix)));
			}
			sender.sendMessage(ChatColor.GREEN + "Reread your mail if you would like to see all of your mail");
			
			mailListRead.addAll(mailListNew);
			mailListNew.clear();
			userFile.set("NewMail", mailListNew);
			userFile.set("ReadMail", mailListRead);
			FileUtilities.saveYamlFile(userFile, file);
			return true;
		}
		double configNumber = config.getMailPerPage();
		List<String> outputList;
		int totalPages = misc.getTotalPages(mailListRead, configNumber);
		int page;
		try{
			if(args.length == 1) {
				outputList = misc.getListPage(mailListRead, "1", configNumber);
				page = 1;
			}
			else {
				outputList = misc.getListPage(mailListRead, args[1], configNumber);
				page = Integer.parseInt(args[1]);
			}
			
			sender.sendMessage(ChatColor.GOLD + "Mail Page " + page + " of " + totalPages + " for " + sender.getName());
			int messageNumberPrefix = (int) ((page*configNumber)-configNumber)+1;
			for(String output : outputList) {
				output = output.replace("<index>", Integer.toString(messageNumberPrefix));
				sender.sendMessage(output);
				messageNumberPrefix++;
			}
		}
		catch(IllegalArgumentException e) { //ie args[1] is not a natural Number (1, 2, 3 etc)
			sender.sendMessage(ChatColor.RED + "That is an invalid page number");
		}
		catch(IllegalStateException e) { //ie the inputList is empty
			sender.sendMessage(ChatColor.RED + "You don't have any mail");
		}
		catch(IndexOutOfBoundsException e) { //ie args[1] is higher than the amount of pages
			sender.sendMessage(ChatColor.RED + "You only have " + totalPages + " pages of mail");
		}
		return true;
	}
	
	public boolean removeSingleMail(CommandSender sender, String[] args) {	
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "The console does not have a mailbox");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail remove <note #> " + ChatColor.RED + "to remove mail message");
			return true;
		}
		if(args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail remove <message #> " + ChatColor.RED + "to remove mail message");
			return true;
		}
		
		File file = new File(plugin.getDataFolder() + "/userdata/" + sender.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> mailListNew = userFile.getStringList("NewMail");
		List<String> mailListRead = userFile.getStringList("ReadMail");
		
		if(!mailListNew.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "You have unread mail! Read your mail first.");
			return true;
		}	
		if(!NumberUtilities.isInt(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid message number");
			return true;
		}
		
		int number = Integer.parseInt(args[1]);
		
		if(!file.exists() || mailListRead.isEmpty() || 
				number > mailListRead.size() || number <= 0) {
			sender.sendMessage(ChatColor.RED + "That message does not exist");
			return true;
		}

		mailListRead.remove(number-1);
		userFile.set("ReadMail", mailListRead);
		FileUtilities.saveYamlFile(userFile, file);
		if(mailListRead.isEmpty()) {
			sender.sendMessage(ChatColor.GREEN + "Message " + args[1] + " has been removed from your mailbox");
			sender.sendMessage(ChatColor.GREEN + "Your mailbox is now empty");
			return true;
		}
		sender.sendMessage(ChatColor.GREEN + "Message " + args[1] + " has been removed from your mail");
		return true;
	}
	
	public boolean removeAllMail(CommandSender sender, String[] args) {	
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "The console does not have a mailbox");
			return true;
		}
		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail removeall " + ChatColor.RED + "to remove all mail");
			return true;
		}
		
		File file = new File(plugin.getDataFolder() + "/userdata/" + sender.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		List<String> mailListNew = userFile.getStringList("NewMail");
		List<String> mailListRead = userFile.getStringList("ReadMail");
		
		if(!mailListNew.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "You have unread mail! Read your mail first.");
			return true;
		}
		mailListRead.clear();
		userFile.set("ReadMail", mailListRead);
		FileUtilities.saveYamlFile(userFile, file);
		sender.sendMessage(ChatColor.GREEN + "All mail has been removed from your mailbox");
		return true;
	}
	
	public boolean sendMail(CommandSender sender, String[] args) {
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail send <playername> <message> " + ChatColor.RED + "to send message to player");
			return true;
		}
		
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
				
		OfflinePlayer targetPlayer;
		if(Bukkit.getServer().getPlayer(args[1]) != null) targetPlayer = Bukkit.getServer().getPlayer(args[1]);
		else targetPlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
	
		String prefix = new ConfigValues(plugin).getPrefix(sender);
		String message = StringUtilities.buildString(args, 2);
		
		File childFile = new File(plugin.getDataFolder() + "/userdata/" + targetPlayer.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(childFile);
		List<String> mailListNew = userFile.getStringList("NewMail");
		
		if(!childFile.exists()) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " has not played on this server before");
			return true;
		}
		mailListNew.add(prefix + message);
		userFile.set("NewMail", mailListNew);
		FileUtilities.saveYamlFile(userFile, childFile);
		sender.sendMessage(ChatColor.GREEN + "Mail sent to " + targetPlayer.getName());
		if(Bukkit.getServer().getPlayer(args[1]) != null) {
			Bukkit.getServer().getPlayer(args[1]).sendMessage(ChatColor.GREEN + "You have new mail!");
		}
		return true;
	}
	
	public boolean sendAllMail(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail sendall <message> " + ChatColor.RED + "to send message to all players");
			return true;
		}
				
		File dir = new File(plugin.getDataFolder() + "/userdata/");
		File[] children = dir.listFiles();
		
		String prefix = new ConfigValues(plugin).getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		for(int i = 0; i < children.length; ++i) {
			File childFile = new File(plugin.getDataFolder() + "/userdata/" + children[i].getName());
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(childFile);
			List<String> mailListNew = userFile.getStringList("NewMail");
			mailListNew.add(prefix + message);
			userFile.set("NewMail", mailListNew);
			FileUtilities.saveYamlFile(userFile, childFile);	
		}
		sender.sendMessage(ChatColor.GREEN + "Mail sent to all players");
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(ChatColor.GREEN + "You have new mail!");
		}
		return true;
	}
	
	public boolean sendStaffMail(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail sendstaff <message> " + ChatColor.RED + "to send message to all staff");
			return true;
		}
				
		File dir = new File(plugin.getDataFolder() + "/userdata/");
		File[] children = dir.listFiles();

		String prefix = new ConfigValues(plugin).getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		for(int i = 0; i < children.length; i++) {
			File childFile = new File(plugin.getDataFolder() + "/userdata/" + children[i].getName());
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(childFile);
			List<String> mailListNew = userFile.getStringList("NewMail");
			if(userFile.getBoolean("StaffMember") == true) {
				mailListNew.add(prefix + message);
				userFile.set("NewMail", mailListNew);
				FileUtilities.saveYamlFile(userFile, childFile);	
			}
		}
		sender.sendMessage(ChatColor.GREEN + "Mail sent to all staff");
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			File childFile = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase());
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(childFile);
			if(userFile.getBoolean("StaffMember") == true) {
				player.sendMessage(ChatColor.GREEN + "You have new mail!");
			}
		}
		return true;
	}
	
	public void showUsage(CommandSender sender) {
		if(sender.hasPermission("adminaid.mail.send")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail send <playername> <message> " + ChatColor.RED + "to send mail");
		}
		if(sender.hasPermission("adminaid.mail.sendall")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail sendall <message> " + ChatColor.RED + "to send mail to all players");
		}
		if(sender.hasPermission("adminaid.mail.sendstaff")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail sendstaff <message> " + ChatColor.RED + "to send mail to staff members");
		}
		if(sender.hasPermission("adminaid.mail.read")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail read " + ChatColor.RED + "to read mail");
		}
		if(sender.hasPermission("adminaid.mail.remove")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail removeall " + ChatColor.RED + "to remove all mail");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail remove <note #> " + ChatColor.RED + "to remove mail message");
		}
		if (!sender.hasPermission("adminaid.mail.send") &&
				!sender.hasPermission("adminaid.mail.sendall") &&
				!sender.hasPermission("adminaid.mail.sendstaff") &&
				!sender.hasPermission("adminaid.mail.read") &&
				!sender.hasPermission("adminaid.mail.remove")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
		}
	}
}
