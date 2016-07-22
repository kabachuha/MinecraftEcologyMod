package ccpm.items;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.*;


@Optional.InterfaceList({@Optional.Interface(iface = "thaumcraft.api.items.IRepairable", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.items.IRunicArmor", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.items.IVisDiscountGear", modid = "Thaumcraft")})
public class RespiratorBase extends ItemArmor implements IRespirator/*, IRepairable, IGoggles, IRunicArmor, IRevealer, IOldItem, IVisDiscountGear */{
	
	public static final ArmorMaterial respiratorMatter = EnumHelper.addArmorMaterial("respirator",CCPM.MODID+":respirator", 5, new int[]{2,0,0,0}, 16, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
	
	public RespiratorBase(String unlocalizedName, ArmorMaterial material) 
	{
		super(material,0,EntityEquipmentSlot.HEAD);
		this.setUnlocalizedName(unlocalizedName);
		//("ccpm:repsirator");
	}

	@Override
	public boolean isFiltering(EntityPlayer player, ItemStack respStack) {
		return true;
	}

	@Override
	public boolean renderHud() {
		return true;
	}
	
	public static boolean isRev(ItemStack item)
	{
		if(!item.hasTagCompound())
			return false;
		
		NBTTagCompound nbt = item.getTagCompound();
		
		if(nbt.hasKey("revealing"))
			if(nbt.getInteger("revealing") == 1)
				return true;
		
		return false;
	}
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) 
	{
		if(isRev(stack))
			list.add(TextFormatting.DARK_PURPLE+"Goggles of Revealing");
	}

	@Optional.Method(modid = "Thaumcraft")
	public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
		return isRev(itemstack);
	}

	@Optional.Method(modid = "Thaumcraft")
	public int getRunicCharge(ItemStack itemstack) {
		
		return 0;
	}

	

	@Optional.Method(modid = "Thaumcraft")
	public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
		
		return isRev(itemstack);
	}

	//@Override
	//public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
	//	return isRev(stack) ? 8 : 0;
	//}
	
	public EnumRarity getRarity(ItemStack stk)
	{
		return EnumRarity.UNCOMMON;
	}
}
