package ecomod.common.items;

import java.util.List;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.core.stuff.EMAchievements;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockFrame extends ItemBlock
{
	public ItemBlockFrame() {
		super(EcomodBlocks.FRAME);
		
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
	}

	public int getMetadata(int damage)
    {
        return damage;
    }
	
	@Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "."+stack.getItemDamage();
    }

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		if(tab == EcomodStuff.ecomod_creative_tabs)
		{
			subItems.add(new ItemStack(this, 1, 0));//Basic Frame
			subItems.add(new ItemStack(this, 1, 1));//Advanced Frame
		}
	}
	
	
}
