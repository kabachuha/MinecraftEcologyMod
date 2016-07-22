package ccpm.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SrapBrick extends Item {

	public SrapBrick() {
		super();
		this.setUnlocalizedName("ccpm.brick");
		
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
