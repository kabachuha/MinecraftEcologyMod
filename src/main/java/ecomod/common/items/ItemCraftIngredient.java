package ecomod.common.items;

import java.util.List;

import ecomod.api.EcomodStuff;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
        return super.getUnlocalizedName(stack) + "."+stack.getItemDamage();
    }
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		if(tab == EcomodStuff.ecomod_creative_tabs)
		{
			subItems.add(new ItemStack(this, 1, 0));//Pistons Array
			subItems.add(new ItemStack(this, 1, 1));//Vent
		}
	}
}
