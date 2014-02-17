package ca.uvic.lscholte.utilities;

/**
 * A utility class containing commonly used
 * methods related to numbers
 */
public final class NumberUtilities {
	
	/**
	 * Utility class cannot be instantiated
	 */
	private NumberUtilities() { }
	
	/**
	 * Checks if a given String is a <code>double</code>
	 * 
	 * @param str The String to check
	 * @return <code>true</code> if the String was a <code>double</code>;
	 * <code>false</code> otherwise
	 */
	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
		}
		catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if a given String is an <code>int</code>
	 * 
	 * @param str The String to check
	 * @return <code>true</code> if the String was an <code>int</code>;
	 * <code>false</code> otherwise
	 */
	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		}
		catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
}
