package com.aehdev.worldguardcitymigrator.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;

/**
 * Cancel all selections for migration.
 */
public class CommandWGMigrateCancel extends Command
{
	/**
	 * Create a new cancel order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateCancel(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Execute cancel order.
	 */
	public boolean process()
	{
		String playerName = null;
		Player player = null;
		if(sender instanceof Player)
		{
			player = (Player)sender;
			if(!player.hasPermission("worldguardcitymigrator.use"))
			{
				sender.sendMessage("You do not have permission to use WorldGuardCityMigrator.");
				return false;
			}
			playerName = player.getName();
		}
		
		if(args.length != 0)
		{
			sender.sendMessage(CommandWGMigrateHelp.HELP_CANCEL);
			return false;
		}
		
		if(WorldGuardCityMigrator.selections.containsKey(playerName))
		{
			WorldGuardCityMigrator.selections.remove(playerName);
			sender.sendMessage("Selection cleared.");
		}else{
			sender.sendMessage("No selection.");
		}
		
		return true;
	}
}
