package com.aehdev.worldguardcitymigrator.commands;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aehdev.worldguardcitymigrator.Point;
import com.aehdev.worldguardcitymigrator.Search;
import com.aehdev.worldguardcitymigrator.Selection;
import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;
import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

/**
 * Show everything you have input for the current migration, indicating if it is enough to do the migration yet. Also provides some hints about the proposed migration.
 */
public class CommandWGMigrateStatus extends Command
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
	public CommandWGMigrateStatus(WorldGuardCityMigrator plugin, CommandSender sender, String[] args)
	{
		super(plugin, sender, args);
	}

	/**
	 * Execute status order -- show the selection's information.
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
			sender.sendMessage(WorldGuardCityMigrator.CHAT_PREFIX + ChatColor.DARK_AQUA + "Nothing selected.");
			return false;
		}

		boolean canMigrate = true;
		LinkedList<String> msg = new LinkedList<String>();
		String around = "";
		Point pivot = null;
		ProtectedRegion region = null;
		String worldName = null;
		World world = null;
		RegionManager wg = null;
		Point finalDeltaPoint = null;
		if(sel.regionName == null)
		{
			canMigrate = false;
			msg.add(String.format((Locale)null, "%sRegion: %s(none)", ChatColor.DARK_AQUA, ChatColor.RED));
		}else{
			worldName = sel.world.getName();
			world = Bukkit.getWorld(worldName);
			wg = WorldGuardCityMigrator.worldguard.get(world);
			region = wg.getRegion(sel.regionName);
			String invalid = "";
			if(region == null)
			{
				canMigrate = false;
				invalid = "[Doesn't Exist]";
			}
			msg.add(String.format((Locale)null, "%sRegion: %s%s %sIn world: %s%s %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, sel.regionName, ChatColor.DARK_AQUA, ChatColor.WHITE, sel.world.getName(), ChatColor.RED, invalid));
			if(region != null)
			{
				msg.add(String.format((Locale)null, "%sVolume: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, region.volume()));
				List<ProtectedRegion> contained = null;
				try{ contained = Search.containedRegions(region, world); }catch(UnsupportedIntersectionException e){}
				if(contained == null)
				{
					msg.add(String.format((Locale)null, "%sContained Regions: %s[WorldGuard Error - Invalid Overlap]", ChatColor.DARK_AQUA, ChatColor.RED));
				}else{
					msg.add(String.format((Locale)null, "%sContained Regions: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, contained.size()));
				}
			}
			if(sel.from == null)
			{
				if(region == null)
				{
					around = "";
				}else{
					pivot = Search.center(region);
					around = String.format((Locale)null, "around %s[CalcCenter]%s: %s", ChatColor.WHITE, ChatColor.DARK_AQUA, pivot);
				}
			}else{
				around = String.format((Locale)null, "around %s'From' point", ChatColor.WHITE);
				pivot = sel.from;
			}
		}
		msg.add(String.format((Locale)null, "%sFrom: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, (sel.from == null ? "(none)" : sel.from)));
		msg.add(String.format((Locale)null, "%sTo: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, (sel.to == null ? "(none)" : sel.to)));
		String finalDelta = "(none)";
		String deltaCalc = "";
		if(sel.from != null && sel.to != null)
		{
			finalDeltaPoint = sel.calcDelta();
			finalDelta = finalDeltaPoint.toString();
			deltaCalc = "[calculated]";
		}else if(sel.delta != null){
			finalDeltaPoint = sel.delta;
			finalDelta = finalDeltaPoint.toString();				
		}
		msg.add(String.format((Locale)null, "%sDelta: %s%s %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, finalDelta, ChatColor.LIGHT_PURPLE, deltaCalc));
		if(sel.delta != null || (sel.from != null && sel.to != null))
		{
			if(sel.from!=null && sel.to!=null && sel.delta!=null)
				msg.add(String.format((Locale)null, "%s[Input delta ignored -- calculated using From/To points]", ChatColor.LIGHT_PURPLE));
		}else{
			canMigrate = false;
			msg.add(String.format((Locale)null, "%s[Must define (From and To points) OR (a Delta)]", ChatColor.RED));
		}
		msg.add(String.format((Locale)null, "%sRotation: %s%s %sdeg. %s", ChatColor.DARK_AQUA, ChatColor.WHITE, sel.rotation, ChatColor.DARK_AQUA, around));
		ProtectedRegion proposed = null;
		if(canMigrate)
		{
			try
			{
				proposed = Search.migrate(region, finalDeltaPoint, sel.rotation, pivot);
			}catch(CircularInheritanceException e1){
				msg.add(String.format((Locale)null, "%s[Region has circular inheritance]", ChatColor.RED));
				canMigrate = false;
			}
		}
		if(canMigrate == false)
		{
			msg.add(String.format((Locale)null, "%s[Solve above issues in red before continuing]", ChatColor.RED));
		}else{
			List<ProtectedRegion> containingCurrent = null, containingProposed = null;
			try{ containingCurrent = Search.containingRegions(region, world); }catch(UnsupportedIntersectionException e){}
			if(containingCurrent == null)
			{
				msg.add(String.format((Locale)null, "%sCurrent Containing Regions: %s[WorldGuard Error - Invalid Overlap]", ChatColor.DARK_AQUA, ChatColor.RED));
			}else{
				msg.add(String.format((Locale)null, "%sCurrent Containing Regions: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, containingCurrent.size()));
			}
			try{ containingProposed = Search.containingRegions(proposed, world); }catch(UnsupportedIntersectionException e){}
			if(containingProposed == null)
			{
				msg.add(String.format((Locale)null, "%sProposed Containing Regions: %s[WorldGuard Error - Invalid Overlap]", ChatColor.DARK_AQUA, ChatColor.RED));
			}else{
				msg.add(String.format((Locale)null, "%sProposed Containing Regions: %s%s", ChatColor.DARK_AQUA, ChatColor.WHITE, containingProposed.size()));
			}
			msg.add(String.format((Locale)null, "%sSelection ok. Use %s/wgmigrate process %sto do the migration.", ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.DARK_AQUA));
		}
		
		sender.sendMessage(msg.toArray(new String[]{}));
		return true;
	}
}
