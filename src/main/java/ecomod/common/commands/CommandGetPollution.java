package ecomod.common.commands;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
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
		
		if(w == null || w.isRemote)return;
		
		
		if(args.length == 2)
		{	
			sender.sendMessage(new TextComponentString(EcomodAPI.getPollution(w, Integer.parseInt(args[0]), Integer.parseInt(args[1])).toString()));
		}
		else if (args.length == 0)
		{
			sender.sendMessage(new TextComponentString(EcomodAPI.getPollution(w, EMUtils.blockPosToPair(sender.getPosition()).getLeft(), EMUtils.blockPosToPair(sender.getPosition()).getRight()).toString()));
		}
		else
		{
			sender.sendMessage(new TextComponentString("Invalid command format!"));
		}
	}

}
