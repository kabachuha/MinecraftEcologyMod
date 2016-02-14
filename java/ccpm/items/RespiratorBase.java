package ccpm.items;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Utils.IOldItem;
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
import net.minecraftforge.fml.common.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRepairable;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IRunicArmor;
import thaumcraft.api.items.IVisDiscountGear;


@Optional.InterfaceList({@Optional.Interface(iface = "thaumcraft.api.items.IRepairable", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.items.IRunicArmor", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft"),@Optional.Interface(iface = "thaumcraft.api.items.IVisDiscountGear", modid = "Thaumcraft")})
public class RespiratorBase extends ItemArmor implements IRespirator, IRepairable, IGoggles, IRunicArmor, IRevealer, IOldItem, IVisDiscountGear {

	Icon i =null;
	
	public static final ArmorMaterial respiratorMatter = EnumHelper.addArmorMaterial("respMat","respMat", 5, new int[]{2,0,0,0}, 16);
	
	public RespiratorBase(String unlocalizedName, ArmorMaterial material) 
	{
		super(material,0,0);
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
	@Optional.Method(modid = "Thaumcraft")
	public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
		
		return isRev(itemstack);
	}

	@Override
	public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
		return isRev(stack) ? 8 : 0;
	}

	@Override
	public Icon getIconFromDamage(int meta) {
		return i;
	}

	@Override
	public Icon getIconFromItemStack(ItemStack stk) {
		return i;
	}

	@Override
	public void registerIcons(IconRegister reg) {
		i = reg.registerItemIcon("ccpm:repsirator");
	}

	@Override
	public int getRenderPasses(ItemStack stk) {
		return 0;
	}

	@Override
	public Icon getIconFromItemStackAndRenderPass(ItemStack stk, int pass) {
		return i;
	}

	@Override
	public boolean recreateIcon(ItemStack stk) {
		return false;
	}

	@Override
	public boolean render3D(ItemStack stk) {
		return false;
	}
	
}
