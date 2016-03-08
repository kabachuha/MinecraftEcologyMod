package ccpm.items;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Utils.IOldItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SrapBrick extends Item implements IOldItem {

	public SrapBrick() {
		super();
		
	}

	Icon icon;
	
	@Override
	public Icon getIconFromDamage(int meta) {
		return icon;
	}

	@Override
	public Icon getIconFromItemStack(ItemStack stk) {
		return icon;
	}

	@Override
	public void registerIcons(IconRegister reg) {
		icon = reg.registerItemIcon("ccpm:brick_pollution");
	}

	@Override
	public int getRenderPasses(ItemStack stk) {
		return 0;
	}

	@Override
	public Icon getIconFromItemStackAndRenderPass(ItemStack stk, int pass) {
		return icon;
	}

	@Override
	public boolean recreateIcon(ItemStack stk) {
		return false;
	}

	@Override
	public boolean render3D(ItemStack stk) {
		return false;
	}

	
	public EnumRarity getRarity(ItemStack stk)
	{
		return EnumRarity.RARE;
	}
	
	public boolean isBeaconPayment(ItemStack stk)
	{
		return true;
	}
}
