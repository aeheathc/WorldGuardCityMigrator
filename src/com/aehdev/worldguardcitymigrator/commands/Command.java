package com.aehdev.worldguardcitymigrator.commands;

import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;

/**
 * Represents a WorldGuardCityMigrator command. That is, each object is a sub-command of
 * /wgmigrate. For example, "/wgmigrate city"
 */
public abstract class Command
{
	/** Reference to the main plugin object. */
	protected WorldGuardCityMigrator plugin = null;
	
	/** The sender/origin of the command. We will almost always need this to be
	 * a player, especially for commands that aren't read-only. */
	protected CommandSender sender = null;

	/** The command arguments. */
	protected String[] args = null;

	/** The logging object with which we write to the server log. */
	protected static final Logger log = Logger.getLogger("Minecraft");

	/**
	 * Define a new command.
	 * @param plugin
	 * Reference back to main plugin object.
	 * @param commandLabel
	 * Alias typed by user for this command.
	 * @param sender
	 * Who sent the command. Should be a player, but might be console.
	 * @param args
	 * Arguments passed to the command.
	 */
	public Command(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		this.plugin = plugin;
		this.sender = sender;
		this.args = args;
	}

	/**
	 * Run the command.
	 * @return true, if successful
	 */
	public abstract boolean process();
}
