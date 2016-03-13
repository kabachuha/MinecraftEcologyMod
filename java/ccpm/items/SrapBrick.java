package ccpm.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Utils.IOldItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SrapBrick extends Item implements IOldItem {

	public SrapBrick() {
		super();
		this.setUnlocalizedName("ccpm.brick");
		
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
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			tooltip.add("Compressed Concentrated Pollution Brick");
		}
	}
}
