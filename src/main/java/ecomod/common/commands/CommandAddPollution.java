package ecomod.common.commands;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class CommandAddPollution extends CommandBase {

	@Override
	public String getName() {
		return "addPollution";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/addPollution <type> <amount> <chunkX> <chunkZ>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World w = server.getEntityWorld();
		
		if(w.isRemote)return;
		
		WorldProcessingThread wpt = EcologyMod.ph.getWPT(w);
			
		if(wpt == null)
			throw new CommandException("commands.ecomod.wpt_not_found", w.provider.getDimension());
			
		int x, z;
			
		if(args.length == 4)
		{	
			x = Integer.parseInt(args[2]);
			z = Integer.parseInt(args[3]);
		}
		else if (args.length == 2)
		{
			Pair<Integer, Integer> pb = EMUtils.blockPosToPair(sender.getPosition());
				
			x = pb.getLeft();
			z = pb.getRight();
		}
		else
		{
				throw new WrongUsageException(getUsage(sender));
		}
			
		EcomodAPI.emitPollution(w, Pair.of(x, z), args[0].toLowerCase().contentEquals("all") ? new PollutionData().addAll(Float.parseFloat(args[1])) : new PollutionData().add(PollutionType.valueOf(args[0]), Float.parseFloat(args[1])), true);

	}

}
