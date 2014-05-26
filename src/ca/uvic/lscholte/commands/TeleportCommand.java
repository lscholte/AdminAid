package ca.uvic.lscholte.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import ca.uvic.lscholte.AdminAid;
import ca.uvic.lscholte.utilities.CommandUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities;
import ca.uvic.lscholte.utilities.NumberUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;
import ca.uvic.lscholte.utilities.MiscUtilities.LavaFilledLocationException;

public class TeleportCommand implements CommandExecutor {
		
	public TeleportCommand(AdminAid plugin) {
		if(plugin.getConfig().getBoolean("DisableCommand.Teleport") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "teleport");	
		}
		else {
			PluginCommand com = plugin.getCommand("teleport");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can teleport");
			return true;
		}
		
		if(!sender.hasPermission("adminaid.teleport") &&
				!sender.hasPermission("adminaid.teleport.others") &&
				!sender.hasPermission("adminaid.teleport.coordinates")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		
		if(args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			if(sender.hasPermission("adminaid.teleport")) {
				sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tp  <player> " + ChatColor.RED + "to teleport yourself to player");
			}
			if(sender.hasPermission("adminaid.teleport.others")) {
				sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tp  <player1> <player2> " + ChatColor.RED + "to teleport player1 to player2");
			}
			if(sender.hasPermission("adminaid.teleport.coordinates")) {
				sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tp  <x> <y> <z> [world] " + ChatColor.RED + "to teleport yourself to coordinates");
			}
			return true;		
		}
		
		if(args.length > 4) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			if(sender.hasPermission("adminaid.teleport")) {
				sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tp  <player> " + ChatColor.RED + "to teleport yourself to player");
			}
			if(sender.hasPermission("adminaid.teleport.others")) {
				sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tp  <player1> <player2> " + ChatColor.RED + "to teleport player1 to player2");
			}
			if(sender.hasPermission("adminaid.teleport.coordinates")) {
				sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/tp  <x> <y> <z> [world]" + ChatColor.RED + "to teleport yourself to coordinates");
			}
			return true;		
		}
		
		Player player = (Player) sender;
		
		if(args.length == 1) {
			if(!sender.hasPermission("adminaid.teleport")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
				return true;
			}
			
			//TODO: Update for UUIDs
			Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
			
			if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
				sender.sendMessage(ChatColor.RED + "That is an invalid player name");
				return true;
			}
			if(targetPlayer == null) {
				sender.sendMessage(ChatColor.RED + args[0] + " is not online");
				return true;
			}
			
			Location loc = targetPlayer.getLocation();
			try {
				player.teleport(MiscUtilities.getSafeLocation(loc));
			}
			catch(LavaFilledLocationException e) {
				sender.sendMessage(ChatColor.RED + "Risk of burning to death at target location. Teleport aborted");
				return true;
			}

			sender.sendMessage(ChatColor.GREEN + "You were teleported to " + targetPlayer.getName());
			return true;
		}
		else if(args.length == 2) {
			if(!sender.hasPermission("adminaid.teleport.others")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
				return true;
			}
			
			//TODO: Update for UUIDs
			Player targetPlayer1 = Bukkit.getServer().getPlayer(args[0]);
			Player targetPlayer2 = Bukkit.getServer().getPlayer(args[1]);
			
			if(StringUtilities.nameContainsInvalidCharacter(args[0]) || 
					StringUtilities.nameContainsInvalidCharacter(args[1])) {
				sender.sendMessage(ChatColor.RED + "1 or more playernames are invalid");
				return true;
			}
			
			if(targetPlayer1 == null){
				sender.sendMessage(ChatColor.RED + args[0] + " is not online");
				return true;
			}
			if(targetPlayer2 == null){
				sender.sendMessage(ChatColor.RED + args[1] + " is not online");
				return true;
			}
			
			Location loc = targetPlayer2.getLocation();
			
			try {
				targetPlayer1.teleport(MiscUtilities.getSafeLocation(loc));
			}
			catch(LavaFilledLocationException e) {
				sender.sendMessage(ChatColor.RED + "Risk of burning to death at target location. Teleport aborted");
				return true;
			}
			
			sender.sendMessage(ChatColor.GREEN + targetPlayer1.getName() + "was teleported to " + targetPlayer2.getName());
			targetPlayer1.sendMessage(ChatColor.GREEN + "You were teleported to " + targetPlayer2.getName());
			return true;
		}
		else if(args.length == 3) {
			if(!sender.hasPermission("adminaid.teleport.coordinates")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
				return true;
			}
			
			if(!NumberUtilities.isDouble(args[0]) || 
					!NumberUtilities.isDouble(args[1]) || 
					!NumberUtilities.isDouble(args[2])) {
				sender.sendMessage(ChatColor.RED + "1 or more coordinates are invalid");
				return true;
			}
			
			int x = (int) Double.parseDouble(args[0]);
			int y = (int) Double.parseDouble(args[1]);
			int z = (int) Double.parseDouble(args[2]);
			World world = player.getWorld();
			Location loc = new Location(world, x, y, z);
			
			try {
				player.teleport(MiscUtilities.getSafeLocation(loc));
			}
			catch(LavaFilledLocationException e) {
				sender.sendMessage(ChatColor.RED + "Risk of burning to death at target location. Teleport aborted");
				return true;
			}
			
			sender.sendMessage(ChatColor.GREEN + "You were teleported to a set of coordinates in world " + world.getName());
			return true;
		}
		else {
			if(!sender.hasPermission("adminaid.teleport.coordinates")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
				return true;
			}
			if(!NumberUtilities.isDouble(args[0]) || 
					!NumberUtilities.isDouble(args[1]) || 
					!NumberUtilities.isDouble(args[2])) {
				sender.sendMessage(ChatColor.RED + "1 or more coordinates are invalid");
				return true;
			}
			
			World world = Bukkit.getServer().getWorld(args[3]);
			if(world == null) {
				sender.sendMessage(ChatColor.RED + "That is an invalid world");
				return true;
			}
			
			int x = (int) Double.parseDouble(args[0]);
			int y = (int) Double.parseDouble(args[1]);
			int z = (int) Double.parseDouble(args[2]);
			Location loc = new Location(world, x, y, z);
			
			try {
				player.teleport(MiscUtilities.getSafeLocation(loc));
			}
			catch(LavaFilledLocationException e) {
				sender.sendMessage(ChatColor.RED + "Risk of burning to death at target location. Teleport aborted");
				return true;
			}

			sender.sendMessage(ChatColor.GREEN + "You were teleported to a set of coordinates in world " + world.getName());
			return true;
		}
	}
}
