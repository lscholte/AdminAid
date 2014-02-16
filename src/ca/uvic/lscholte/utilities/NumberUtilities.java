package ca.uvic.lscholte.utilities;

public final class NumberUtilities {
	
	private NumberUtilities() { }
	
	public static boolean isDouble(String string) {
		try {
			Double.parseDouble(string);
		}
		catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
		}
		catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
}
