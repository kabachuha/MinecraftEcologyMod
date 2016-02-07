package ccpm.commands;

import ccpm.utils.PollutionUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

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
	public void processCommand(ICommandSender sender, String[] args) {
		if(args == null || args.length == 0 || !args[0].equals("gen"))
		{
			sender.addChatMessage(new ChatComponentText("This chunk pollution is"+PollutionUtils.getChunkPollution(sender.getEntityWorld().getChunkFromBlockCoords(sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posZ))));
		}
		else
		{
			sender.addChatMessage(new ChatComponentText("This chunk pollution generation is "+PollutionUtils.processChunk(sender.getEntityWorld().getChunkFromBlockCoords(sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posZ))));
		}
	}

}
