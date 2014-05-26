package ca.uvic.lscholte;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ConfigConstants {
	
	private static ConfigConstants instance;
	
	public final boolean FREEZE_PLAYERS_ON_WARNS;
	
	public final int FREEZE_TIME;
	
	public final boolean AUTO_RECORD_BANS;
	public final boolean AUTO_RECORD_TEMPBANS;
	public final boolean AUTO_RECORD_UNBANS;
	public final boolean AUTO_RECORD_KICKS;
	public final boolean AUTO_RECORD_MUTES;
	public final boolean AUTO_RECORD_TEMPMUTES;
	public final boolean AUTO_RECORD_UNMUTES;
	public final boolean AUTO_RECORD_WARNS;
	
	public final boolean BROADCAST_BANS;
	public final boolean BROADCAST_TEMPBANS;
	public final boolean BROADCAST_UNBANS;
	public final boolean BROADCAST_KICKS;
	public final boolean BROADCAST_MUTES;
	public final boolean BROADCAST_TEMPMUTES;
	public final boolean BROADCAST_UNMUTES;
	public final boolean BROADCAST_WARNS;

	public final boolean SHOW_UUID;
	public final boolean SHOW_ONLINE_STATUS;
	public final boolean SHOW_LAST_LOGIN;
	public final boolean SHOW_FIRST_LOGIN;
	public final boolean SHOW_IP_ADDRESS;
	public final boolean SHOW_LOCATION;
	public final boolean SHOW_BANNED_STATUS;
	public final boolean SHOW_MUTED_STATUS;
	public final boolean SHOW_STAFF_MEMBER_STATUS;
	public final boolean SHOW_CHAT_SPY_STATUS;
	public final boolean SHOW_BAN_EXEMPT_STATUS;
	public final boolean SHOW_MUTE_EXEMPT_STATUS;
	public final boolean SHOW_KICK_EXEMPT_STATUS;
	
	public final boolean SHOW_TOTAL_PLAY_TIME;
	public final boolean SHOW_MONTHLY_PLAY_TIME;
	public final boolean SHOW_WEEKLY_PLAY_TIME;
	public final boolean SHOW_DAILY_PLAY_TIME;

	public final boolean SHOW_TOTAL_VOTES;
	public final boolean SHOW_MONTHLY_VOTES;
	public final boolean SHOW_WEEKLY_VOTES;
	public final boolean SHOW_DAILY_VOTES;
	
	public final boolean SHOW_TOTAL_REFERRALS;
	public final boolean SHOW_MONTHLY_REFERRALS;
	public final boolean SHOW_WEEKLY_REFERRALS;
	public final boolean SHOW_DAILY_REFERRALS;
	
	public final int RULES_PER_PAGE;
	public final int INFO_PER_PAGE;
	public final int MAIL_PER_PAGE;
	public final int NOTES_PER_PAGE;
	
	public final String PRIVATE_CHAT_COLOR;
	public final String CHAT_SPY_COLOR;
	public final String STAFF_CHAT_COLOR;
	
	private final String PREFIX;
	
	private final List<String> LOGIN_MESSAGES;

	private ConfigConstants(AdminAid plugin) {
		FREEZE_PLAYERS_ON_WARNS = plugin.getConfig().getBoolean("FreezePlayersOnWarns");

		FREEZE_TIME = plugin.getConfig().getInt("FreezeTime");

		AUTO_RECORD_BANS = plugin.getConfig().getBoolean("AutoRecordNotes.Bans");
		AUTO_RECORD_TEMPBANS = plugin.getConfig().getBoolean("AutoRecordNotes.Tempbans");
		AUTO_RECORD_UNBANS = plugin.getConfig().getBoolean("AutoRecordNotes.Unbans");
		AUTO_RECORD_KICKS = plugin.getConfig().getBoolean("AutoRecordNotes.Kicks");
		AUTO_RECORD_MUTES = plugin.getConfig().getBoolean("AutoRecordNotes.Mutes");
		AUTO_RECORD_TEMPMUTES = plugin.getConfig().getBoolean("AutoRecordNotes.Tempmutes");
		AUTO_RECORD_UNMUTES = plugin.getConfig().getBoolean("AutoRecordNotes.Unmutes");
		AUTO_RECORD_WARNS = plugin.getConfig().getBoolean("AutoRecordNotes.Warns");

		BROADCAST_BANS = plugin.getConfig().getBoolean("Broadcast.Bans");
		BROADCAST_TEMPBANS = plugin.getConfig().getBoolean("Broadcast.Tempbans");
		BROADCAST_UNBANS = plugin.getConfig().getBoolean("Broadcast.Unbans");
		BROADCAST_KICKS = plugin.getConfig().getBoolean("Broadcast.Kicks");
		BROADCAST_MUTES = plugin.getConfig().getBoolean("Broadcast.Mutes");
		BROADCAST_TEMPMUTES = plugin.getConfig().getBoolean("Broadcast.Tempmutes");
		BROADCAST_UNMUTES = plugin.getConfig().getBoolean("Broadcast.Unmutes");
		BROADCAST_WARNS = plugin.getConfig().getBoolean("Broadcast.Warns");

		SHOW_UUID = plugin.getConfig().getBoolean("PlayerInfo.ShowUUID");
		SHOW_ONLINE_STATUS = plugin.getConfig().getBoolean("PlayerInfo.ShowOnlineStatus");
		SHOW_LAST_LOGIN = plugin.getConfig().getBoolean("PlayerInfo.ShowLastLogin");
		SHOW_FIRST_LOGIN = plugin.getConfig().getBoolean("PlayerInfo.ShowFirstLogin");
		SHOW_IP_ADDRESS = plugin.getConfig().getBoolean("PlayerInfo.ShowIPAddress");
		SHOW_LOCATION = plugin.getConfig().getBoolean("PlayerInfo.ShowLocation");
		SHOW_BANNED_STATUS = plugin.getConfig().getBoolean("PlayerInfo.ShowBannedStatus");
		SHOW_MUTED_STATUS = plugin.getConfig().getBoolean("PlayerInfo.ShowMutedStatus");
		SHOW_STAFF_MEMBER_STATUS = plugin.getConfig().getBoolean("PlayerInfo.ShowStaffMemberStatus");
		SHOW_CHAT_SPY_STATUS = plugin.getConfig().getBoolean("PlayerInfo.ShowChatSpyStatus");
		SHOW_BAN_EXEMPT_STATUS = plugin.getConfig().getBoolean("PlayerInfo.ShowBanExemptStatus");
		SHOW_MUTE_EXEMPT_STATUS = plugin.getConfig().getBoolean("PlayerInfo.ShowMuteExemptStatus");
		SHOW_KICK_EXEMPT_STATUS = plugin.getConfig().getBoolean("PlayerInfo.ShowKickExemptStatus");

		SHOW_TOTAL_PLAY_TIME = plugin.getConfig().getBoolean("PlayerInfo.ShowTotalPlayTime");
		SHOW_MONTHLY_PLAY_TIME = plugin.getConfig().getBoolean("PlayerInfo.ShowMonthlyPlayTime");
		SHOW_WEEKLY_PLAY_TIME = plugin.getConfig().getBoolean("PlayerInfo.ShowWeeklyPlayTime");
		SHOW_DAILY_PLAY_TIME = plugin.getConfig().getBoolean("PlayerInfo.ShowDailyPlayTime");

		SHOW_TOTAL_VOTES = plugin.getConfig().getBoolean("PlayerInfo.ShowTotalVotes");
		SHOW_MONTHLY_VOTES = plugin.getConfig().getBoolean("PlayerInfo.ShowMonthlyVotes");
		SHOW_WEEKLY_VOTES = plugin.getConfig().getBoolean("PlayerInfo.ShowWeeklyVotes");
		SHOW_DAILY_VOTES = plugin.getConfig().getBoolean("PlayerInfo.ShowDailyVotes");

		SHOW_TOTAL_REFERRALS = plugin.getConfig().getBoolean("PlayerInfo.ShowTotalReferrals");
		SHOW_MONTHLY_REFERRALS = plugin.getConfig().getBoolean("PlayerInfo.ShowMonthlyReferrals");
		SHOW_WEEKLY_REFERRALS = plugin.getConfig().getBoolean("PlayerInfo.ShowWeeklyReferrals");
		SHOW_DAILY_REFERRALS = plugin.getConfig().getBoolean("PlayerInfo.ShowDailyReferrals");

		RULES_PER_PAGE = plugin.getConfig().getInt("MessagesPerPage.Rules");
		INFO_PER_PAGE = plugin.getConfig().getInt("MessagesPerPage.Info");
		MAIL_PER_PAGE = plugin.getConfig().getInt("MessagesPerPage.Mail");
		NOTES_PER_PAGE = plugin.getConfig().getInt("MessagesPerPage.Notes");

		PRIVATE_CHAT_COLOR = plugin.getConfig().getString("PrivateChatColor");
		CHAT_SPY_COLOR = plugin.getConfig().getString("ChatSpyColor");
		STAFF_CHAT_COLOR = plugin.getConfig().getString("StaffChatColor");

		PREFIX = plugin.getConfig().getString("Prefix");

		LOGIN_MESSAGES = plugin.getConfig().getStringList("LoginMessages");
	}
	
	public static ConfigConstants getInstance(AdminAid plugin) {
		if(instance == null) {
			instance = new ConfigConstants(plugin);
		}
		return instance;
	}
	
	public String getPrefix(CommandSender sender) {
		Date date = new Date();
		String prefix = ChatColor.translateAlternateColorCodes('&', PREFIX);
		prefix = prefix.replace("<MM>", new SimpleDateFormat("MM").format(date));
		prefix = prefix.replace("<MMM>", new SimpleDateFormat("MMM").format(date));
		prefix = prefix.replace("<MMMM>", new SimpleDateFormat("MMMM").format(date));
		prefix = prefix.replace("<dd>", new SimpleDateFormat("dd").format(date));
		prefix = prefix.replace("<yyyy>", new SimpleDateFormat("yyyy").format(date));
		prefix = prefix.replace("<yy>", new SimpleDateFormat("yy").format(date));
		prefix = prefix.replace("<HH>", new SimpleDateFormat("HH").format(date));
		prefix = prefix.replace("<hh>", new SimpleDateFormat("hh").format(date));
		prefix = prefix.replace("<mm>", new SimpleDateFormat("mm").format(date));
		prefix = prefix.replace("<ss>", new SimpleDateFormat("ss").format(date));
		prefix = prefix.replace("<a>", new SimpleDateFormat("a").format(date));
		prefix = prefix.replace("<Z>", new SimpleDateFormat("z").format(date));
		prefix = prefix.replace("<player>", sender.getName());
		return prefix;
	}
	
	public List<String> getLoginMessages(Player player) {
		Date date = new Date();
		String dateString = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a z").format(date);
		
		int maxPlayerCount = Bukkit.getServer().getMaxPlayers();
		//int onlinePlayerCount = 0; 
		int onlinePlayerCount = Bukkit.getServer().getOnlinePlayers().length;
		
		StringBuilder sb = new StringBuilder();
		for(Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
			sb.append(onlinePlayer.getName() + ", ");
			//onlinePlayerCount++;
		}
		
		String playerList = "{" + sb.toString().trim() + "}";
		Pattern pattern1 = Pattern.compile(",}");
		Matcher matcher1 = pattern1.matcher(playerList);
		playerList = matcher1.replaceAll("}");
		sb.setLength(0);
		
		for(World world : Bukkit.getServer().getWorlds()) {
			sb.append(world.getName() + ", ");
		}
		String worldList = "{" + sb.toString().trim() + "}";
		Pattern pattern2 = Pattern.compile(",}");
		Matcher matcher2 = pattern2.matcher(worldList);
		worldList = matcher2.replaceAll("}");
		
		List<String> loginMessages = new ArrayList<String>(LOGIN_MESSAGES.size());
		
		for(int i = 0; i < LOGIN_MESSAGES.size(); ++i) {
			String line = LOGIN_MESSAGES.get(i);
			line = ChatColor.translateAlternateColorCodes('&', line);
			line = line.replace("<player>", player.getName());
			line = line.replace("<playerlist>", playerList);
			line = line.replace("<world>", player.getWorld().getName());
			line = line.replace("<worldlist>", worldList);
			line = line.replace("<date>", dateString);
			line = line.replace("<playercount>", onlinePlayerCount + "/" + maxPlayerCount);
			loginMessages.add(line);
		}
		return loginMessages;
	}
}
