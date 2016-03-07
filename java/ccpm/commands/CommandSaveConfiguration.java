package ccpm.commands;

import ccpm.core.CCPM;
import ccpm.utils.config.PollutionConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandSaveConfiguration extends CommandBase {

	public CommandSaveConfiguration() {
	}

	@Override
	public String getCommandName() {
		return "saveEcologyModCfg";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/saveEcologyModCfg //Saves & rewrites tiles pollution prodution configuration";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(sender == null)
			return;
		
		if(sender.getEntityWorld() == null || sender.getEntityWorld().isRemote)
			return;
		
		sender.addChatMessage(new ChatComponentText("Serializing and saving configuration..."));
		PollutionConfig.serializeAndSave(CCPM.cfgpath);
		sender.addChatMessage(new ChatComponentText("Configuration saved!"));
	}

}
