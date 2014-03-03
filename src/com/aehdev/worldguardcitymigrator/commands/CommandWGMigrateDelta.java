package com.aehdev.worldguardcitymigrator.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.Point;
import com.aehdev.worldguardcitymigrator.Selection;
import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;

/**
 * Selects a Delta for migration.
 */
public class CommandWGMigrateDelta extends Command
{
	/**
	 * Create a new Delta selection order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateDelta(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Execute Delta select order.
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
		
		if(args.length != 3)
		{
			sender.sendMessage(CommandWGMigrateHelp.HELP_DELTA);
			return false;
		}
		int x,y,z;
		try{
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			z = Integer.parseInt(args[2]);
		}catch(NumberFormatException e){
			sender.sendMessage(CommandWGMigrateHelp.HELP_DELTA);
			return false;
		}
		
		Selection sel = WorldGuardCityMigrator.selections.get(playerName);
		if(sel == null)
		{
			sel = new Selection();
			sel.delta = new Point(x,y,z);
			WorldGuardCityMigrator.selections.put(playerName, sel);
		}else{
			sel.delta = new Point(x,y,z);
		}
		sender.sendMessage("Delta selected.");
		
		return true;
	}
}
