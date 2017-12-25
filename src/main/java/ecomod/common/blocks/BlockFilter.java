package ecomod.common.blocks;

import ecomod.api.EcomodStuff;
import ecomod.common.tiles.TileFilter;
import ecomod.core.EMConsts;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFilter extends Block implements ITileEntityProvider {

	public BlockFilter() {
		super(Material.rock);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		this.setHardness(8F);
		this.setResistance(5F);
		
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileFilter();
	}


	@Override
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}


	@Override
	public boolean isOpaqueCube() {
		return false;
	}


	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return false;
	}
	
	@Override
	public String getUnlocalizedName() {
		return EMConsts.modid+".filter";
	}
}
