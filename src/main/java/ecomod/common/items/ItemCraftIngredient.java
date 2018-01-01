package ecomod.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ecomod.api.EcomodStuff;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemCraftIngredient extends Item
{
	@SideOnly(Side.CLIENT)
	protected IIcon vent_icon;
	
	public ItemCraftIngredient() {
		super();
		
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
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
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		subItems.add(new ItemStack(this, 1, 0));//Pistons Array
		subItems.add(new ItemStack(this, 1, 1));//Vent
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta) {
		if(meta == 1)
			return vent_icon;
		
		if(meta == 2)
			return BlockPistonBase.getPistonBaseIcon("piston_side");
		
		return super.getIconFromDamage(meta);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister reg) {
		vent_icon = reg.registerIcon("ecomod:vent_s");
	}
	
	
}
