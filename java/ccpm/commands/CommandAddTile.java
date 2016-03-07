package ccpm.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class CommandAddTile extends CommandBase {

	public CommandAddTile() {
	}

	@Override
	public String getCommandName() {
		return "addTile";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/addTile <pollution> //Adds a tile under you to the pollution configuration";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args==null || args.length == 0 || args[0] == null)
		{
			sender.addChatMessage(new ChatComponentText("Argument can't be null"));
			return;
		}
		if(sender == null)
			return;
		if(sender.getEntityWorld() == null || sender.getEntityWorld().isRemote)
			return;
		
		BlockPos tiPos = sender.getPosition().down();
		float pollution;
		try{
		   pollution = Float.parseFloat(args[0]);
		}
		catch(NumberFormatException e)
		{
			sender.addChatMessage(new ChatComponentText("Invalid argument!"));
			return;
		}
		
		TileEntity tile = sender.getEntityWorld().getTileEntity(sender.getPosition());
		
		if(tile == null || tile.isInvalid())
			return;
		
		
		NBTTagCompound tag = new NBTTagCompound();
		
		tile.writeToNBT(tag);
		
		if(!tag.hasKey("id"))
		{
			sender.addChatMessage(new ChatComponentText("Tile entity doesn't have an ID parameter!"));
			return;
		}
		
		String id = tag.getString("id");
		
		Tilez tilez = new Tilez();
		
		tilez.setModid(sender.getName());
		tilez.setName(id);
		tilez.setPollution(pollution);
		
		List<Tilez> tiles = new LinkedList<Tilez>(Arrays.asList(PollutionConfig.cfg.getTiles()));
		
		if(!tiles.contains(tilez))
		{
			tiles.add(tilez);
		}
		else
		{
			sender.addChatMessage(new ChatComponentText("There are already this tile in the configuration!"));
		}
		
		PollutionConfig.cfg.setTiles(tiles.toArray(new Tilez[tiles.size()]));
		
		sender.addChatMessage(new ChatComponentText("Tile added to configuration!"));
	}

}
