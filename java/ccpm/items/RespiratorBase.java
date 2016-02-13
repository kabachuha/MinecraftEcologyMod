package ccpm.items;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import DummyCore.Utils.MiscUtils;
import DummyCore.Utils.ReflectionProvider;
import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.EnumHelper;
import thaumcraft.api.IGoggles;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.nodes.IRevealer;
import vazkii.botania.api.mana.IManaDiscountArmor;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList({@Optional.Interface(iface = "thaumcraft.api.IRepairable", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.IGoggles", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.IRunicArmor", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.nodes.IRevealer", modid = "Thaumcraft"),@Optional.Interface(iface = "vazkii.botania.api.mana.IManaDiscountArmor", modid = "Botania")})
public class RespiratorBase extends ItemArmor implements IRespirator, IRepairable, IGoggles, IRunicArmor, IManaDiscountArmor, IRevealer {

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
	
	public static boolean isManned(ItemStack item)
	{
		if(!item.hasTagCompound())
			return false;
		
		NBTTagCompound nbt = item.getTagCompound();
		
		if(nbt.hasKey("mana"))
			if(nbt.getBoolean("mana"))
				return true;
		
		return false;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) 
	{
		if(isRev(stack))
			list.add(EnumChatFormatting.DARK_PURPLE+"Goggles of Revealing");
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
		return isRev(itemstack);
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public int getRunicCharge(ItemStack itemstack) {
		
		return 0;
	}

	@Override
	@Optional.Method(modid = "Botania")
	public float getDiscount(ItemStack stack, int slot, EntityPlayer player) {
	
		return isManned(stack) ? 0.1F : 0F;
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
		
		return isRev(itemstack);
	}
	
}
