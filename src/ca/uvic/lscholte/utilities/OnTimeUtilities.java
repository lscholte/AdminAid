package ca.uvic.lscholte.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.edge209.OnTime.DataIO;
import me.edge209.OnTime.OnTimeAPI;
import me.edge209.OnTime.PlayingTime;

public final class OnTimeUtilities {
	
	private OnTimeUtilities() { }
	
	public static String splitSeconds(double seconds) {
		int days = 0;
		int hours = 0;
		int minutes = 0;
		
		if(seconds >= 86400) {
			days = (int) Math.floor(seconds/86400);
			seconds = seconds % 86400;
		}
		if(seconds >= 3600) {
			hours = (int) Math.floor(seconds/3600);
			seconds = seconds % 3600;
		}
		if(seconds >= 60) {
			minutes = (int) Math.floor(seconds/60);
			seconds = seconds % 60;
		}
		int sec = (int) seconds;
		
		String time = days + " days, " + hours + " hours, "
				+ minutes + " minutes, " + sec + " seconds";
		
		if(days == 0) time = time.replace("0 days, ", "");
		if(hours == 0) time = time.replace("0 hours, ", "");
		if(minutes == 0) time = time.replace("0 minutes, ", "");
		if(sec == 0) time = time.replace("0 seconds", "");
		
		if(days == 1) time = time.replace("1 days, ", "1 day, ");
		if(hours == 1) time = time.replace("1 hours, ", "1 hour, ");
		if(minutes == 1) time = time.replace("1 minutes, ", "1 minute, ");
		if(sec == 1) time = time.replace("1 seconds", "1 second");
		
		Pattern pattern = Pattern.compile(", $");
		Matcher matcher = pattern.matcher(time);
		time = matcher.replaceAll("");
		
		if(time.isEmpty()) {
			time = "No play time has been accumulated";
		}
		return time;
	}
	
	public static String formatVotes(int votes) {
		if(votes <= 0) {
			return "No votes";
		}
		if(votes == 1) {
			return "1 vote";
		}
		return votes + " votes";
	}
	
	public static String formatReferrals(int referrals) {
		if(referrals <= 0) {
			return "No referrals";
		}
		if(referrals == 1) {
			return "1 referral";
		}
		return referrals + " referrals";
	}
	
	public static boolean hasOnTimeRecord(String targetPlayer) {
		return PlayingTime.playerHasOnTimeRecord(targetPlayer);
	}
	
	public static String getTotalPlayTime(String targetPlayer) {
		double seconds = DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.TOTALPLAY)/1000;
		return splitSeconds(seconds);
	}
	
	public static String getMonthlyPlayTime(String targetPlayer) {
		double seconds = DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.MONTHPLAY)/1000;
		return splitSeconds(seconds);
	}
	
	public static String getWeeklyPlayTime(String targetPlayer) {
		double seconds = DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.WEEKPLAY)/1000;
		return splitSeconds(seconds);
	}
	
	public static String getDailyPlayTime(String targetPlayer) {
		double seconds = DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.TODAYPLAY)/1000;
		return splitSeconds(seconds);
	}
	
	public static String getTotalVotes(String targetPlayer) {
		int votes = (int) DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.TOTALVOTE);
		return formatVotes(votes);
	}
	
	public static String getMonthlyVotes(String targetPlayer) {
		int votes = (int) DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.MONTHVOTE);
		return formatVotes(votes);
	}
	
	public static String getWeeklyVotes(String targetPlayer) {
		int votes = (int) DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.WEEKVOTE);
		return formatVotes(votes);
	}
	
	public static String getDailyVotes(String targetPlayer) {
		int votes = (int) DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.TODAYVOTE);
		return formatVotes(votes);
	}
	
	public static String getTotalReferrals(String targetPlayer) {
		int referrals = (int) DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.TOTALREFER);
		return formatReferrals(referrals);
	}
	
	public static String getMonthlyReferrals(String targetPlayer) {
		int referrals = (int) DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.MONTHREFER);
		return formatReferrals(referrals);
	}
	
	public static String getWeeklyReferrals(String targetPlayer) {
		int referrals = (int) DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.WEEKREFER);
		return formatReferrals(referrals);
	}
	
	public static String getDailyReferrals(String targetPlayer) {
		int referrals = (int) DataIO.getPlayerTimeData(targetPlayer, OnTimeAPI.data.TODAYREFER);
		return formatReferrals(referrals);
	}
}
