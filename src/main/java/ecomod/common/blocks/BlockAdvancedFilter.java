package ecomod.common.blocks;

import ecomod.api.EcomodStuff;
import ecomod.common.tiles.TileAdvancedFilter;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAdvancedFilter extends Block implements ITileEntityProvider{

	public BlockAdvancedFilter()
	{
		super(Material.iron);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		
		this.setHardness(15F);
		this.setResistance(10F);
		
		this.setHarvestLevel("pickaxe", 2);
	}

	public boolean isNormalCube()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileAdvancedFilter();
	}

	@Override
	public MapColor getMapColor(int meta) {
		return MapColor.diamondColor;
	}
	
	public String getUnlocalizedName()
	{
		return EMConsts.modid+".advanced_filter";
	}
}
