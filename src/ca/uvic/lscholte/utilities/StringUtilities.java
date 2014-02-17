package ca.uvic.lscholte.utilities;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class containing commonly used
 * methods related to strings
 */
public final class StringUtilities {
	
	/**
	 * Utility class cannot be instantiated
	 */
	private StringUtilities() { }
	
	/**
	 * Checks if a String is in a given List, ignoring case considerations
	 * 
	 * @param str The String to look for
	 * @param list The List of Strings to search in
	 * @return <code>true</code> if the specified String was found
	 * in the List; <code>false</code> otherwise
	 */
	public static boolean containsIgnoreCase(String str, List<String> list) {
		for(String s : list) {
			if(s.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the index of a String in a given List, ignoring case considerations
	 * 
	 * @param str The String to look for
	 * @param list The List of Strings to search in
	 * @return The index of the specified String as an <code>int</code>;
	 * -1 if String was not found
	 */
	public static int getIndexOfString(String str, List<String> list) {
		for(int i = 0; i < list.size(); ++i) {
			if(str.equalsIgnoreCase(list.get(i))) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Checks if a name contains an invalid character
	 * according to Minecraft's naming regulations
	 * 
	 * <p>A valid name may only contain a-z, A-Z, 0-9, and _</p>
	 * 
	 * @param name The name to check for invalid characters
	 * @return <code>true</code> if name contains an invalid character;
	 * <code>false</code> otherwise
	 */
	public static boolean nameContainsInvalidCharacter(String name) {
		Pattern pattern = Pattern.compile("\\W+");
		Matcher matcher = pattern.matcher(name);
		if(matcher.find()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Builds a String using the elements of a String array separated by spaces.
	 * The String will be built starting with a specified element until the
	 * last element in the array
	 * 
	 * @param args The String array to build the String from
	 * @param start The element of the array to begin building the String
	 * @return The resulting String obtained by concatenating the String array
	 * separated by spaces  
	 */
	public static String buildString(String[] args, int start) {
		StringBuilder sb = new StringBuilder();
		for(int i = start; i < args.length; ++i) {
			sb.append(args[i]).append(" ");
		}
		String message = sb.toString().trim();
		return message;
	}
}
