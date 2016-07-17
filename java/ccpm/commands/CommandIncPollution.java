package ccpm.commands;

import ccpm.utils.PollutionUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args==null||args[0]==null)
		{
			sender.addChatMessage(new TextComponentString("Argument can't be null"));
			return;
		}
		
		PollutionUtils.increasePollution(Float.parseFloat(args[0]), sender.getEntityWorld().getChunkFromBlockCoords(sender.getPosition()));
	}

}
