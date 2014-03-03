package com.aehdev.worldguardcitymigrator.commands;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.Action;
import com.aehdev.worldguardcitymigrator.Point;
import com.aehdev.worldguardcitymigrator.Search;
import com.aehdev.worldguardcitymigrator.Selection;
import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;
import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

/**
 * Do the migration.
 */
public class CommandWGMigrateProcess extends Command
{
	/**
	 * Create a new status order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateProcess(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Execute process order -- do the migration as given in the selection.
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
			sender.sendMessage(CommandWGMigrateHelp.HELP_STATUS);
			return false;
		}
		Selection sel = WorldGuardCityMigrator.selections.get(playerName);
		if(sel == null)
		{
			sender.sendMessage("Nothing selected.");
			return false;
		}

		Point around = null;
		ProtectedRegion region = null;
		String worldName = null;
		World world = null;
		
		
		if(sel.regionName == null)
		{
			sender.sendMessage("No region selected.");
			return false;
		}
		worldName = sel.world.getName();
		world = Bukkit.getWorld(worldName);
		RegionManager wg = WorldGuardCityMigrator.worldguard.get(world);
		region = wg.getRegion(sel.regionName);
		if(region == null)
		{
			sender.sendMessage("Selected region doesn't exist.");
			return false;
		}
		
		if(region.getTypeName().contains("global"))
		{
			sender.sendMessage("Can't migrate global region");
			return false;
		}

		List<ProtectedRegion> contained = null;
		try{ contained = Search.containedRegions(region, world); }catch(UnsupportedIntersectionException e){}
		if(contained == null)
		{
			sender.sendMessage("Worldguard error: Invalid Overlap. Migration aborted.");
			return false;
		}
		if(sel.from == null)
		{
			around = Search.center(region);
		}else{
			around = sel.from;
		}

		Point finalDeltaPoint = null;
		if(sel.from != null && sel.to != null)
		{
			finalDeltaPoint = sel.calcDelta();
		}else if(sel.delta != null){
			finalDeltaPoint = sel.delta;
		}else{
			sender.sendMessage("Must define (From and To points) OR (a Delta)");
			return false;
		}

		LinkedList<ProtectedRegion> undo = new LinkedList<ProtectedRegion>();
		List<ProtectedRegion> results = new LinkedList<ProtectedRegion>();
		try{
			results.add(Search.migrate(region, finalDeltaPoint, sel.rotation, around));
			undo.add(region);
			for(ProtectedRegion con : contained)
			{
				results.add(Search.migrate(con, finalDeltaPoint, sel.rotation, around));
				undo.add(con);
			}
		}catch(CircularInheritanceException e){
			sender.sendMessage("Worldguard error: circular inheritance. Migration aborted.");
			return false;
		}

		for(ProtectedRegion res : results)
		{
			//In theory what we're doing here is replacing an existing region with its updated copy, not adding a new region.
			wg.addRegion(res);
		}
		
		WorldGuardCityMigrator.undo.put(playerName, new Action(undo, world));
		
		try{
			wg.save();
		}catch(ProtectionDatabaseException e){
			sender.sendMessage("Worldguard error during save. Regions were probably migrated but may revert after reloading wg or restarting the server.");
		}
		
		WorldGuardCityMigrator.selections.remove(playerName); //clear selection
		sender.sendMessage("Migration done.");
			
		return true;
	}
}
