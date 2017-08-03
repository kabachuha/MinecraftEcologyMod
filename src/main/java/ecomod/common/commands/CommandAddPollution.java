package ecomod.common.commands;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
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
		
		if(w == null || w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		if(EcologyMod.ph.threads.containsKey(key))
		{
			WorldProcessingThread wpt = EcologyMod.ph.threads.get(key);
			
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
				sender.sendMessage(new TextComponentString("Invalid command format!"));
				return;
			}
			
			EcomodAPI.emitPollution(w, Pair.of(x, z), new PollutionData().add(PollutionType.valueOf(args[0]), Double.parseDouble(args[1])), true);
		}

	}

}
