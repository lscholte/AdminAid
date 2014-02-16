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

public class ConfigValues {
	
	private AdminAid plugin;
	
	private boolean freezePlayersOnWarns = false;
	
	private int freezeTime = 0;
	
	private boolean autoRecordBans = false;
	private boolean autoRecordTempbans = false;
	private boolean autoRecordUnbans = false;
	private boolean autoRecordKicks = false;
	private boolean autoRecordMutes = false;
	private boolean autoRecordTempmutes = false;
	private boolean autoRecordUnmutes = false;
	private boolean autoRecordWarns = false;
	
	private boolean broadcastBans = false;
	private boolean broadcastTempbans = false;
	private boolean broadcastUnbans = false;
	private boolean broadcastKicks = false;
	private boolean broadcastMutes = false;
	private boolean broadcastTempmutes = false;
	private boolean broadcastUnmutes = false;
	private boolean broadcastWarns = false;
	
	private boolean showOnlineStatus = false;
	private boolean showLastLogin = false;
	private boolean showFirstLogin = false;
	private boolean showIpAddress = false;
	private boolean showLocation = false;
	private boolean showBannedStatus = false;
	private boolean showMutedStatus = false;
	private boolean showStaffMemberStatus = false;
	private boolean showChatSpyStatus = false;
	private boolean showBanExemptStatus = false;
	private boolean showMuteExemptStatus = false;
	private boolean showKickExemptStatus = false;
	private boolean showTotalPlayTime = false;
	private boolean showMonthlyPlayTime = false;
	private boolean showWeeklyPlayTime = false;
	private boolean showDailyPlayTime = false;
	private boolean showTotalVotes = false;
	private boolean showMonthlyVotes = false;
	private boolean showWeeklyVotes = false;
	private boolean showDailyVotes = false;
	private boolean showTotalReferrals = false;
	private boolean showMonthlyReferrals = false;
	private boolean showWeeklyReferrals = false;
	private boolean showDailyReferrals = false;
	
	private int rulesPerPage = 0;
	private int infoPerPage = 0;
	private int mailPerPage = 0;
	private int notesPerPage = 0;
	
	private String prefix = "";
	
	private String privateChatColor;
	private String chatSpyColor;
	private String staffChatColor;
	
	private List<String> loginMessages = new ArrayList<String>();
		
	public ConfigValues(AdminAid instance) {
		plugin = instance;
		
		freezePlayersOnWarns = plugin.getConfig().getBoolean("FreezePlayersOnWarns");
		
		freezeTime = plugin.getConfig().getInt("FreezeTime");
		
		autoRecordBans = plugin.getConfig().getBoolean("AutoRecordNotes.Bans");
		autoRecordTempbans = plugin.getConfig().getBoolean("AutoRecordNotes.Tempbans");
		autoRecordUnbans = plugin.getConfig().getBoolean("AutoRecordNotes.Unbans");
		autoRecordKicks = plugin.getConfig().getBoolean("AutoRecordNotes.Kicks");
		autoRecordMutes = plugin.getConfig().getBoolean("AutoRecordNotes.Mutes");
		autoRecordTempmutes = plugin.getConfig().getBoolean("AutoRecordNotes.Tempmutes");
		autoRecordUnmutes = plugin.getConfig().getBoolean("AutoRecordNotes.Unmutes");
		autoRecordWarns = plugin.getConfig().getBoolean("AutoRecordNotes.Warns");
		
		broadcastBans = plugin.getConfig().getBoolean("Broadcast.Bans");
		broadcastTempbans = plugin.getConfig().getBoolean("Broadcast.Tempbans");
		broadcastUnbans = plugin.getConfig().getBoolean("Broadcast.Unbans");
		broadcastKicks = plugin.getConfig().getBoolean("Broadcast.Kicks");
		broadcastMutes = plugin.getConfig().getBoolean("Broadcast.Mutes");
		broadcastTempmutes = plugin.getConfig().getBoolean("Broadcast.Tempmutes");
		broadcastUnmutes = plugin.getConfig().getBoolean("Broadcast.Unmutes");
		broadcastWarns = plugin.getConfig().getBoolean("Broadcast.Warns");
		
		showOnlineStatus = plugin.getConfig().getBoolean("PlayerInfo.ShowOnlineStatus");
		showLastLogin = plugin.getConfig().getBoolean("PlayerInfo.ShowLastLogin");
		showFirstLogin = plugin.getConfig().getBoolean("PlayerInfo.ShowFirstLogin");
		showIpAddress = plugin.getConfig().getBoolean("PlayerInfo.ShowIPAddress");
		showLocation = plugin.getConfig().getBoolean("PlayerInfo.ShowLocation");
		showBannedStatus = plugin.getConfig().getBoolean("PlayerInfo.ShowBannedStatus");
		showMutedStatus = plugin.getConfig().getBoolean("PlayerInfo.ShowMutedStatus");
		showStaffMemberStatus = plugin.getConfig().getBoolean("PlayerInfo.ShowStaffMemberStatus");
		showChatSpyStatus = plugin.getConfig().getBoolean("PlayerInfo.ShowChatSpyStatus");
		showBanExemptStatus = plugin.getConfig().getBoolean("PlayerInfo.ShowBanExemptStatus");
		showMuteExemptStatus = plugin.getConfig().getBoolean("PlayerInfo.ShowMuteExemptStatus");
		showKickExemptStatus = plugin.getConfig().getBoolean("PlayerInfo.ShowKickExemptStatus");
		showTotalPlayTime = plugin.getConfig().getBoolean("PlayerInfo.ShowTotalPlayTime");
		showMonthlyPlayTime = plugin.getConfig().getBoolean("PlayerInfo.ShowMonthlyPlayTime");
		showWeeklyPlayTime = plugin.getConfig().getBoolean("PlayerInfo.ShowWeeklyPlayTime");
		showDailyPlayTime = plugin.getConfig().getBoolean("PlayerInfo.ShowDailyPlayTime");
		showTotalVotes = plugin.getConfig().getBoolean("PlayerInfo.ShowTotalVotes");
		showMonthlyVotes = plugin.getConfig().getBoolean("PlayerInfo.ShowMonthlyVotes");
		showWeeklyVotes = plugin.getConfig().getBoolean("PlayerInfo.ShowWeeklyVotes");
		showDailyVotes = plugin.getConfig().getBoolean("PlayerInfo.ShowDailyVotes");
		showTotalReferrals = plugin.getConfig().getBoolean("PlayerInfo.ShowTotalReferrals");
		showMonthlyReferrals = plugin.getConfig().getBoolean("PlayerInfo.ShowMonthlyReferrals");
		showWeeklyReferrals = plugin.getConfig().getBoolean("PlayerInfo.ShowWeeklyReferrals");
		showDailyReferrals = plugin.getConfig().getBoolean("PlayerInfo.ShowDailyReferrals");
		
		rulesPerPage = plugin.getConfig().getInt("MessagesPerPage.Rules");
		infoPerPage = plugin.getConfig().getInt("MessagesPerPage.Info");
		mailPerPage = plugin.getConfig().getInt("MessagesPerPage.Mail");
		notesPerPage = plugin.getConfig().getInt("MessagesPerPage.Notes");

		prefix = plugin.getConfig().getString("Prefix");
		
		privateChatColor = plugin.getConfig().getString("PrivateChatColor");
		chatSpyColor = plugin.getConfig().getString("ChatSpyColor");
		staffChatColor = plugin.getConfig().getString("StaffChatColor");
		
		loginMessages = plugin.getConfig().getStringList("LoginMessages");
	}
	
	public boolean freezePlayersOnWarns() {
		return freezePlayersOnWarns;
	}
	
	public int getFreezeTime() {
		return freezeTime;
	}
	
	public boolean autoRecordBans() {
		return autoRecordBans;
	}
	
	public boolean autoRecordTempbans() {
		return autoRecordTempbans;
	}
	
	public boolean autoRecordUnbans() {
		return autoRecordUnbans;
	}
	
	public boolean autoRecordKicks() {
		return autoRecordKicks;
	}
	
	public boolean autoRecordMutes() {
		return autoRecordMutes;
	}
	
	public boolean autoRecordTempmutes() {
		return autoRecordTempmutes;
	}
	
	public boolean autoRecordUnmutes() {
		return autoRecordUnmutes;
	}
	
	public boolean autoRecordWarns() {
		return autoRecordWarns;
	}
	
	public boolean broadcastBans() {
		return broadcastBans;
	}
	
	public boolean broadcastTempbans() {
		return broadcastTempbans;
	}
	
	public boolean broadcastUnbans() {
		return broadcastUnbans;
	}
	
	public boolean broadcastKicks() {
		return broadcastKicks;
	}
	
	public boolean broadcastMutes() {
		return broadcastMutes;
	}
	
	public boolean broadcastTempmutes() {
		return broadcastTempmutes;
	}
	
	public boolean broadcastUnmutes() {
		return broadcastUnmutes;
	}
	
	public boolean broadcastWarns() {
		return broadcastWarns;
	}
	
	public boolean showOnlineStatus() {
		return showOnlineStatus;
	}
	
	public boolean showLastLogin() {
		return showLastLogin;
	}
	
	public boolean showFirstLogin() {
		return showFirstLogin;
	}
	
	public boolean showIpAddress() {
		return showIpAddress;
	}
	
	public boolean showLocation() {
		return showLocation;
	}
	
	public boolean showBannedStatus() {
		return showBannedStatus;
	}
	
	public boolean showMutedStatus() {
		return showMutedStatus;
	}
	
	public boolean showStaffMemberStatus() {
		return showStaffMemberStatus;
	}
	
	public boolean showChatSpyStatus() {
		return showChatSpyStatus;
	}
	
	public boolean showBanExemptStatus() {
		return showBanExemptStatus;
	}
	
	public boolean showMuteExemptStatus() {
		return showMuteExemptStatus;
	}
	
	public boolean showKickExemptStatus() {
		return showKickExemptStatus;
	}
	
	public boolean showTotalPlayTime() {
		return showTotalPlayTime;
	}
	
	public boolean showMonthlyPlayTime() {
		return showMonthlyPlayTime;
	}
	
	public boolean showWeeklyPlayTime() {
		return showWeeklyPlayTime;
	}
	
	public boolean showDailyPlayTime() {
		return showDailyPlayTime;
	}
	
	public boolean showTotalVotes() {
		return showTotalVotes;
	}
	
	public boolean showMonthlyVotes() {
		return showMonthlyVotes;
	}
	
	public boolean showWeeklyVotes() {
		return showWeeklyVotes;
	}
	
	public boolean showDailyVotes() {
		return showDailyVotes;
	}
	
	public boolean showTotalReferrals() {
		return showTotalReferrals;
	}
	
	public boolean showMonthlyReferrals() {
		return showMonthlyReferrals;
	}
	
	public boolean showWeeklyReferrals() {
		return showWeeklyReferrals;
	}
	
	public boolean showDailyReferrals() {
		return showDailyReferrals;
	}
	
	public double getRulesPerPage() {
		return rulesPerPage;
	}
	
	public double getInfoPerPage() {
		return infoPerPage;
	}
	
	public double getMailPerPage() {
		return mailPerPage;
	}
	
	public double getNotesPerPage() {
		return notesPerPage;
	}
	
	public String getPrefix(CommandSender sender) {
		Date date = new Date();
		prefix = ChatColor.translateAlternateColorCodes('&', prefix);
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
		
		for(int i = 0; i < loginMessages.size(); ++i) {
			String line = loginMessages.get(i);
			line = ChatColor.translateAlternateColorCodes('&', line);
			line = line.replace("<player>", player.getName());
			line = line.replace("<playerlist>", playerList);
			line = line.replace("<world>", player.getWorld().getName());
			line = line.replace("<worldlist>", worldList);
			line = line.replace("<date>", dateString);
			line = line.replace("<playercount>", onlinePlayerCount + "/" + maxPlayerCount);
			loginMessages.set(i, line);
		}
		return loginMessages;
	}
	
	public String getPrivateMessageColor() {
		return privateChatColor;
	}
	
	public String getChatSpyColor() {
		return chatSpyColor;
	}
	
	public String getStaffChatColor() {
		return staffChatColor;
	}
}
