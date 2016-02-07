package ccpm.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

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
	public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText( "Feel the power of DEBUG!"));
        //sender.addChatMessage(new ChatComponentText("X="+sender.getPlayerCoordinates().posX+ " Z="+sender.getPlayerCoordinates().posZ));
        EntityPlayer player = this.getCommandSenderAsPlayer(sender);
        if(player == null)
        	return;
        
        if(player.getCurrentEquippedItem() != null)
        {
        	NBTTagCompound comp = player.getCurrentEquippedItem().getTagCompound();
        	
        	if(comp == null)
        	{
        		comp = new NBTTagCompound();
        	}
        	
        	comp.setString("ccpmTest", "ccpmTest");
        	
        	player.getCurrentEquippedItem().setTagCompound(comp);
        }
        else
        {
        	ItemStack is = new ItemStack(Items.blaze_rod);
        	NBTTagCompound comp = new NBTTagCompound();
        	comp.setString("ccpmTest", "test");
        	is.setTagCompound(comp);
        	
        	player.setCurrentItemOrArmor(0, is);
        }
	}

}
