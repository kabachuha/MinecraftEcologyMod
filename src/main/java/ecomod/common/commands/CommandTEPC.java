package ecomod.common.commands;

import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.TEPollutionConfig.TEPollution;
import ecomod.core.EcologyMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

public class CommandTEPC extends CommandBase {

	@Override
	public String getName() {
		return "TEPollutionConfig";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/TEPollutionConfig [ADD|REMOVE|SAVE|LOAD|GET] [tileId] ([air] [water] [soil])";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 1)
			throw new CommandException("Invalid arguments! At least 1 has to be.");
		
		CTMode mode = CTMode.valueOf(args[0].toLowerCase());
		
		switch(mode)
		{
		case add:
			if(args.length != 5)
			{
				throw new CommandException("Invalid arguments! For ADD mode there has to be exactly 5 arguments /TEPollutionConfig ADD [tileId] [air] [water] [soil]");
			}
			
			String tileId = args[1];
			
			double air = 0.0D;
			double water = 0.0D;
			double soil = 0.0D;
			
			air = Double.parseDouble(args[2]);
			water = Double.parseDouble(args[3]);
			soil = Double.parseDouble(args[4]);
			
			TEPollution tep = null;
			
			if(tileId.length() > 0 && !(air == 0 && water == 0 && soil == 0))
			{
				tep = new TEPollution(tileId, new PollutionData(air, water, soil));
			}
			
			if(tep != null)
			{
				if(EcologyMod.instance.tepc.hasTile(new ResourceLocation(tep.getId())))
				{
					EcologyMod.log.warn(tep.getId()+" is already in TEPC. Thus replacing the previous("+EcologyMod.instance.tepc.getTEP(tep.getId()).toString()+").");
					EcologyMod.instance.tepc.data.remove(EcologyMod.instance.tepc.getTEP(tep.getId()));
				}
				
				
				EcologyMod.instance.tepc.data.add(tep);
				sender.sendMessage(new TextComponentString("Added to TEPC: "+tep.toString()));
				EcologyMod.log.info("Added to TEPC: "+tep.toString());
			}
			else
			{
				sender.sendMessage(new TextComponentString("Unable to add TileEntity to TEPollutionConfig."));
			}
			break;
			
		case remove:
			if(args.length != 2)
			{
				throw new CommandException("Invalid arguments! For REMOVE mode there has to be exactly 2 arguments /TEPollutionConfig REMOVE [tileId]");
			}
			
			String tileId1 = args[1];
			
			if(EcologyMod.instance.tepc.hasTile(new ResourceLocation(tileId1)))
			{
				EcologyMod.instance.tepc.data.remove(EcologyMod.instance.tepc.getTEP(tileId1));
				sender.sendMessage(new TextComponentString("Removed "+tileId1+" from TEPollutionConfig"));
			}
			else
			{
				sender.sendMessage(new TextComponentString("Attempted to remove "+tileId1+" from TEPollutionConfig but there is no such entry"));
			}
			break;
			
		case load:
			sender.sendMessage(new TextComponentString("Loading TEPollutionConfig from the config file."));
			EcologyMod.instance.tepc.load(EcologyMod.instance.tepc.path);
			break;
			
		case save:
			sender.sendMessage(new TextComponentString("Saving TEPollutionConfig to the config file"));
			EcologyMod.instance.tepc.save(EcologyMod.instance.tepc.path);
			break;
			
		case get:
			if(args.length != 2)
			{
				throw new CommandException("Invalid arguments! For GET mode there has to be exactly 2 arguments /TEPollutionConfig GET [tileId]");
			}
			
			String tileId2 = args[1];
			
			if(EcologyMod.instance.tepc.hasTile(new ResourceLocation(tileId2)))
			{
				sender.sendMessage(new TextComponentString(EcologyMod.instance.tepc.getTEP(tileId2).toString()));
			}
			else
			{
				sender.sendMessage(new TextComponentString("Attempted to get "+tileId2+" from TEPollutionConfig but there is no such entry"));
			}
			break;
			
		default:
			throw new CommandException("Invalid MODE!!! It has to be ADD or REMOVE or SAVE or LOAD or GET.");
		}
	}

	private static enum CTMode
	{
		add,
		remove,
		save,
		load,
		get
	}
}
