package com.aehdev.worldguardcitymigrator.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.Action;
import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Undo the last action.
 */
public class CommandWGMigrateUndo extends Command
{
	/**
	 * Create a new undo order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateUndo(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Execute undo order.
	 */
	public boolean process()
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			if(!player.hasPermission("worldguardcitymigrator.use"))
			{
				sender.sendMessage("You do not have permission to use WorldGuardCityMigrator.");
				return false;
			}
		}
		String playerName = sender.getName();
		
		if(args.length != 0)
		{
			sender.sendMessage(CommandWGMigrateHelp.HELP_UNDO);
			return false;
		}
		
		Action undo = WorldGuardCityMigrator.undo.get(playerName);
		if(undo == null)
		{
			sender.sendMessage("Nothing to undo.");
			return false;
		}
		RegionManager wg = WorldGuardCityMigrator.worldguard.get(undo.getWorld());
		WorldGuardCityMigrator.selections.remove(playerName);
		
		for(ProtectedRegion res : undo.getRegions())
		{
			//Overwrite the modified regions with the original regions
			wg.addRegion(res);
		}
		
		try{
			wg.save();
		}catch(ProtectionDatabaseException e){
			sender.sendMessage("Worldguard error during save. Last migration was probably undone but may revert after reloading wg or restarting the server. If you also got this error when doing the migration the two errors probably cancel each other out.");
		}
		
		WorldGuardCityMigrator.undo.remove(playerName);
		sender.sendMessage("Last migration undone.");
		
		return true;
	}
}
