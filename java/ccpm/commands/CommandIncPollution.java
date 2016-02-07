package ccpm.commands;

import ccpm.utils.PollutionUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandIncPollution extends CommandBase {

	public CommandIncPollution() {
	}

	@Override
	public String getCommandName() {
		return "increasePollution";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/increasePollution <amount>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		
		if(args==null||args[0]==null)
		{
			sender.addChatMessage(new ChatComponentText("Argument can't be null"));
			return;
		}
		
		PollutionUtils.increasePollution(Float.parseFloat(args[0]), sender.getEntityWorld().getChunkFromBlockCoords(sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posZ));
	}

}
