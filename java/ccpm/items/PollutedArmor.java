package ccpm.items;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Utils.IOldItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import thaumcraft.api.items.IRepairable;
import net.minecraftforge.fml.common.*;

@Optional.Interface(iface = "thaumcraft.api.items.IRepairable", modid = "Thaumcraft")
public class PollutedArmor extends ItemArmor implements IOldItem, IRepairable {

	public static final ArmorMaterial pollution = EnumHelper.addArmorMaterial("polluted", "polluted", 20, new int[]{3,7,5,2}, 20);
	
	Icon icon;
    String textureName;
	
	int type;
	
	
	public PollutedArmor(ArmorMaterial material, int armorType, String unlockname) {
		super(material, 0, armorType);
		this.setUnlocalizedName(unlockname);
		type = armorType;
		
	}
	
	public Item setTextureName(String s)
	{
		textureName = s;
		return this;
	}


	@Override
	public Icon getIconFromDamage(int meta) {
		return icon;
	}

	@Override
	public Icon getIconFromItemStack(ItemStack stk) {
		return getIconFromDamage(stk.getMetadata());
	}

	@Override
	public void registerIcons(IconRegister reg) {
		icon = reg.registerItemIcon(textureName);
	}



	@Override
	public int getRenderPasses(ItemStack stk) {
		return 0;
	}

	@Override
	public Icon getIconFromItemStackAndRenderPass(ItemStack stk, int pass) {
		return getIconFromItemStack(stk);
	}

	@Override
	public boolean recreateIcon(ItemStack stk) {
		return false;
	}

	@Override
	public boolean render3D(ItemStack stk) {
		return false;
	}

	
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
    	return slot == 2 ? "ccpm:textures/armor/pollArmor1.png" : "ccpm:textures/armor/pollArmor2.png";
    }
}
