package ccpm.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import ccpm.core.CCPM;
import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameData;

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
		CCPM.log.debug("Starting handleling 'addTile' command...");
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
		
		TileEntity tile = sender.getEntityWorld().getTileEntity(tiPos);
		
		if(tile == null || tile.isInvalid())
		{
			sender.addChatMessage(new ChatComponentText("There is not tile entity at pos "+tiPos.toString()));
			return;
		}
		
		NBTTagCompound tag = new NBTTagCompound();
		
		tile.writeToNBT(tag);
		
		if(!tag.hasKey("id"))
		{
			sender.addChatMessage(new ChatComponentText("The tile entity doesn't have an ID properity!"));
			return;
		}
		
		String id = tag.getString("id");
		
		Tilez tilez = new Tilez();
		
		tilez.setModid(getStackModid(new ItemStack(sender.getEntityWorld().getBlockState(tiPos).getBlock())) == "" || getStackModid(new ItemStack(sender.getEntityWorld().getBlockState(tiPos).getBlock())) == null ? "Minecraft" : getStackModid(new ItemStack(sender.getEntityWorld().getBlockState(tiPos).getBlock())));
		tilez.setName(id);
		tilez.setPollution(pollution);
		
		List<Tilez> tiles = new LinkedList<Tilez>(Arrays.asList(PollutionConfig.cfg.getTiles()));
		
		if(!tiles.contains(tilez))
		{
			sender.addChatMessage(new ChatComponentText("Adding tile entity with id " + tilez.getName()+ " producing "+tilez.getPollution()+" pollution from mod"+tilez.getModid()+ " to configuration"));
			tiles.add(tilez);
		}
		else
		{
			sender.addChatMessage(new ChatComponentText("There are already this tile in the configuration! If you want to rewrite it use /removeTile command"));
		}
		
		PollutionConfig.cfg.setTiles(tiles.toArray(new Tilez[tiles.size()]));
		
		sender.addChatMessage(new ChatComponentText("Tile added to configuration!"));
		
		sender.addChatMessage(new ChatComponentText("If you want to save changes use /saveEcologyModCfg command"));
	}

	 
	 private static String getStackModid(ItemStack stack) {
		 return ((ResourceLocation) GameData.getItemRegistry().getNameForObject(stack.getItem())).getResourceDomain();
	    }
}
