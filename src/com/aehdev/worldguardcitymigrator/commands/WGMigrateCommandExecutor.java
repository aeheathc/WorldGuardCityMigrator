package com.aehdev.worldguardcitymigrator.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.aehdev.worldguardcitymigrator.WorldGuardCityMigrator;

/**
 * Engine for parsing and running all commands accepted by the plugin.
 */
public class WGMigrateCommandExecutor implements CommandExecutor
{

	/** reference to the main plugin object */
	private final WorldGuardCityMigrator plugin;

	/**
	 * Start accepting commands.
	 * @param plugin reference to the main plugin object
	 */
	public WGMigrateCommandExecutor(WorldGuardCityMigrator plugin)
	{
		this.plugin = plugin;
	}

    /**
     * Process players' commands which are sent here by Bukkit.
     * 
     * @param sender who sent the command, maybe a player
     * @param command the command name
     * @param commandLabel actual command alias that was used
     * @param args arguments to the command
     * @return true
     */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if(args.length < 1) return (new CommandWGMigrateHelp(plugin, sender, args)).process();
		
		int argsNum = args.length-1;
		String cmdArgs[] = new String[argsNum];
		System.arraycopy(args, 1, cmdArgs, 0, argsNum);

		String commandName = command.getName().toLowerCase();
		String type = args[0];
		com.aehdev.worldguardcitymigrator.commands.Command cmd = null;

		if(commandName.equalsIgnoreCase("wgmigrate"))
		{
			if(type.equalsIgnoreCase("info"))			cmd = new CommandWGMigrateInfo(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("region"))	cmd = new CommandWGMigrateRegion(plugin,  sender, cmdArgs);
			else if(type.equalsIgnoreCase("from"))		cmd = new CommandWGMigrateFrom(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("to"))		cmd = new CommandWGMigrateTo(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("delta"))		cmd = new CommandWGMigrateDelta(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("rotation"))	cmd = new CommandWGMigrateRotation(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("status"))	cmd = new CommandWGMigrateStatus(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("process"))	cmd = new CommandWGMigrateProcess(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("undo"))		cmd = new CommandWGMigrateUndo(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("cancel"))	cmd = new CommandWGMigrateCancel(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("reload"))	cmd = new CommandWGMigrateReload(plugin, sender, cmdArgs);
			else if(type.equalsIgnoreCase("version"))	cmd = new CommandWGMigrateVersion(plugin, sender, cmdArgs);
			else										cmd = new CommandWGMigrateHelp(plugin, sender, cmdArgs);

			cmd.process();
			/*
			 * We used to return a boolean indicating command success/failure, however doing so causes bukkit to display
			 * redundant command help on failure, and we want to show highly context sensitive help, so now we always
			 * return true regardless of what happened, as long as we know the sender is supposed to be here in the first place.
			 */
			return true;
		}

		return false;
	}
}
