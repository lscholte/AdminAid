package ca.uvic.lscholte;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ca.uvic.lscholte.Updater.VersionCheckException;

public class LoginRunnables {
	
	public static class MailRunnable implements Runnable {

		private AdminAid plugin;
		private Player player;
		
		public MailRunnable(AdminAid instance, Player p) {
			plugin = instance;
			player = p;
		}
		
		@Override
		public void run() {
			
			if(plugin.getConfig().getBoolean("DisableCommand.Mail") == true) return;
			if(!player.hasPermission("adminaid.mail.read")) return;
			
			File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
			List<String> mailListNew = userFile.getStringList("NewMail");
			List<String> mailListRead = userFile.getStringList("ReadMail");
						
			if(!mailListNew.isEmpty()) {
				player.sendMessage(ChatColor.GREEN + "You have unread mail in your mailbox");
				player.sendMessage(ChatColor.GREEN + "Use " + ChatColor.WHITE + "/mail read " + ChatColor.GREEN + "to read your mail");
				return;
			}
			
			if(plugin.getConfig().getBoolean("AlwaysNotifyMailboxMessage") == false) return;
			
			if(!mailListRead.isEmpty()) {
				player.sendMessage(ChatColor.GREEN + "You have mail in your mailbox");
				player.sendMessage(ChatColor.GREEN + "Use " + ChatColor.WHITE + "/mail read " + ChatColor.GREEN + "to read your mail");
			}
		}	
	}
	
	public static class LoginMessagesRunnable implements Runnable {
		
		private AdminAid plugin;
		private Player player;
		
		public LoginMessagesRunnable(AdminAid instance, Player p) {
			plugin = instance;
			player = p;
		}
		
		@Override
		public void run() {
			for(String line : new ConfigValues(plugin).getLoginMessages(player)) {
				player.sendMessage(line);
			}
		}
	}
	
	public static class UpdaterRunnable implements Runnable {
		
		private AdminAid plugin;
		private Player player;
		
		public UpdaterRunnable(AdminAid instance, Player p) {
			plugin = instance;
			player = p;
		}
		
		@Override
		public void run() {
			if(!player.isOp()) return;
			if(plugin.getConfig().getBoolean("EnableVersionChecker") == false) return;
			Updater updater = new Updater(plugin);
			try {
				if(!updater.isLatest()) {
					player.sendMessage(ChatColor.RED + "There is a newer version of AdminAid available");
				}
			}
			catch(VersionCheckException e) { }
		}		
	}
}
