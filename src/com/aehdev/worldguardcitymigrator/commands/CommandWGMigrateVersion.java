package com.aehdev.worldguardcitymigrator.commands;

import java.util.Locale;

import org.bukkit.command.CommandSender;

import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;

/**
 * Command that queries the plugin version info.
 */
public class CommandWGMigrateVersion extends Command
{
	/**
	 * Create a new version order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateVersion(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Run version command; display the version info to the sender.
	 */
	public boolean process()
	{
		sender.sendMessage(String.format((Locale)null,"%s Version %s", WorldGuardCityMigrator.pdfFile.getName(), plugin.getDescription().getVersion()));
		return true;
	}
}
