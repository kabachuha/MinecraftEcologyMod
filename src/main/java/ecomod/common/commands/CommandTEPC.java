package ecomod.common.commands;

import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.TEPollutionConfig.TEPollution;
import ecomod.core.EcologyMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandTEPC extends CommandBase {

	@Override
	public String getName() {
		return "TEPollutionConfig";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.ecomod.tepc.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 1)
			throw new CommandException("commands.ecomod.tepc.fail.not_enough_arguments");
		
		CTMode mode;
		
		try
		{
			mode = CTMode.valueOf(args[0].toLowerCase());
		}
		catch(Exception e)
		{
			throw new CommandException("commands.ecomod.tepc.fail.mode_not_found");
		}
		
		switch(mode)
		{
		case add:
			if(args.length != 5)
			{
				throw new CommandException("commands.ecomod.tepc.add.fail.args");
			}
			
			String tileId = args[1];
			
			float air;
			float water;
			float soil;
			
			try
			{
				air = Float.parseFloat(args[2]);
			}
			catch (NumberFormatException e)
			{
				throw new NumberInvalidException("commands.generic.num.invalid", args[2]);
			}
			
			try
			{
				water = Float.parseFloat(args[3]);
			}
			catch (NumberFormatException e)
			{
				throw new NumberInvalidException("commands.generic.num.invalid", args[3]);
			}
			
			try
			{
				soil = Float.parseFloat(args[4]);
			}
			catch (NumberFormatException e)
			{
				throw new NumberInvalidException("commands.generic.num.invalid", args[4]);
			}
			
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
				sender.sendMessage(new TextComponentTranslation("commands.ecomod.tepc.add.success", tep.toString()));
				EcologyMod.log.info("Added to TEPC: "+tep.toString());
			}
			else
			{
				throw new CommandException("commands.ecomod.tepc.add.fail");
			}
			break;
			
		case remove:
			if(args.length != 2)
			{
				throw new CommandException("commands.ecomod.tepc.remove.fail.args");
			}
			
			String tileId1 = args[1];
			
			if(EcologyMod.instance.tepc.hasTile(new ResourceLocation(tileId1)))
			{
				EcologyMod.instance.tepc.data.remove(EcologyMod.instance.tepc.getTEP(tileId1));
				sender.sendMessage(new TextComponentTranslation("commands.ecomod.tepc.remove.success", tileId1));
			}
			else
			{
				TextComponentTranslation txt = new TextComponentTranslation("commands.ecomod.tepc.remove.fail", tileId1);
				txt.setStyle(txt.getStyle().setColor(TextFormatting.RED));
				sender.sendMessage(txt);
			}
			break;
			
		case load:
			sender.sendMessage(new TextComponentTranslation("commands.ecomod.tepc.load"));
			EcologyMod.instance.tepc.load(EcologyMod.instance.tepc.path);
			break;
			
		case save:
			sender.sendMessage(new TextComponentTranslation("commands.ecomod.tepc.save"));
			if(!EcologyMod.instance.tepc.save(EcologyMod.instance.tepc.path))
				throw new CommandException("commands.ecomod.tepc.save.fail");
			break;
			
		case get:
			if(args.length != 2)
			{
				throw new CommandException("commands.ecomod.tepc.get.fail.args");
			}
			
			String tileId2 = args[1];
			
			if(EcologyMod.instance.tepc.hasTile(new ResourceLocation(tileId2)))
			{
				sender.sendMessage(new TextComponentString(EcologyMod.instance.tepc.getTEP(tileId2).toString()));
			}
			else
			{
				TextComponentTranslation txt = new TextComponentTranslation("commands.ecomod.tepc.get.fail", tileId2);
				txt.setStyle(txt.getStyle().setColor(TextFormatting.RED));
				sender.sendMessage(txt);
			}
			break;
			
		default:
			throw new CommandException("commands.ecomod.tepc.fail.mode_not_found");
		}
	}

	private enum CTMode
	{
		add,
		remove,
		save,
		load,
		get
	}
}
