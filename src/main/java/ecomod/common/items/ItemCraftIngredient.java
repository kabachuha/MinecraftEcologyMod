package ecomod.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemCraftIngredient extends Item
{
	public ItemCraftIngredient() {
		super();
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
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
	    subItems.add(new ItemStack(itemIn, 1, 0));//Pistons Array
	    subItems.add(new ItemStack(itemIn, 1, 1));//Vent
	}
}
