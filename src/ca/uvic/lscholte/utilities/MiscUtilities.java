package ca.uvic.lscholte.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ca.uvic.lscholte.AdminAid;

public final class MiscUtilities {
	
	private MiscUtilities() { }
	
	/**
	 * 
	 * @param name
	 * @return An OfflinePlayer whose last-known name matches the
	 * name being searched for. Returns null if the player with
	 * that name has not played on the server
	 */
	public static OfflinePlayer getOfflinePlayer(String name) {
		for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if(p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Checks to see if a player is permanently banned
	 * @param player - player to check
	 * @return <tt>true</tt> if player is permanently banned,
	 * otherwise <tt>false</tt>
	 */
	public static boolean isPermaBanned(AdminAid plugin, OfflinePlayer player) {
		//TODO: Update to UUIDs
		
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getUniqueId() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		if(player.isBanned()) return true;
		else if(userFile.getBoolean("PermaBanned") == true) return true;
		else return false;
	}
	
	/**
	 * Checks to see if a player is temporarily banned
	 * @param player - player to check
	 * @return <tt>true</tt> if player is temporarily banned,
	 * otherwise <tt>false</tt>
	 */
	public static boolean isTempBanned(AdminAid plugin, OfflinePlayer player) {
		//TODO: Update to UUIDs
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getUniqueId() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		if(userFile.getBoolean("TempBanned") == true) return true;
		else return false;
	}
	
	/**
	 * Checks to see if a player is permanently muted
	 * @param player - player to check
	 * @return <tt>true</tt> if player is permanently muted,
	 * otherwise <tt>false</tt>
	 */
	public static boolean isPermaMuted(AdminAid plugin, OfflinePlayer player) {
		//TODO: Update to UUIDs
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getUniqueId() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		if(userFile.getBoolean("PermaMuted") == true) return true;
		else return false;
	}
	
	/**
	 * Checks to see if a player is temporarily muted
	 * @param player - player to check
	 * @return <tt>true</tt> if player is temporarily muted,
	 * otherwise <tt>false</tt>
	 */
	public static boolean isTempMuted(AdminAid plugin, OfflinePlayer player) {
		//TODO: Update to UUIDs
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getUniqueId() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		if(userFile.getBoolean("TempMuted") == true) return true;
		else return false;
	}
	
	
	@SuppressWarnings("serial")
	public static class LavaFilledLocationException extends Exception { }
	
	/**
	 * Looks for a safe location for
	 * player teleportation
	 * @param loc - original location
	 * @return a location that is safe for a player to
	 * teleport to
	 * @throws LavaFilledLocationException if a safe location
	 * cannot be found
	 */
	public static Location getSafeLocation(Location loc) throws LavaFilledLocationException {
		
		double y = loc.getY();
		
		/* Above world check */
		if(y > loc.getWorld().getMaxHeight()) {
			y = loc.getWorld().getHighestBlockAt(loc).getY();
			loc.setY(y);
		}
		while(y > -1) {
			/* Falling check */
			if(loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
				loc = loc.subtract(0, 1, 0);
				continue;
			}
			/* Drowning check */
			if(loc.getBlock().getType() == Material.STATIONARY_WATER || 
					loc.getBlock().getType() == Material.WATER) {
				loc = loc.add(0, 1, 0);
				continue;
			}
			if(loc.getBlock().getType().isSolid()) {
				loc = loc.add(0, 1, 0);
				continue;
			}
			break;
		}
		/* Lava check */
		if(loc.getBlock().getType() == Material.STATIONARY_LAVA || 
				loc.getBlock().getType() == Material.LAVA ||
				loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.STATIONARY_LAVA ||
				loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.LAVA) {
			throw new LavaFilledLocationException();
		}
		
		return loc;
	}
	
	public static List<String> getListPage(List<String> list, int page, int elementsPerPage) {
		int totalPages = getTotalPages(list, elementsPerPage);
		
		if(page <= 0 || page > totalPages || list.isEmpty()) return null;
		
		List<String> listPage = new ArrayList<String>();
		
		for(int i = (page*elementsPerPage)-elementsPerPage; i < (page*elementsPerPage); ++i) {
			if(i < list.size()) {
				listPage.add(list.get(i));
			}
		}
		return listPage;
	}
	
	public static int getTotalPages(List<String> list, int elementsPerPage) {
		return (int) Math.ceil((double) list.size()/elementsPerPage);
	}
	
	/**
	 * Adds the specified string to every staff member's mailbox
	 * if feature is enabled in the config. Otherwise, nothing will happen.
	 * @param message - the message to be sent to all staff members' mailboxes
	 */	
	public static void addStringStaffList(AdminAid plugin, String message) {
		if(plugin.getConfig().getBoolean("DisableCommand.Mail") == true) return;
		if(plugin.getConfig().getBoolean("NotifyStaffNewNote") == false) return;
		
		File dir = new File(plugin.getDataFolder() + "/userdata/");
		File[] children = dir.listFiles();
		
		for(File f : children) {			
			//File childFile = new File(plugin.getDataFolder() + "/userdata/" + f.getName());
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(f);
			List<String> newMail = userFile.getStringList("NewMail");
			
			if(userFile.getBoolean("StaffMember") == false) continue;
			
			newMail.add(message);
			userFile.set("NewMail", newMail);
			FileUtilities.saveYamlFile(userFile, f);
			
			//TODO: Update for UUIDs
			//Also keep in mind I have a method in FileUtilities
			//for removing the extension from a filename
			Player player = Bukkit.getServer().getPlayer(UUID.fromString(FileUtilities.removeFileExtension(f.getName())));
			if(player != null) {
				player.sendMessage(ChatColor.GREEN + "You have new mail!");
			}
		}
	}
}
