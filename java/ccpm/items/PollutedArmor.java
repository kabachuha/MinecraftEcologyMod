package ccpm.items;

import ccpm.core.CCPM;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.*;

@Optional.Interface(iface = "thaumcraft.api.items.IRepairable", modid = "Thaumcraft")
public class PollutedArmor extends ItemArmor/* implements IOldItem, IRepairable */{

	public static final ArmorMaterial pollution = EnumHelper.addArmorMaterial("polluted", CCPM.MODID+":polluted", 20, new int[]{3,7,5,2}, 20, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 3);
	
    String textureName;
	
	EntityEquipmentSlot type;
	
	
	public PollutedArmor(ArmorMaterial material, EntityEquipmentSlot armorType, String unlockname) {
		super(material, 0, armorType);
		this.setUnlocalizedName(unlockname);
		type = armorType;	
	}
	
	public Item setTextureName(String s)
	{
		textureName = s;
		return this;
	}
	
	public EnumRarity getRarity(ItemStack stk)
	{
		return EnumRarity.RARE;
	}
}
