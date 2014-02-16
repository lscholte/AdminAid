package ca.uvic.lscholte.utilities;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtilities {
	
	private StringUtilities() { }
	
	public static boolean containsIgnoreCase(String string, List<String> list) {
		for(String s : list) {
			if(s.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}
	
	public static int getIndexOfString(String string, List<String> list) {
		for(int i = 0; i < list.size(); ++i) {
			if(string.equalsIgnoreCase(list.get(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public static boolean nameContainsInvalidCharacter(String name) {
		Pattern pattern = Pattern.compile("\\W+");
		Matcher matcher = pattern.matcher(name);
		if(matcher.find()) {
			return true;
		}
		return false;
	}
}
