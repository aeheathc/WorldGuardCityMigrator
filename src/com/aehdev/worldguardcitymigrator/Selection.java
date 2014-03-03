package com.aehdev.worldguardcitymigrator;

import org.bukkit.World;

/**
 * Stores a player's current selection info regarding a migration they are preparing to make.
 */
public class Selection
{
	
	/** The WorldGuard region to move. */
	public String regionName = null;
	
	/** The world of the region. */
	public World world = null;
	
	/** Reference point to move "from" in calculating delta. */
	public Point from = null;
	
	/** Reference point to move "to" in calculating delta. */
	public Point to = null;
	
	/** How far to move. */
	public Point delta = null;
	
	/** Degrees to rotate. */
	public int rotation = 0;
	
	/**
	 * Instantiates a new selection.
	 */
	public Selection()
	{
		
	}
	
	
	/**
	 * Returns a calculated delta based on the to/from and ignoring the specified delta
	 * 
	 * @return the delta, or null if either reference point is null
	 */
	public Point calcDelta()
	{
		if(to == null || from == null) return null;
		return new Point(to.x-from.x, to.y-from.y, to.z-from.z);
	}
}