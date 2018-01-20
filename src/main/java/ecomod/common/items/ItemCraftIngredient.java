package ecomod.common.items;

import ecomod.api.EcomodStuff;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemCraftIngredient extends Item
{
	public ItemCraftIngredient() {
		super();
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
			items.add(new ItemStack(this, 1, 0));//Pistons Array
			items.add(new ItemStack(this, 1, 1));//Vent
		}
	}
}
