package ecomod.common.commands;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
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
import net.minecraft.world.World;

public class CommandAddPollution extends CommandBase {

	@Override
	public String getCommandName() {
		return "addPollution";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/addPollution <type> <amount> <chunkX> <chunkZ>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		World w = sender.getEntityWorld();
		
		if(w == null || w.isRemote)return;
		
		int dim = w.provider.dimensionId;
		
		WorldProcessingThread wpt = EcologyMod.ph.getWPT(dim);
		
		if(wpt == null)
			throw new CommandException("commands.ecomod.wpt_not_found", w.provider.dimensionId);
			
		int x, z;
			
		if(args.length == 4)
		{	
			x = Integer.parseInt(args[2]);
			z = Integer.parseInt(args[3]);
		}
		else if (args.length == 2)
		{
			Pair<Integer, Integer> pb = EMUtils.blockPosToPair(new EMBlockPos(sender.getPlayerCoordinates()));
				
			x = pb.getLeft();
			z = pb.getRight();
		}
		else
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
			
		EcomodAPI.emitPollution(w, x, z, args[0].toLowerCase().contentEquals("all") ? new PollutionData().addAll(Float.parseFloat(args[1])) : new PollutionData().add(PollutionType.valueOf(args[0]), Float.parseFloat(args[1])), true);
	}

}
