package com.aehdev.worldguardcitymigrator.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.Selection;
import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;

/**
 * Selects a rotation for migration.
 */
public class CommandWGMigrateRotation extends Command
{
	/**
	 * Create a new rotation selection order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateRotation(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Execute rotation select order.
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
		
		if(args.length != 1)
		{
			sender.sendMessage(CommandWGMigrateHelp.HELP_DELTA);
			return false;
		}
		int r;
		try{
			r = Integer.parseInt(args[0]);
		}catch(NumberFormatException e){
			sender.sendMessage(CommandWGMigrateHelp.HELP_DELTA);
			return false;
		}
		//normalize to 0,90,180,270
		int ticks = r / 90;
		ticks = ticks % 4;
		r = ticks * 90;
		if(r<0) r += 360;
		
		Selection sel = WorldGuardCityMigrator.selections.get(playerName);
		if(sel == null)
		{
			sel = new Selection();
			sel.rotation = r;
			WorldGuardCityMigrator.selections.put(playerName, sel);
		}else{
			sel.rotation = r;
		}
		sender.sendMessage("Rotation amount selected.");
		
		return true;
	}
}
