package ecomod.common.commands;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.common.utils.EMUtils;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.core.EcologyMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandGetPollution extends CommandBase {

	@Override
	public String getCommandName()
	{
		return "getPollution";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/getPollution <chunkX> <chunkZ>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException 
	{
		World w = sender.getEntityWorld();
		
		if(w == null || w.isRemote)return;
		
		
		if(args.length == 2)
		{	
			sender.addChatMessage(new ChatComponentText(EcomodAPI.getPollution(w, Integer.parseInt(args[0]), Integer.parseInt(args[1])).toString()));
		}
		else if (args.length == 0)
		{
			sender.addChatMessage(new ChatComponentText(EcomodAPI.getPollution(w, EMUtils.blockPosToPair(new EMBlockPos(sender.getPlayerCoordinates())).getLeft(), EMUtils.blockPosToPair(new EMBlockPos(sender.getPlayerCoordinates())).getRight()).toString()));
		}
		else
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
	}

}
