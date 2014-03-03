package com.aehdev.worldguardcitymigrator.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.Selection;
import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Selects a region for migration.
 */
public class CommandWGMigrateRegion extends Command
{
	/**
	 * Create a new region selection order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateRegion(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Execute region select order.
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
		
		if(args.length != 1 || args[0].trim().length()<1)
		{
			sender.sendMessage(CommandWGMigrateHelp.HELP_REGION);
			return false;
		}
		
		World world = (World)Bukkit.getWorlds().toArray()[0];
		if(player != null) world = player.getWorld();
		RegionManager wg = WorldGuardCityMigrator.worldguard.get(world);
		ProtectedRegion region = null;
		String regionName = args[0].trim();
		region = wg.getRegion(regionName);
		if(region == null)
		{
			sender.sendMessage("No region by that name in this world.");
			return false;
		}
		
		Selection sel = WorldGuardCityMigrator.selections.get(playerName);
		if(sel == null)
		{
			sel = new Selection();
			sel.regionName = regionName;
			sel.world = world;
			WorldGuardCityMigrator.selections.put(playerName, sel);
		}else{
			sel.regionName = regionName;
			sel.world = world;
		}
		sender.sendMessage("Region selected.");
		
		return true;
	}
}
