package com.aehdev.worldguardcitymigrator;

import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.aehdev.worldguardcitymigrator.commands.WGMigrateCommandExecutor;

/**
 * The main Bukkit plugin class for WorldGuardCityMigrator.
 */
public class WorldGuardCityMigrator extends JavaPlugin
{
	/** General plugin info that comes directly from the private
	 * {@link JavaPlugin#description} in the parent. Effectively all we're
	 * doing here is converting it to public. */
	public static PluginDescriptionFile pdfFile = null;

	/** Main logger with which we write to the server log. */
	private final Logger log = Logger.getLogger("Minecraft");

	/** Plugin-identifying string that prefixes every message we show to players */
	public static final String CHAT_PREFIX = ChatColor.DARK_AQUA + "["
			+ ChatColor.WHITE + "WGCM" + ChatColor.DARK_AQUA + "] ";

	/** Reference to WorldGuard region manager. */
	public static GlobalRegionManager worldguard = null;
	
	/** Selections currently being made by each player. */
	public static HashMap<String,Selection> selections = new HashMap<String, Selection>();
	
	/** Undo data for each player. */
	public static HashMap<String, Action> undo = new HashMap<String, Action>();

	/**
	 * Setup method for when this plugin is enabled by Bukkit
	 */
	public void onEnable()
	{
		pdfFile = getDescription();	//cache plugin info
		
		//Connect to worldguard
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if(plugin == null || !(plugin instanceof WorldGuardPlugin))
        {
            log.severe(String.format((Locale)null,"[%s] - Shutting down: Supported region plugin not found!", pdfFile.getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else{
        	worldguard = ((WorldGuardPlugin)plugin).getGlobalRegionManager();
        	log.info(String.format((Locale)null,"[%s] %s", pdfFile.getName(), "WorldGuard connected."));
        }		
        
		// Register Commands
		getCommand("wgmigrate").setExecutor(new WGMigrateCommandExecutor(this));

		// update the console that we've started
		log.info(String.format((Locale)null,"[%s] %s", pdfFile.getName(), "Version " + pdfFile.getVersion() + " is enabled: "));
	}

	/**
	 * Shut down the plugin.
	 * Called by Bukkit when the server is shutting down, plugins are being reloaded,
	 * or we voluntarily shutdown due to errors. 
	 */
	public void onDisable()
	{
		//drop Worldguard hook
		WorldGuardCityMigrator.worldguard = null;
		
		// update the console that we've stopped
		log.info(String.format((Locale)null,"[%s] %s", pdfFile.getName(), "Version " + pdfFile.getVersion() + " is disabled!"));
	}

    public static void main(String args[])
    {
    	System.out.println("This is a bukkit plugin what do you think you're doing");
    }
}
