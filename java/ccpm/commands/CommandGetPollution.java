package ccpm.commands;

import ccpm.utils.PollutionUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandGetPollution extends CommandBase {

	public CommandGetPollution() {

	}

	@Override
	public String getCommandName() {
		return "getPollution";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/getPollution <gen>(optional)";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args == null || args.length == 0 || !args[0].equals("gen"))
		{
			sender.addChatMessage(new TextComponentString("This chunk pollution is"+PollutionUtils.getChunkPollution(sender.getEntityWorld().getChunkFromBlockCoords(sender.getPosition()))));
		}
		else
		{
			sender.addChatMessage(new TextComponentString("This chunk pollution generation is "+PollutionUtils.getChunkPollution(sender.getEntityWorld().getChunkFromBlockCoords(sender.getPosition()))));
		}
	}

}
