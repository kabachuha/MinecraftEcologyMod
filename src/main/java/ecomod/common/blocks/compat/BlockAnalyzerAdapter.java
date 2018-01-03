package ecomod.common.blocks.compat;

import ecomod.api.EcomodStuff;
import ecomod.common.tiles.compat.TileAnalyzerAdapter;
import ecomod.core.EMConsts;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.classloading.FMLForgePlugin;

public class BlockAnalyzerAdapter extends Block implements ITileEntityProvider {

	public BlockAnalyzerAdapter() {
		super(Material.rock);
		
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		
		this.setHardness(8F);
		this.setResistance(5F);
		
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {

		return new TileAnalyzerAdapter();
	}

	
	public boolean isFullCube()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    
    public MapColor getMapColor(int meta)
    {
    	return MapColor.silverColor;
    }
    
    public String getUnlocalizedName()
    {
    	return "tile."+EMConsts.modid + ".oc_analyzer_adapter";
    }
    
    @Override
    public int getRenderBlockPass()
    {
        return 0;
    }
	
	@Override
	public int getRenderType()
    {
        return 2634;
    }
	
	@Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }
}
