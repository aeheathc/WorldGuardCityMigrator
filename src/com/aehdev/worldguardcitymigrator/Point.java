package com.aehdev.worldguardcitymigrator;

import java.util.Locale;

import org.bukkit.ChatColor;

/**
 * Point in 3d integer space.
 */
public class Point
{
	
	/** Coordinates of the point. */
	public long x,z;
	public int y;
	
	/**
	 * Instantiates a new point.
	 * 
	 * @param x the x value
	 * @param y the y value
	 * @param z the z value
	 */
	public Point(long x, int y, long z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString()
	{
		return String.format((Locale)null, "%s%s%s,%s%s%s,%s%s", ChatColor.WHITE, x, ChatColor.DARK_AQUA, ChatColor.WHITE, y, ChatColor.DARK_AQUA, ChatColor.WHITE, z);
	}
}