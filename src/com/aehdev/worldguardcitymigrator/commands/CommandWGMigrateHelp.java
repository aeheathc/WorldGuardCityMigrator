package com.aehdev.worldguardcitymigrator.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;

/**
 * Command that shows syntax for all commands.
 */
public class CommandWGMigrateHelp extends Command
{
	/**
	 * Create a new help request.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateHelp(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Run the request and display the help.
	 */
	public boolean process()
	{
		sender.sendMessage(WorldGuardCityMigrator.CHAT_PREFIX + ChatColor.DARK_AQUA + "Available commands [required] <optional>");
		
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		
		sender.sendMessage(HELP_HELP);
		sender.sendMessage(HELP_VERSION);
		if(player == null || player.hasPermission("worldguardcitymigrator.use"))
		{
			sender.sendMessage(HELP_INFO);
			sender.sendMessage(HELP_REGION);
			sender.sendMessage(HELP_FROM);
			sender.sendMessage(HELP_TO);
			sender.sendMessage(HELP_DELTA);
			sender.sendMessage(HELP_ROTATION);
			sender.sendMessage(HELP_STATUS);
			sender.sendMessage(HELP_PROCESS);
			sender.sendMessage(HELP_UNDO);
			sender.sendMessage(HELP_CANCEL);
		}
		if(player == null || player.hasPermission("worldguardcitymigrator.use")) sender.sendMessage(HELP_RELOAD);
		
		return true;
	}
	
	public static String HELP_HELP		= ChatColor.WHITE + "/wgmigrate " + ChatColor.DARK_AQUA + "- Show this help.";
	public static String HELP_INFO		= ChatColor.WHITE + "/wgmigrate info <region> " + ChatColor.DARK_AQUA + "- Show region's migratability info.";
	public static String HELP_REGION	= ChatColor.WHITE + "/wgmigrate region [region] " + ChatColor.DARK_AQUA + "- Select a region to migrate.";
	public static String HELP_FROM		= ChatColor.WHITE + "/wgmigrate from <x y z> " + ChatColor.DARK_AQUA + "- Select reference point to move a region 'from'.";
	public static String HELP_TO		= ChatColor.WHITE + "/wgmigrate to <x y z> " + ChatColor.DARK_AQUA + "- Select reference point to move a region 'to'.";
	public static String HELP_DELTA		= ChatColor.WHITE + "/wgmigrate delta [x y z] " + ChatColor.DARK_AQUA + "- Specify distance to move region.";
	public static String HELP_ROTATION	= ChatColor.WHITE + "/wgmigrate rotation [degrees] " + ChatColor.DARK_AQUA + "- Specify rotation amount.";
	public static String HELP_STATUS	= ChatColor.WHITE + "/wgmigrate status " + ChatColor.DARK_AQUA + "- Describe pending migration.";
	public static String HELP_PROCESS	= ChatColor.WHITE + "/wgmigrate process " + ChatColor.DARK_AQUA + "- Do the migration.";
	public static String HELP_UNDO		= ChatColor.WHITE + "/wgmigrate undo " + ChatColor.DARK_AQUA + "- Undo the last migration.";
	public static String HELP_CANCEL	= ChatColor.WHITE + "/wgmigrate cancel " + ChatColor.DARK_AQUA + "- Clear all current parameters.";
	public static String HELP_RELOAD	= ChatColor.WHITE + "/wgmigrate reload " + ChatColor.DARK_AQUA + "- Reload the plugin.";
	public static String HELP_VERSION	= ChatColor.WHITE + "/wgmigrate version " + ChatColor.DARK_AQUA + "- Show plugin version.";
}
