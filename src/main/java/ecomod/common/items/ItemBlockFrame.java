package ecomod.common.items;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

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
        return super.getUnlocalizedName(stack) + '.' +stack.getMetadata();
    }

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		
		if(tab == EcomodStuff.ecomod_creative_tabs)
		{
			items.add(new ItemStack(this, 1, 0));//Basic Frame
			items.add(new ItemStack(this, 1, 1));//Advanced Frame
		}
	}
	
	
}
