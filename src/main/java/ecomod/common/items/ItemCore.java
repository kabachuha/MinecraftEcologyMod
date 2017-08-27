package ecomod.common.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.core.stuff.EMAchievements;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemCore extends Item
{

	public ItemCore() {
		super();
		this.setHasSubtypes(true);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);

	}
	
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		
		if(tab == EcomodStuff.ecomod_creative_tabs)
		{
			items.add(new ItemStack(this, 1, 0));//Filter Core
			items.add(new ItemStack(this, 1, 1));//Advanced Filter Core
			items.add(new ItemStack(this, 1, 2));//Analyzer Core
		}
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
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
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

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		
		if(worldIn.isRemote)
			return;
		
		/*Achievement ach = null;
		
		if(stack.getMetadata() == 0)
			ach = EMAchievements.ACHS.get("filter_core");
		if(stack.getMetadata() == 1)
			ach = EMAchievements.ACHS.get("advanced_core");
		if(stack.getMetadata() == 2)
			ach = EMAchievements.ACHS.get("analyzer_core");
		
		if(ach != null)
		if(!playerIn.hasAchievement(ach))
		{
			playerIn.addStat(ach);
		}*/
	}
	
	
}
