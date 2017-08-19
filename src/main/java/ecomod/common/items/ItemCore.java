package ecomod.common.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import ecomod.api.EcomodBlocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemCore extends Item
{

	public ItemCore() {
		super();
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.REDSTONE);
		
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
	    subItems.add(new ItemStack(itemIn, 1, 0));//Filter Core
	    subItems.add(new ItemStack(itemIn, 1, 1));//Advanced Filter Core
	    subItems.add(new ItemStack(itemIn, 1, 2));//Analyzer Core
	}
	
	public int getMetadata(int damage)
    {
        return damage;
    }
	
	@Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "."+stack.getMetadata();
    }

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		
		if(stack.getMetadata() == 0)
		{
			tooltip.add("These filters are used by Respirator");
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			String s = stack.getMetadata() == 1 ? I18n.format(((ItemBlockFrame)Item.getItemFromBlock(EcomodBlocks.FRAME)).getUnlocalizedName(new ItemStack(EcomodBlocks.FRAME, 1, 1)) + ".name") : I18n.format(((ItemBlockFrame)Item.getItemFromBlock(EcomodBlocks.FRAME)).getUnlocalizedName(new ItemStack(EcomodBlocks.FRAME, 1, 0))+".name", new Object[0]);
			tooltip.add("Right Click "+s+" with this item to make a corresponding machine");
		}
		else
		{
			tooltip.add("<SHIFT for more information>");
		}
	}
	
	
}
