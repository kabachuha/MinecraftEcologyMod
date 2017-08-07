package ecomod.common.commands;

import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.core.EcologyMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSaveManager extends CommandBase {

	@Override
	public String getName() 
	{
		return "savePollutionManager";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/savePollutionManager {dimension} - save the dimension's pollution manager to its default file";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		int dim = 0;
		
		if(args.length > 0 && args[0] != null)
		{
			try
			{
				dim = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e)
			{
				throw new CommandException("Unable to parse the argument!", args[0]);
			}
		}
		else
		{
			if(sender != null && sender.getEntityWorld() != null)
			{
				dim = sender.getEntityWorld().provider.getDimension();
			}
		}
		
		WorldProcessingThread wpt = EcologyMod.ph.getWPT(server.getWorldName()+"_"+dim);
		
		wpt.slp(10);
		wpt.getPM().save();
	}

}
