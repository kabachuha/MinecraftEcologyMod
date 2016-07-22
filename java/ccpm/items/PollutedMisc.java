package ccpm.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PollutedMisc extends Item {

	String[] names;
	
	public PollutedMisc(String... names) {
		super();
		this.setHasSubtypes(true);
		this.names = names;
	}

	@Override
	public int getMetadata (int meta) {
		return meta;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "ccpm.item.ingredient."+names[stack.getItemDamage()];
	}

	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items)
	{
		for(int i = 0; i < names.length; i++)
		{
			items.add(new ItemStack(item, 1, i));
		}
	}
	
	public EnumRarity getRarity(ItemStack item)
	{
		return EnumRarity.RARE;
	}
	
}
