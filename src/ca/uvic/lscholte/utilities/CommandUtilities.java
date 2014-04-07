package ca.uvic.lscholte.utilities;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import ca.uvic.lscholte.AdminAid;

public final class CommandUtilities {
	
	private CommandUtilities() { }
	
	private static Object getPrivateField(Object object, String field) throws SecurityException,
    NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		Field objectField = clazz.getDeclaredField(field);
		objectField.setAccessible(true);
		Object result = objectField.get(object);
		objectField.setAccessible(false);
		return result;
	}
 
	public static void unregisterBukkitCommand(PluginCommand cmd) {
		try {
			Object result = getPrivateField(AdminAid.getPlugin().getServer().getPluginManager(), "commandMap");
			SimpleCommandMap commandMap = (SimpleCommandMap) result;
			Object map = getPrivateField(commandMap, "knownCommands");
			@SuppressWarnings("unchecked")
			HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
			knownCommands.remove(cmd.getName());
			for (String alias : cmd.getAliases()) {
				if(knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(AdminAid.getPlugin().getName())) {
					knownCommands.remove(alias);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
