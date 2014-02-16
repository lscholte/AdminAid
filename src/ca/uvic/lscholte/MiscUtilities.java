package ca.uvic.lscholte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ca.uvic.lscholte.utilities.FileUtilities;
import ca.uvic.lscholte.utilities.NumberUtilities;

public class MiscUtilities {
	
	private AdminAid plugin;
		
	public MiscUtilities(AdminAid instance) {
		plugin = instance;
	}
	
	/**
	 * Checks to see if a player is permanently banned
	 * @param player - player to check
	 * @return <tt>true</tt> if player is permanently banned,
	 * otherwise <tt>false</tt>
	 */
	
	public boolean isPermaBanned(OfflinePlayer player) {
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
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
	
	public boolean isTempBanned(OfflinePlayer player) {
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
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
	
	public boolean isPermaMuted(OfflinePlayer player) {
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
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
	
	public boolean isTempMuted(OfflinePlayer player) {
		File file = new File(plugin.getDataFolder() + "/userdata/" + player.getName().toLowerCase() + ".yml");
		YamlConfiguration userFile = YamlConfiguration.loadConfiguration(file);
		if(userFile.getBoolean("TempMuted") == true) return true;
		else return false;
	}
	
	
	@SuppressWarnings("serial")
	public class LavaFilledLocationException extends Exception {}
	
	/**
	 * Looks for a safe location for
	 * player teleportation
	 * @param loc - original location
	 * @return a location that is safe for a player to
	 * teleport to
	 * @throws LavaFilledLocationException if a safe location
	 * cannot be found
	 */
	public Location getSafeLocation(Location loc) throws LavaFilledLocationException {
		
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
	
	/**
	 * Separates a List of Strings into pages and returns the specified page as a new List of Strings
	 * 
	 * @param list - List of Strings to make into pages
	 * @param pageString - the page number (as a String) of the new list that you would like returned
	 * @param stringsPerPage - the maximum number of strings contained in the returned list
	 * @return the desired page from the List of Strings
	 * @throws IllegalArgumentException if pageString cannot be converted into an <tt>int</tt>
	 * @throws IllegalStateException if list is empty
	 * @throws IndexOutOfBoundsException if pageString results in a page higher than the total number of pages
	 */
	
	public List<String> getListPage(List<String> list, String pageString, double stringsPerPage)
			throws IllegalArgumentException, IllegalStateException, IndexOutOfBoundsException {
		
		int page;
		if(NumberUtilities.isInt(pageString) && Integer.parseInt(pageString) > 0) {
			page = Integer.parseInt(pageString);
		}
		else {
			page = 0;
		}	
		
		double listSize = list.size();
		int totalPages = (int) Math.ceil(listSize/stringsPerPage);
		
		if(page == 0) {
			throw new IllegalArgumentException();
		}
		else if(listSize == 0) {
			throw new IllegalStateException();
		}
		else if(page > totalPages) {
			throw new IndexOutOfBoundsException();
		}
		
		List<String> listPage = new ArrayList<String>();
		
		for(int i = (int) ((page*stringsPerPage)-stringsPerPage); i < (page*stringsPerPage); ++i) {
			if(i < listSize) {
				listPage.add(list.get(i));
			}
		}
		return listPage;
	}
	
	/**
	 * Will return the number of pages that a list could be made into
	 * 
	 * @param list - List of Strings that could be made into pages
	 * @param stringsPerPage - the maximum number of strings contained in the returned list
	 * @return the total number of pages if the list was separated into pages
	 */
	
	public int getTotalPages(List<String> list, double stringsPerPage) {
		double listSize = list.size();
		int totalPages = (int) Math.ceil(listSize/stringsPerPage);
		return totalPages;
	}
	
	/**
	 * Adds the specified string to every staff member's mailbox
	 * if feature is enabled in the config. Otherwise, nothing will happen.
	 * @param message - the message to be sent to all staff members' mailboxes
	 */
		
	public void addStringStaffList(String message) {
		if(plugin.getConfig().getBoolean("DisableCommand.Mail") == true) return;
		if(plugin.getConfig().getBoolean("NotifyStaffNewNote") == false) return;
		
		File dir = new File(plugin.getDataFolder() + "/userdata/");
		File[] children = dir.listFiles();
		if(children == null) return;
		
		for(int i = 0; i < children.length; ++i) {
			
			File childFile = new File(plugin.getDataFolder() + "/userdata/" + children[i].getName());
			YamlConfiguration userFile = YamlConfiguration.loadConfiguration(childFile);
			List<String> newMail = userFile.getStringList("NewMail");
			
			if(userFile.getBoolean("StaffMember") == false) continue;
			
			newMail.add(message);
			userFile.set("NewMail", newMail);
			FileUtilities.saveYamlFile(userFile, childFile);
			
			String fileNameWithExt = childFile.getName();
			int pos = fileNameWithExt.lastIndexOf(".");
			
			if(pos != -1) {
				String playerName = fileNameWithExt.substring(0, pos);
				Player player = Bukkit.getServer().getPlayer(playerName);
				if(player != null) {
					player.sendMessage(ChatColor.GREEN + "You have new mail!");
				}	
			}
		}
	}
}
