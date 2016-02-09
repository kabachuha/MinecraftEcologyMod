package ccpm.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class RespiratorBase extends ItemArmor implements IRespirator {

	public static final ArmorMaterial respiratorMatter = EnumHelper.addArmorMaterial("respMat", 5, new int[]{2,0,0,0}, 16);
	
	public RespiratorBase(String unlocalizedName, ArmorMaterial material) 
	{
		super(material,0,0);
		this.setUnlocalizedName(unlocalizedName);
		this.setTextureName("ccpm:repsirator");
	}

	@Override
	public boolean isFiltering(EntityPlayer player, ItemStack respStack) {
		return true;
	}

	@Override
	public boolean renderHud() {
		return true;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		return CCPM.MODID+":textures/armor/Armor.png";
	}
	
	
}
