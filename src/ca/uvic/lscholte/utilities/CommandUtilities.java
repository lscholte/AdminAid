package ca.uvic.lscholte.utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
 
	public static void unregisterBukkitCommand(Plugin plugin, PluginCommand cmd) {
		try {
			Object result = getPrivateField(Bukkit.getServer().getPluginManager(), "commandMap");
			SimpleCommandMap commandMap = (SimpleCommandMap) result;
			Object map = getPrivateField(commandMap, "knownCommands");
			@SuppressWarnings("unchecked")
			HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
			knownCommands.remove(cmd.getName());
			for (String alias : cmd.getAliases()) {
				if(knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(plugin.getName())) {
					knownCommands.remove(alias);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void giveCommandPriority(AdminAid plugin, CommandExecutor executor, String name) {
		//get unmodified command
		PluginCommand unmodified = Bukkit.getPluginCommand(name);
		
		if(!unmodified.getPlugin().getName().equalsIgnoreCase(plugin.getName())) {
			//get AdminAid command
			PluginCommand com = plugin.getCommand(name);
			
			//get plugin associated with unmodified command
			JavaPlugin p = (JavaPlugin) unmodified.getPlugin(); //is this a safe cast?
			
			//get executor associated with unmodified command
			CommandExecutor ex = unmodified.getExecutor();
			
			//unregister unmodified command
			CommandUtilities.unregisterBukkitCommand(p, unmodified);
			
			//unregister AdminAid version of command
			CommandUtilities.unregisterBukkitCommand(plugin, com);
			
			//register AdminAid version of command (giving it the unmodified version)
			CommandUtilities.registerBukkitCommand(plugin, name);
			
			//register other plugin's version of command
			CommandUtilities.registerBukkitCommand(p, name);
			
			p.getCommand(name).setExecutor(ex);
		}
		plugin.getCommand(name).setExecutor(executor);
	}
	
	public static void registerBukkitCommand(Plugin plugin, String name) {
		try {
			Object result = getPrivateField(Bukkit.getServer().getPluginManager(), "commandMap");
			SimpleCommandMap commandMap = (SimpleCommandMap) result;
			
			String alias = name.toLowerCase();
			
			Class<PluginCommand> clazz = PluginCommand.class;
			Constructor<PluginCommand> con = clazz.getDeclaredConstructor(String.class, Plugin.class);
			con.setAccessible(true);
			PluginCommand cmd = con.newInstance(alias, plugin);
			con.setAccessible(false);
			
			commandMap.register(plugin.getDescription().getName(), cmd);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
