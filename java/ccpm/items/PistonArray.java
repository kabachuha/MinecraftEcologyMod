package ccpm.items;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Utils.IOldItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PistonArray extends Item implements IOldItem {

	public PistonArray() {
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
		icon = reg.registerItemIcon("ccpm:piston_array");
	}

	@Override
	public int getRenderPasses(ItemStack stk) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Icon getIconFromItemStackAndRenderPass(ItemStack stk, int pass) {
		// TODO Auto-generated method stub
		return icon;
	}

	@Override
	public boolean recreateIcon(ItemStack stk) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean render3D(ItemStack stk) {
		// TODO Auto-generated method stub
		return false;
	}

}
