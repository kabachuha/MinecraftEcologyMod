package ecomod.common.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.core.stuff.EMAchievements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemCore extends Item
{
	@SideOnly(Side.CLIENT)
	protected IIcon filer_icon;
	
	@SideOnly(Side.CLIENT)
	protected IIcon advanced_filter_icon;
	
	@SideOnly(Side.CLIENT)
	protected IIcon analyzer_icon;

	public ItemCore() {
		super();
		
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		subItems.add(new ItemStack(this, 1, 0));//Filter Core
		subItems.add(new ItemStack(this, 1, 1));//Advanced Filter Core
		subItems.add(new ItemStack(this, 1, 2));//Analyzer Core
	}
	
	public int getMetadata(int damage)
    {
        return damage;
    }
	
	@Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "."+stack.getItemDamage();
    }
	
	

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean flagIn) {
		super.addInformation(stack, player, tooltip, flagIn);
		
		if(stack.getItemDamage() == 0)
		{
			tooltip.add(I18n.format("tooltip.ecomod.core.filter", new Object[0]));
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
		{
			String s = stack.getItemDamage() == 1 ? I18n.format(((ItemBlockFrame)Item.getItemFromBlock(EcomodBlocks.FRAME)).getUnlocalizedName(new ItemStack(EcomodBlocks.FRAME, 1, 1)) + ".name") : I18n.format(((ItemBlockFrame)Item.getItemFromBlock(EcomodBlocks.FRAME)).getUnlocalizedName(new ItemStack(EcomodBlocks.FRAME, 1, 0))+".name", new Object[0]);
			tooltip.add(I18n.format("tooltip.ecomod.core.shifted", s));
		}
		else
		{
			tooltip.add("<"+I18n.format("tooltip.ecomod.more_information", new Object[0])+">");
		}
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		
		if(worldIn.isRemote)
			return;
		
		/*Achievement ach = null;
		
		if(stack.getMetadata() == 0)
			ach = EMAchievements.ACHS.get("filter_core");
		if(stack.getMetadata() == 1)
			ach = EMAchievements.ACHS.get("advanced_core");
		if(stack.getMetadata() == 2)
			ach = EMAchievements.ACHS.get("analyzer_core");
		
		if(ach != null)
		if(!playerIn.hasAchievement(ach))
		{
			playerIn.addStat(ach);
		}*/
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta) {
		if(meta == 0)
			return filer_icon;
		if(meta == 1)
			return advanced_filter_icon;
		if(meta == 2)
			return analyzer_icon;
		
		return super.getIconFromDamage(meta);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister reg) {
		filer_icon = reg.registerIcon("ecomod:filter_core");
		advanced_filter_icon = reg.registerIcon("ecomod:advanced_filter_core");
		analyzer_icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("mushroom_block_inside");
	}
}
