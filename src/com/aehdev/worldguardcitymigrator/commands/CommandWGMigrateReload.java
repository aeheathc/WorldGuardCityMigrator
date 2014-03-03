package com.aehdev.worldguardcitymigrator.commands;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;

/**
 * Command that reloads the plugin.
 */
public class CommandWGMigrateReload extends Command
{
	/**
	 * Create a new reloading order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateReload(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Run version command; display the version info to the sender.
	 */
	public boolean process()
	{
		if((sender instanceof Player) && !((Player)sender).hasPermission("worldguardcitymigrator.reload"))
		{
			sender.sendMessage("You do not have permission to reload WorldGuard City Migrator");
			return false;
		}
		log.info(String.format((Locale)null,"[%s] Starting reload", plugin.getDescription().getName()));

		plugin.onDisable();
		plugin.onEnable();
		sender.sendMessage("WorldGuardCityMigrator reloaded");
		log.info(String.format((Locale)null,"[%s] Reload finished.", plugin.getDescription().getName()));
		return true;
	}
}
