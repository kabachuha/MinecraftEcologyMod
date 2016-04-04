package ccpm.commands;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import ccpm.core.CCPM;
import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandRemoveTile extends CommandBase {

	public CommandRemoveTile() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getCommandName() {
		return "removeTile";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/removeTile [tileId...]    //Removes tile from the "+CCPM.NAME+"'s configuration";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args==null || args.length == 0 || args[0] == null)
		{
			sender.addChatMessage(new ChatComponentText("Arguments can't be null. If you want to know tile ids click them with 'TestWand'(/testCCPM) or use /getRegTiles command but it is risky(Minecraft may crash) "));
			return;
		}
		if(sender == null)
			return;
		if(sender.getEntityWorld() == null || sender.getEntityWorld().isRemote)
			return;
		
		List<Tilez> tiles = new LinkedList<Tilez>(Arrays.asList(PollutionConfig.cfg.getTiles()));
		
		Hashtable<String, Tilez> ht = PollutionConfig.toHashByName();
		
		for(String a : args)
		{
			if(ht.containsKey(a))
			{
				sender.addChatMessage(new ChatComponentText("Removing tile with id "+a+" from configuration"));
				tiles.remove(ht.get(a));
				ht.remove(a);
			}
			else
			{
				sender.addChatMessage(new ChatComponentText("Tile with id "+a+" is not found in configuration!"));
			}
		}

		PollutionConfig.cfg.setTiles(tiles.toArray(new Tilez[tiles.size()]));
		
		sender.addChatMessage(new ChatComponentText("If you want to save changes use /saveEcologyModCfg command"));
	}

}
