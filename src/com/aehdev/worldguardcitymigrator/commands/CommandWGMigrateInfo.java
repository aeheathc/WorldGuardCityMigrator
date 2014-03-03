package com.aehdev.worldguardcitymigrator.commands;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.Search;
import com.aehdev.worldguardcitymigrator.Selection;
import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;
import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * List everything WorldGuardCityMigrator knows about this region including time left on current rental and its interpretation of the flags.
 */
public class CommandWGMigrateInfo extends Command
{
	/**
	 * Create a new info order.
	 * @param plugin
	 * reference to the main plugin object
	 * @param commandLabel
	 * command name/alias
	 * @param sender
	 * who sent the command
	 * @param args
	 * arguments to the subcommand
	 */
	public CommandWGMigrateInfo(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Execute info order -- show the region's information.
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
		
		if(args.length > 1 || (args.length==1 && args[0].trim().length()<1))
		{
			sender.sendMessage(CommandWGMigrateHelp.HELP_INFO);
			return false;
		}
		
		World world = (World)Bukkit.getWorlds().toArray()[0];
		if(player != null) world = player.getWorld();
		RegionManager wg = WorldGuardCityMigrator.worldguard.get(world);
		ProtectedRegion region = null;
		if(args.length == 1)
		{
			String regionName = args[0].trim();
			region = wg.getRegion(regionName);
			if(region == null)
			{
				sender.sendMessage("No region by that name in this world.");
				return false;
			}
		}else{
			Selection sel = WorldGuardCityMigrator.selections.get(playerName);
			if(sel == null || sel.regionName == null)
			{
				sender.sendMessage("No region specified or selected.");
				return false;
			}
			world = sel.world;
			wg = WorldGuardCityMigrator.worldguard.get(world);
			region = wg.getRegion(sel.regionName);
		}
		
		String regionName = region.getId();
		String worldName = world.getName();
		
		LinkedList<String> msg = new LinkedList<String>();
		msg.add(String.format((Locale)null, "%sRegion: %s%s %sIn world: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, regionName, ChatColor.DARK_AQUA, ChatColor.WHITE, worldName));
		msg.add(String.format((Locale)null, "%sVolume: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, region.volume()));
		List<ProtectedRegion> contained = null;
		try{ contained = Search.containedRegions(region, world); }catch(UnsupportedIntersectionException e){}
		if(contained == null)
		{
			msg.add(String.format((Locale)null, "%sWorldGuard reported invalid intersections and couldn't give contained region count.",ChatColor.DARK_AQUA));
		}else{
			msg.add(String.format((Locale)null, "%sContained Regions: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, contained.size()));
		}
		
		sender.sendMessage(msg.toArray(new String[]{}));
		return true;
	}
}
