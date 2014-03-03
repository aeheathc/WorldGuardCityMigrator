package com.aehdev.worldguardcitymigrator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.World;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

/**
 * Search utility functions.
 */
public class Search
{
	/** The logging object with which we write to the server log. */
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	/**
	 * Get all regions contained within this one.
	 * 
	 * @param region the region
	 * @param world the world
	 * @return a list of contained regions
	 * @throws UnsupportedIntersectionException Thrown if WorldGuard complains about invalid data while looking up intersecting regions
	 */
	public static List<ProtectedRegion> containedRegions(ProtectedRegion region, World world) throws UnsupportedIntersectionException
	{
		RegionManager wg = WorldGuardCityMigrator.worldguard.get(world);
		//get INTERSECTING regions
		List<ProtectedRegion> intersect = region.getIntersectingRegions(new ArrayList<ProtectedRegion>(wg.getRegions().values()));
		
		//remove regions from the list which are not fully contained
		Iterator<ProtectedRegion> itr = intersect.iterator();
		while(itr.hasNext())
		{
			ProtectedRegion ir = itr.next();
			//check if it's the same region
			if(ir.equals(region))
			{
				itr.remove();
				continue;
			}			
			//check height
			if(ir.getMaximumPoint().getBlockY() > region.getMaximumPoint().getBlockY())
			{
				itr.remove();
				continue;
			}
			if(ir.getMinimumPoint().getBlockY() < region.getMinimumPoint().getBlockY())
			{
				itr.remove();
				continue;
			}
			//check 2d containment
			for(BlockVector2D point : ir.getPoints())
			{
				if(!region.contains(point))
				{
					itr.remove();
					break;
				}
			}
		}
		return intersect;
	}
	
	/**
	 * Get all regions containing this one.
	 * 
	 * @param region the region
	 * @param world the world
	 * @return a list of containing regions
	 * @throws UnsupportedIntersectionException Thrown if WorldGuard complains about invalid data while looking up intersecting regions
	 */
	public static List<ProtectedRegion> containingRegions(ProtectedRegion region, World world) throws UnsupportedIntersectionException
	{
		RegionManager wg = WorldGuardCityMigrator.worldguard.get(world);
		//get INTERSECTING regions
		List<ProtectedRegion> intersect = region.getIntersectingRegions(new ArrayList<ProtectedRegion>(wg.getRegions().values()));
		
		//remove regions from the list which are not fully containing
		Iterator<ProtectedRegion> itr = intersect.iterator();
		while(itr.hasNext())
		{
			ProtectedRegion ir = itr.next();
			//check if it's the same region
			if(ir.equals(region))
			{
				itr.remove();
				continue;
			}	
			//check height
			if(ir.getMaximumPoint().getBlockY() < region.getMaximumPoint().getBlockY())
			{
				itr.remove();
				continue;
			}
			if(ir.getMinimumPoint().getBlockY() > region.getMinimumPoint().getBlockY())
			{
				itr.remove();
				continue;
			}
			//check 2d containment
			for(BlockVector2D point : region.getPoints())
			{
				if(!ir.contains(point))
				{
					itr.remove();
					break;
				}
			}
		}
		return intersect;
	}
	

	/**
	 * Calculate the center block of a region.
	 * 
	 * @param region the region
	 * @return the center
	 */
	public static Point center(ProtectedRegion region)
	{
		long x_max = Long.MIN_VALUE;
		long x_min = Long.MAX_VALUE;
		int y_max = region.getMaximumPoint().getBlockY();
		int y_min = region.getMinimumPoint().getBlockY();
		long z_max = Long.MIN_VALUE;
		long z_min = Long.MAX_VALUE;
		List<BlockVector2D> points = region.getPoints();
		for(BlockVector2D pt : points)
		{
			int x = pt.getBlockX();
			int z = pt.getBlockZ();
			if(x < x_min) x_min = x;
			if(x > x_max) x_max = x;
			if(z < z_min) z_min = z;
			if(z > z_max) z_max = z;
		}
		return new Point((x_max + x_min)/2, (y_max + y_min)/2, (z_max + z_min)/2);
	}
	
	/**
	 * Rotate a point around an arbitrary pivot. The rotation only takes place along the compass axes and the height is preserved.
	 * 
	 * @param pt the point to be rotated
	 * @param degrees how far to rotate -- only recognizes 0,90,180,270 degrees. Other values assumed equal to 0.
	 * @param pivot the pivot point
	 * @return the resulting location
	 */
	public static BlockVector rotate(BlockVector pt, int degrees, Point pivot)
	{
		long x = pt.getBlockX() - pivot.x;
		long y = pt.getBlockY();
		long z = pt.getBlockZ() - pivot.z;
		BlockVector out = null;
		switch(degrees)
		{
			case 90:	out = new BlockVector(-z,y,x);	break;
			case 180:	out = new BlockVector(-x,y,-z); break;
			case 270:	out = new BlockVector(z,y,-x);	break;
			
			case 0:
			default:
			out = new BlockVector(x,y,z);
		}
		return out.add(pivot.x, 0, pivot.z).toBlockVector();
	}
	
	/**
	 * Rotate a point around an arbitrary pivot.
	 * 
	 * @param pt the point to be rotated
	 * @param degrees how far to rotate -- only recognizes 0,90,180,270 degrees. Other values assumed equal to 0.
	 * @param pivot the pivot point
	 * @return the resulting location
	 */
	public static BlockVector2D rotate(BlockVector2D pt, int degrees, Point pivot)
	{
		BlockVector out = rotate(new BlockVector(pt.getBlockX(),0,pt.getBlockZ()), degrees, pivot);
		return new BlockVector2D(out.getBlockX(), out.getBlockZ());
	}
	
	/**
	 * Return a new region resulting from applying the given transforms to the given source region.
	 * This only provides a region object, no changes are made to WorldGuard.
	 * 
	 * @param region the region
	 * @param delta the delta
	 * @param rotation the rotation
	 * @param pivot the pivot
	 * @return the protected region
	 * @throws CircularInheritanceException if worldguard throws a fit about something
	 */
	public static ProtectedRegion migrate(ProtectedRegion region, Point delta, int rotation, Point pivot) throws CircularInheritanceException
	{
		ProtectedRegion proposed;
		if(region.getTypeName().contains("cuboid"))
		{
			BlockVector b1 = Search.rotate(region.getMinimumPoint(), rotation, pivot);
			BlockVector b2 = Search.rotate(region.getMaximumPoint(), rotation, pivot);
			b1 = b1.add(delta.x, delta.y, delta.z).toBlockVector();
			b2 = b2.add(delta.x, delta.y, delta.z).toBlockVector();
			proposed = new ProtectedCuboidRegion(region.getId(),b1,b2);
		}else{ //poly
			List<BlockVector2D> pts = region.getPoints();
			List<BlockVector2D> out = new LinkedList<BlockVector2D>();
			for(BlockVector2D pt : pts)
			{
				BlockVector2D ptp = Search.rotate(pt, rotation, pivot);
				ptp = ptp.add(delta.x, delta.z).toBlockVector2D();
				out.add(ptp);
			}
			int min = region.getMinimumPoint().getBlockY() + delta.y;
			int max = region.getMaximumPoint().getBlockY() + delta.y;
			proposed = new ProtectedPolygonalRegion(region.getId(), out, min, max);
		}
		proposed.setFlags(region.getFlags());
		proposed.setMembers(region.getMembers());
		proposed.setOwners(region.getOwners());
		proposed.setParent(region.getParent());
		proposed.setPriority(region.getPriority());
		
		return proposed;
	}
}
