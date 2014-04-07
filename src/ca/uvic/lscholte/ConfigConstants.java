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
	
	private ConfigConstants() { }
		
	public static final boolean FREEZE_PLAYERS_ON_WARNS = AdminAid.getPlugin().getConfig().getBoolean("FreezePlayersOnWarns");
	
	public static final int FREEZE_TIME = AdminAid.getPlugin().getConfig().getInt("FreezeTime");
	
	public static final boolean AUTO_RECORD_BANS = AdminAid.getPlugin().getConfig().getBoolean("AutoRecordNotes.Bans");
	public static final boolean AUTO_RECORD_TEMPBANS = AdminAid.getPlugin().getConfig().getBoolean("AutoRecordNotes.Tempbans");
	public static final boolean AUTO_RECORD_UNBANS = AdminAid.getPlugin().getConfig().getBoolean("AutoRecordNotes.Unbans");
	public static final boolean AUTO_RECORD_KICKS = AdminAid.getPlugin().getConfig().getBoolean("AutoRecordNotes.Kicks");
	public static final boolean AUTO_RECORD_MUTES = AdminAid.getPlugin().getConfig().getBoolean("AutoRecordNotes.Mutes");
	public static final boolean AUTO_RECORD_TEMPMUTES = AdminAid.getPlugin().getConfig().getBoolean("AutoRecordNotes.Tempmutes");
	public static final boolean AUTO_RECORD_UNMUTES = AdminAid.getPlugin().getConfig().getBoolean("AutoRecordNotes.Unmutes");
	public static final boolean AUTO_RECORD_WARNS = AdminAid.getPlugin().getConfig().getBoolean("AutoRecordNotes.Warns");
	
	public static final boolean BROADCAST_BANS = AdminAid.getPlugin().getConfig().getBoolean("Broadcast.Bans");
	public static final boolean BROADCAST_TEMPBANS = AdminAid.getPlugin().getConfig().getBoolean("Broadcast.Tempbans");
	public static final boolean BROADCAST_UNBANS = AdminAid.getPlugin().getConfig().getBoolean("Broadcast.Unbans");
	public static final boolean BROADCAST_KICKS = AdminAid.getPlugin().getConfig().getBoolean("Broadcast.Kicks");
	public static final boolean BROADCAST_MUTES = AdminAid.getPlugin().getConfig().getBoolean("Broadcast.Mutes");
	public static final boolean BROADCAST_TEMPMUTES = AdminAid.getPlugin().getConfig().getBoolean("Broadcast.Tempmutes");
	public static final boolean BROADCAST_UNMUTES = AdminAid.getPlugin().getConfig().getBoolean("Broadcast.Unmutes");
	public static final boolean BROADCAST_WARNS = AdminAid.getPlugin().getConfig().getBoolean("Broadcast.Warns");

	public static final boolean SHOW_ONLINE_STATUS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowOnlineStatus");
	public static final boolean SHOW_LAST_LOGIN = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowLastLogin");
	public static final boolean SHOW_FIRST_LOGIN = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowFirstLogin");
	public static final boolean SHOW_IP_ADDRESS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowIPAddress");
	public static final boolean SHOW_LOCATION = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowLocation");
	public static final boolean SHOW_BANNED_STATUS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowBannedStatus");
	public static final boolean SHOW_MUTED_STATUS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowMutedStatus");
	public static final boolean SHOW_STAFF_MEMBER_STATUS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowStaffMemberStatus");
	public static final boolean SHOW_CHAT_SPY_STATUS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowChatSpyStatus");
	public static final boolean SHOW_BAN_EXEMPT_STATUS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowBanExemptStatus");
	public static final boolean SHOW_MUTE_EXEMPT_STATUS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowMuteExemptStatus");
	public static final boolean SHOW_KICK_EXEMPT_STATUS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowKickExemptStatus");
	
	public static final boolean SHOW_TOTAL_PLAY_TIME = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowTotalPlayTime");
	public static final boolean SHOW_MONTHLY_PLAY_TIME = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowMonthlyPlayTime");
	public static final boolean SHOW_WEEKLY_PLAY_TIME = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowWeeklyPlayTime");
	public static final boolean SHOW_DAILY_PLAY_TIME = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowDailyPlayTime");

	public static final boolean SHOW_TOTAL_VOTES = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowTotalVotes");
	public static final boolean SHOW_MONTHLY_VOTES = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowMonthlyVotes");
	public static final boolean SHOW_WEEKLY_VOTES = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowWeeklyVotes");
	public static final boolean SHOW_DAILY_VOTES = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowDailyVotes");
	
	public static final boolean SHOW_TOTAL_REFERRALS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowTotalReferrals");
	public static final boolean SHOW_MONTHLY_REFERRALS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowMonthlyReferrals");
	public static final boolean SHOW_WEEKLY_REFERRALS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowWeeklyReferrals");
	public static final boolean SHOW_DAILY_REFERRALS = AdminAid.getPlugin().getConfig().getBoolean("PlayerInfo.ShowDailyReferrals");
	
	public static final int RULES_PER_PAGE = AdminAid.getPlugin().getConfig().getInt("MessagesPerPage.Rules");
	public static final int INFO_PER_PAGE = AdminAid.getPlugin().getConfig().getInt("MessagesPerPage.Info");
	public static final int MAIL_PER_PAGE = AdminAid.getPlugin().getConfig().getInt("MessagesPerPage.Mail");
	public static final int NOTES_PER_PAGE = AdminAid.getPlugin().getConfig().getInt("MessagesPerPage.Notes");
	
	public static final String PRIVATE_CHAT_COLOR = AdminAid.getPlugin().getConfig().getString("PrivateChatColor");
	public static final String CHAT_SPY_COLOR = AdminAid.getPlugin().getConfig().getString("ChatSpyColor");
	public static final String STAFF_CHAT_COLOR = AdminAid.getPlugin().getConfig().getString("StaffChatColor");
	
	
	private static final String PREFIX = AdminAid.getPlugin().getConfig().getString("Prefix");
	
	private static final List<String> LOGIN_MESSAGES = AdminAid.getPlugin().getConfig().getStringList("LoginMessages");

	public static String getPrefix(CommandSender sender) {
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
	
	public static List<String> getLoginMessages(Player player) {
		Date date = new Date();
		String dateString = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a z").format(date);
		
		int maxPlayerCount = Bukkit.getServer().getMaxPlayers();
		int onlinePlayerCount = 0; 
		
		StringBuilder sb = new StringBuilder();
		for(Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
			sb.append(onlinePlayer.getName() + ", ");
			onlinePlayerCount++;
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
