package ecomod.common.items;

import ecomod.api.EcomodBlocks;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockFrame extends ItemBlock
{
	public ItemBlockFrame() {
		super(EcomodBlocks.FRAME);
		
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.REDSTONE);
	}

	public int getMetadata(int damage)
    {
        return damage;
    }
	
	@Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "."+stack.getMetadata();
    }
}
