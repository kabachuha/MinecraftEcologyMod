package ccpm.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;

public class CommandTestWand extends CommandBase {

	public CommandTestWand() {
	}

	@Override
	public String getCommandName() {

		return "testCCPM";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {

		return "/testCCPM";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		sender.addChatMessage(new TextComponentString( "Feel the power of DEBUG!"));
        //sender.addChatMessage(new ChatComponentText("X="+sender.getPlayerCoordinates().posX+ " Z="+sender.getPlayerCoordinates().posZ));
        EntityPlayer player;
		try {
			player = this.getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException e) {
			e.printStackTrace();
			return;
		}
        if(player == null)
        	return;
        
        if(player.getHeldItemMainhand() != null)
        {
        	NBTTagCompound comp = player.getHeldItemMainhand().getTagCompound();
        	
        	if(comp == null)
        	{
        		comp = new NBTTagCompound();
        	}
        	
        	comp.setString("ccpmTest", "ccpmTest");
        	
        	player.getHeldItemMainhand().setTagCompound(comp);
        }
        else
        {
        	ItemStack is = new ItemStack(Items.BLAZE_ROD);
        	NBTTagCompound comp = new NBTTagCompound();
        	comp.setString("ccpmTest", "test");
        	is.setTagCompound(comp);
        	
        	player.setHeldItem(EnumHand.MAIN_HAND, is);;
        }
		
	}

}
