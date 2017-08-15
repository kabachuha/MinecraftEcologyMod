package ecomod.common.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
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
}
