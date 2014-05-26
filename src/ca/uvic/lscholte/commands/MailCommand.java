package ca.uvic.lscholte.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

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
import ca.uvic.lscholte.ConfigConstants;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.NumberUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class MailCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public MailCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Mail") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "mail");	
		}
		else {
			PluginCommand com = plugin.getCommand("mail");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
			
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
		if(args.length == 0) {
			return showUsage(sender);
		}
		if(args[0].equalsIgnoreCase("read")) {
			if(sender.hasPermission("adminaid.mail.read")) {
				return readMail(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("remove")) {
			if(sender.hasPermission("adminaid.mail.remove")) {
				return removeSingleMail(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("removeall")) {
			if(sender.hasPermission("adminaid.mail.remove")) {
				return removeAllMail(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("send")) {
			if(sender.hasPermission("adminaid.mail.send")) {
				return sendMail(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("sendall")) {
			if(sender.hasPermission("adminaid.mail.sendall")) {
				return sendAllMail(sender, args);
			}
		}
		else if(args[0].equalsIgnoreCase("sendstaff")) {
			if(sender.hasPermission("adminaid.mail.sendstaff")) {
				return sendStaffMail(sender, args);
			}
		}
		else return showUsage(sender);
		sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
		return true;
	}
	
	public boolean readMail(CommandSender sender, String[] args) {		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "The console does not have a mailbox");
			return true;
		}
		
		if(args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail read [page #] " + ChatColor.RED + "to read mail");
			return true;
		}
		
		//TODO: Update for UUIDs
		UUID uuid = ((Player) sender).getUniqueId();
		
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
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
			FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
			return true;
		}
		
		if(mailListRead == null || mailListRead.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "You don't have any mail");
			return true;
		}
		
		if(args.length == 2 && (!NumberUtilities.isInt(args[1]) || Integer.parseInt(args[1]) <= 0)) {
			sender.sendMessage(ChatColor.RED + "That is an invalid page number");
			return true;
		}
		
		int page = args.length == 1 ? 1 : Integer.parseInt(args[1]);
		int totalPages = MiscUtilities.getTotalPages(mailListRead, constants.MAIL_PER_PAGE);

		if(page > totalPages) {
			sender.sendMessage(ChatColor.RED + "You only have " + totalPages + " pages of mail");
			return true;
		}
		
		List<String> outputList = MiscUtilities.getListPage(mailListRead, page, constants.MAIL_PER_PAGE);
		sender.sendMessage(ChatColor.GOLD + "Mail Page " + page + " of " + totalPages + " for " + sender.getName());
		int messageNumberPrefix = ((page*constants.MAIL_PER_PAGE)-constants.MAIL_PER_PAGE)+1;
		for(String output : outputList) {
			output = output.replace("<index>", Integer.toString(messageNumberPrefix));
			sender.sendMessage(output);
			++messageNumberPrefix;
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
		
		//TODO: Update for UUIDs
		UUID uuid = ((Player) sender).getUniqueId();
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
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
		
		if(mailListRead.isEmpty() || 
				number > mailListRead.size() || number <= 0) {
			sender.sendMessage(ChatColor.RED + "That message does not exist");
			return true;
		}

		mailListRead.remove(number-1);
		userFile.set("ReadMail", mailListRead);
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
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
		
		//TODO: Update for UUIDs
		UUID uuid = ((Player) sender).getUniqueId();
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> mailListNew = userFile.getStringList("NewMail");
		List<String> mailListRead = userFile.getStringList("ReadMail");
		
		if(!mailListNew.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "You have unread mail! Read your mail first.");
			return true;
		}
		
		mailListRead.clear();
		userFile.set("ReadMail", mailListRead);
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
		sender.sendMessage(ChatColor.GREEN + "All mail has been removed from your mailbox");
		return true;
	}
	
	public boolean sendMail(CommandSender sender, String[] args) {
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail send <player> <message> " + ChatColor.RED + "to send message to player");
			return true;
		}
		
		if(StringUtilities.nameContainsInvalidCharacter(args[1])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid playername");
			return true;
		}
				
		//TODO: Update for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(args[1]) != null ?
				Bukkit.getServer().getPlayer(args[1]) :
				Bukkit.getServer().getOfflinePlayer(args[1]);
				
		if(!targetPlayer.hasPlayedBefore()) {
			sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " has not played on this server before");
			return true;
		}
				
		UUID uuid = targetPlayer.getUniqueId();
	
		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 2);
		
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		List<String> mailListNew = userFile.getStringList("NewMail");
		
		
		mailListNew.add(prefix + message);
		userFile.set("NewMail", mailListNew);
		FileUtilities.saveYamlConfiguration(plugin, userFile, uuid);
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
		
		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		for(File f : children) {
			//TODO: Update for UUIDs
			//File childFile = new File(plugin.getDataFolder() + "/userdata/" + f.getName());
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(f);
			List<String> mailListNew = userFile.getStringList("NewMail");
			mailListNew.add(prefix + message);
			userFile.set("NewMail", mailListNew);
			FileUtilities.saveYamlFile(userFile, f);	
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

		String prefix = constants.getPrefix(sender);
		String message = StringUtilities.buildString(args, 1);
		
		for(File f : children) {
			//TODO: Update for UUIDs
			//File childFile = new File(plugin.getDataFolder() + "/userdata/" + f.getName());
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(f);
			if(userFile.getBoolean("StaffMember") == true) {
				List<String> mailListNew = userFile.getStringList("NewMail");
				mailListNew.add(prefix + message);
				userFile.set("NewMail", mailListNew);
				FileUtilities.saveYamlFile(userFile, f);	
			}
		}
		sender.sendMessage(ChatColor.GREEN + "Mail sent to all staff");
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			//TODO: Update for UUIDs
			UUID uuid = player.getUniqueId();
			YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
			if(userFile.getBoolean("StaffMember") == true) {
				player.sendMessage(ChatColor.GREEN + "You have new mail!");
			}
		}
		return true;
	}
	
	public boolean showUsage(CommandSender sender) {
		if(sender.hasPermission("adminaid.mail.send")) {
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/mail send <player> <message> " + ChatColor.RED + "to send mail");
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
		if(!sender.hasPermission("adminaid.mail.send") &&
				!sender.hasPermission("adminaid.mail.sendall") &&
				!sender.hasPermission("adminaid.mail.sendstaff") &&
				!sender.hasPermission("adminaid.mail.read") &&
				!sender.hasPermission("adminaid.mail.remove")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
		}
		return true;
	}
}
