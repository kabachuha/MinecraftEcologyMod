package ccpm.items;

import java.util.List;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Utils.IOldItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PollutedMisc extends Item implements IOldItem {

	Icon[] icons;
	String[] names;
	
	public PollutedMisc(String... names) {
		super();
		this.setHasSubtypes(true);
		this.names = names;
		icons = new Icon[names.length];
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
	
	@Override
	public Icon getIconFromDamage(int meta) {
		return icons[meta];
	}

	@Override
	public Icon getIconFromItemStack(ItemStack stk) {
		return icons[stk.getItemDamage()];
	}

	@Override
	public void registerIcons(IconRegister reg) {
		for(int i = 0; i < names.length; i++)
		{
			icons[i] = reg.registerItemIcon("ccpm:"+names[i]);
		}
	}

	@Override
	public int getRenderPasses(ItemStack stk) {
		return 0;
	}

	@Override
	public Icon getIconFromItemStackAndRenderPass(ItemStack stk, int pass) {
		return icons[stk.getItemDamage()];
	}

	@Override
	public boolean recreateIcon(ItemStack stk) {
		return false;
	}

	@Override
	public boolean render3D(ItemStack stk) {
		return false;
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
