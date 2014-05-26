package ca.uvic.lscholte.commands;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import ca.uvic.lscholte.utilities.OnTimeUtilities;
import ca.uvic.lscholte.utilities.StringUtilities;

public class PlayerinfoCommand implements CommandExecutor {
	
	private AdminAid plugin;
	private ConfigConstants constants;
	
	public PlayerinfoCommand(AdminAid plugin) {
		this.plugin = plugin;
		constants = ConfigConstants.getInstance(plugin);
		if(plugin.getConfig().getBoolean("DisableCommand.Playerinfo") == false) { 	
			CommandUtilities.giveCommandPriority(plugin, this, "playerinfo");	
		}
		else {
			PluginCommand com = plugin.getCommand("playerinfo");
			CommandUtilities.unregisterBukkitCommand(plugin, com);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if(!sender.hasPermission("adminaid.playerinfo")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
			return true;
		}
		if(args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Too few arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/playerinfo <player> " + ChatColor.RED + "to view info about player");
			return true;
		}
		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Use " + ChatColor.WHITE + "/playerinfo <player> " + ChatColor.RED + "to view info about player");
			return true;
		}
		if(StringUtilities.nameContainsInvalidCharacter(args[0])) {
			sender.sendMessage(ChatColor.RED + "That is an invalid player name");
			return true;
		}
		
		//TODO: Update for UUIDs
		OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(args[0]) != null ?
				Bukkit.getServer().getPlayer(args[0]) :
				Bukkit.getServer().getOfflinePlayer(args[0]);
				
		UUID uuid = targetPlayer.getUniqueId();
								
		//TODO: Update for UUIDs
		YamlConfiguration userFile = FileUtilities.loadYamlConfiguration(plugin, uuid);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a z");
		
		if(constants.SHOW_UUID == true) {
			sender.sendMessage(ChatColor.YELLOW + "UUID: " + ChatColor.WHITE + uuid);
		}
		
		if(constants.SHOW_ONLINE_STATUS == true) {
			if(Bukkit.getServer().getPlayer(args[0]) != null) {
				sender.sendMessage(ChatColor.YELLOW + "Online: " + ChatColor.WHITE + "yes");
			}
			else {
				sender.sendMessage(ChatColor.YELLOW + "Online: " + ChatColor.WHITE + "no");
			}
		}
		
		if(constants.SHOW_LAST_LOGIN == true) {
			if(Bukkit.getServer().getPlayer(args[0]) == null) {
				String date = sdf.format(new Date(targetPlayer.getLastPlayed()));
				sender.sendMessage(ChatColor.YELLOW + "Last Seen: " + ChatColor.WHITE + date);
			}	
		}
		
		if(constants.SHOW_FIRST_LOGIN == true) {
			String date = sdf.format(new Date(targetPlayer.getFirstPlayed()));
			sender.sendMessage(ChatColor.YELLOW + "First Seen: " + ChatColor.WHITE + date);
		}
		
		if(constants.SHOW_IP_ADDRESS == true) {
			sender.sendMessage(ChatColor.YELLOW + "IP Address: " 
					+ ChatColor.WHITE + userFile.getString("IPAddress", "unknown"));
		}
		
		if(constants.SHOW_LOCATION == true) {
			if(Bukkit.getServer().getPlayer(args[0]) != null) {
				Player player = Bukkit.getServer().getPlayer(args[0]);
				Location loc = player.getLocation();
				String xCoord = Integer.toString(loc.getBlockX());
				String yCoord = Integer.toString(loc.getBlockY());
				String zCoord = Integer.toString(loc.getBlockZ());
				String world = loc.getWorld().getName();
	
				sender.sendMessage(ChatColor.YELLOW + "X: " + ChatColor.WHITE + xCoord +
						ChatColor.YELLOW + " Y: " + ChatColor.WHITE + yCoord +
						ChatColor.YELLOW + " Z: " + ChatColor.WHITE + zCoord +
						ChatColor.YELLOW + " World: " + ChatColor.WHITE + world);
			}
			else if(userFile.get("Location") != null){
				String xCoord = Integer.toString(userFile.getInt("Location.X"));
				String yCoord = Integer.toString(userFile.getInt("Location.Y"));
				String zCoord = Integer.toString(userFile.getInt("Location.Z"));
				String world = userFile.getString("Location.World");
	
				sender.sendMessage(ChatColor.YELLOW + "X: " + ChatColor.WHITE + xCoord +
						ChatColor.YELLOW + " Y: " + ChatColor.WHITE + yCoord +
						ChatColor.YELLOW + " Z: " + ChatColor.WHITE + zCoord +
						ChatColor.YELLOW + " World: " + ChatColor.WHITE + world);
			}
		}
		
		if(constants.SHOW_BANNED_STATUS == true) {
			if(MiscUtilities.isPermaBanned(plugin, targetPlayer)) {
				String defaultMessage = "permanently banned from this server";
				sender.sendMessage(ChatColor.YELLOW + "Banned: " 
						+ ChatColor.WHITE + "is " + userFile.getString("PermaBanReason", defaultMessage));
			}
			else if(MiscUtilities.isTempBanned(plugin, targetPlayer)) {
				String defaultMessage = "temporarily banned from this server";
				sender.sendMessage(ChatColor.YELLOW + "Banned: " 
						+ ChatColor.WHITE + "is " + userFile.getString("TempBanReason", defaultMessage));
			}
		}
		
		if(constants.SHOW_MUTED_STATUS == true) {
			if(MiscUtilities.isPermaMuted(plugin, targetPlayer)) {
				String defaultMessage = "permanently muted";
				sender.sendMessage(ChatColor.YELLOW + "Muted: " 
						+ ChatColor.WHITE + "is " + userFile.getString("PermaMuteReason", defaultMessage));
			}
			else if(MiscUtilities.isTempMuted(plugin, targetPlayer)) {
				String defaultMessage = "temporarily muted";
				sender.sendMessage(ChatColor.YELLOW + "Muted: " 
						+ ChatColor.WHITE + "is " + userFile.getString("TempMuteReason", defaultMessage));
			}
		}
		
		if(constants.SHOW_STAFF_MEMBER_STATUS == true) {
			if(userFile.getBoolean("StaffMember") == true) {
				sender.sendMessage(ChatColor.YELLOW + "Staff Member: " + ChatColor.WHITE + "yes");
			}
			else {
				sender.sendMessage(ChatColor.YELLOW + "Staff Member: " + ChatColor.WHITE + "no");
			}
		}
		
		if(constants.SHOW_CHAT_SPY_STATUS == true) {
			if(userFile.getBoolean("ChatSpy") == true) {
				sender.sendMessage(ChatColor.YELLOW + "ChatSpy Enabled: " + ChatColor.WHITE + "yes");
			}
			else {
				sender.sendMessage(ChatColor.YELLOW + "ChatSpy Enabled: " + ChatColor.WHITE + "no");
			}
		}
		
		if(constants.SHOW_BAN_EXEMPT_STATUS == true) {
			if(userFile.getBoolean("BanExempt") == true) {
				sender.sendMessage(ChatColor.YELLOW + "Ban Exempt: " + ChatColor.WHITE + "yes");
			}
			else {
				sender.sendMessage(ChatColor.YELLOW + "Ban Exempt: " + ChatColor.WHITE + "no");
			}
		}
		
		if(constants.SHOW_MUTE_EXEMPT_STATUS == true) {
			if(userFile.getBoolean("MuteExempt") == true) {
				sender.sendMessage(ChatColor.YELLOW + "Mute Exempt: " + ChatColor.WHITE + "yes");
			}
			else {
				sender.sendMessage(ChatColor.YELLOW + "Mute Exempt: " + ChatColor.WHITE + "no");
			}
		}
		
		if(constants.SHOW_KICK_EXEMPT_STATUS == true) {
			if(userFile.getBoolean("KickExempt") == true) {
				sender.sendMessage(ChatColor.YELLOW + "Kick Exempt: " + ChatColor.WHITE + "yes");
			}
			else {
				sender.sendMessage(ChatColor.YELLOW + "Kick Exempt: " + ChatColor.WHITE + "no");
			}
		}
		
		if(AdminAid.getOnTime() != null) {
			if(OnTimeUtilities.hasOnTimeRecord(targetPlayer.getName())) {
				if(constants.SHOW_TOTAL_PLAY_TIME == true) {
					String time = OnTimeUtilities.getTotalPlayTime(targetPlayer.getName());
					sender.sendMessage(ChatColor.YELLOW + "Total Play Time: " + ChatColor.WHITE + time);
				}
				if(constants.SHOW_MONTHLY_PLAY_TIME == true) {
					String time = OnTimeUtilities.getMonthlyPlayTime(targetPlayer.getName());
					sender.sendMessage(ChatColor.YELLOW + "Monthly Play Time: " + ChatColor.WHITE + time);
				}
				if(constants.SHOW_WEEKLY_PLAY_TIME == true) {
					String time = OnTimeUtilities.getWeeklyPlayTime(targetPlayer.getName());
					sender.sendMessage(ChatColor.YELLOW + "Weekly Play Time: " + ChatColor.WHITE + time);
				}
				if(constants.SHOW_DAILY_PLAY_TIME == true) {
					String time = OnTimeUtilities.getDailyPlayTime(targetPlayer.getName());
					sender.sendMessage(ChatColor.YELLOW + "Daily Play Time: " + ChatColor.WHITE + time);
				}
				try {
					if(constants.SHOW_TOTAL_VOTES == true) {
						String votes = OnTimeUtilities.getTotalVotes(targetPlayer.getName());
						sender.sendMessage(ChatColor.YELLOW + "Total Votes: " + ChatColor.WHITE + votes);
					}
					if(constants.SHOW_MONTHLY_VOTES == true) {
						String votes = OnTimeUtilities.getMonthlyVotes(targetPlayer.getName());
						sender.sendMessage(ChatColor.YELLOW + "Monthly Votes: " + ChatColor.WHITE + votes);
					}
					if(constants.SHOW_WEEKLY_VOTES == true) {
						String votes = OnTimeUtilities.getWeeklyVotes(targetPlayer.getName());
						sender.sendMessage(ChatColor.YELLOW + "Weekly Votes: " + ChatColor.WHITE + votes);
					}
					if(constants.SHOW_DAILY_VOTES == true) {
						String votes = OnTimeUtilities.getDailyVotes(targetPlayer.getName());
						sender.sendMessage(ChatColor.YELLOW + "Daily Votes: " + ChatColor.WHITE + votes);
					}
					if(constants.SHOW_TOTAL_REFERRALS == true) {
						String referrals = OnTimeUtilities.getTotalReferrals(targetPlayer.getName());
						sender.sendMessage(ChatColor.YELLOW + "Total Referrals: " + ChatColor.WHITE + referrals);
					}
					if(constants.SHOW_MONTHLY_REFERRALS == true) {
						String referrals = OnTimeUtilities.getMonthlyReferrals(targetPlayer.getName());
						sender.sendMessage(ChatColor.YELLOW + "Monthly Referrals: " + ChatColor.WHITE + referrals);
					}
					if(constants.SHOW_WEEKLY_REFERRALS == true) {
						String referrals = OnTimeUtilities.getWeeklyReferrals(targetPlayer.getName());
						sender.sendMessage(ChatColor.YELLOW + "Weekly Referrals: " + ChatColor.WHITE + referrals);
					}
					if(constants.SHOW_DAILY_REFERRALS == true) {
						String referrals = OnTimeUtilities.getDailyReferrals(targetPlayer.getName());
						sender.sendMessage(ChatColor.YELLOW + "Daily Referrals: " + ChatColor.WHITE + referrals);
					}
				}
				catch(NoSuchFieldError e) {
					plugin.getLogger().warning("Only certain OnTime data was shown!");
					plugin.getLogger().warning("You are strongly encouraged to update "
							+ "to OnTime version 3.9.0 or above!");
				}
			}
		}
		return true;
	}
}
