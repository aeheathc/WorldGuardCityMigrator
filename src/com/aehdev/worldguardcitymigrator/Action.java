package com.aehdev.worldguardcitymigrator;

import java.util.LinkedList;

import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Stores enough info about a migration to undo it.
 */
public class Action
{
	
	/** The regions that were replaced by this migration. */
	private LinkedList<ProtectedRegion> priorRegions;
	
	/** The world in which the migration took place. */
	private World world;
	
	/**
	 * Instantiates an action record.
	 * 
	 * @param priorRegions The regions that were replaced by this migration.
	 * @param world The world in which the migration took place.
	 */
	public Action(LinkedList<ProtectedRegion> priorRegions, World world)
	{
		this.world = world;
		this.priorRegions = priorRegions;
	}
	
	/**
	 * Gets the regions that were replaced by this migration.
	 * 
	 * @return the regions
	 */
	public LinkedList<ProtectedRegion> getRegions()
	{
		return priorRegions;
	}
	
	/**
	 * Gets the world in which the migration took place.
	 * 
	 * @return the world
	 */
	public World getWorld()
	{
		return world;
	}
}