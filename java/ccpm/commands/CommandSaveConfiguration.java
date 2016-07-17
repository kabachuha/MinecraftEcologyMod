package ccpm.commands;

import ccpm.core.CCPM;
import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender == null)
			return;
		
		if(sender.getEntityWorld() == null || sender.getEntityWorld().isRemote)
			return;
		
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
		
		NBTTagList pages = new NBTTagList();
		for(Tilez t : PollutionConfig.cfg.getTiles())
		{
			pages.appendTag(new NBTTagString("Tile with id "+t.getName()+" from mod "+t.getModid()+" can produce "+t.getPollution()+" pollution"));
		}
		
		book.setTagInfo("pages", pages);
		book.setTagInfo("author", new NBTTagString(CCPM.NAME));
		book.setTagInfo("title", new NBTTagString("Pollution configuration"));
		
		EntityItem ei = new EntityItem(sender.getEntityWorld(), sender.getPosition().getX(), sender.getPosition().getY() + 8, sender.getPosition().getZ(), book);
		
		sender.getEntityWorld().spawnEntityInWorld(ei);
		
		sender.addChatMessage(new TextComponentString("Serializing and saving configuration..."));
		PollutionConfig.serializeAndSave(CCPM.cfgpath);
		sender.addChatMessage(new TextComponentString("Configuration saved!"));

		
	}

}
