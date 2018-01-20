package ecomod.common.commands;

import ecomod.api.EcomodAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandGetPollution extends CommandBase {

	@Override
	public String getName()
	{
		return "getPollution";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/getPollution <chunkX> <chunkZ>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		World w = server.getEntityWorld();
		
		if(w.isRemote)return;


		if(args.length == 2)
		{
			sender.sendMessage(new TextComponentString(EcomodAPI.getPollution(w, Integer.parseInt(args[0]), Integer.parseInt(args[1])).toString()));
		}
		else if (args.length == 0)
		{
			sender.sendMessage(new TextComponentString(EcomodAPI.getPollution(w, sender.getPosition().getX() >> 4, sender.getPosition().getZ() >> 4).toString()));
		}
		else
		{
			throw new WrongUsageException(getUsage(sender));
		}
	}

}
