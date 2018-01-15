package ecomod.common.blocks;

import ecomod.api.EcomodStuff;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAnalyzer extends Block implements ITileEntityProvider {

	public BlockAnalyzer()
	{
		super(Material.ROCK, MapColor.BROWN);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		this.setHardness(8F);
		this.setResistance(5F);
		
		this.setHarvestLevel("pickaxe", 1);
	}

	public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return Block.FULL_BLOCK_AABB;
    }
    
    @Override
    public BlockStateContainer createBlockState()
    {
		return new BlockStateContainer(this, new IProperty[0]);
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileAnalyzer();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
	    	if(player == null || !player.isSneaking())
	    	{
	    		if(!world.isRemote)
	    		{
	    			((TileAnalyzer)world.getTileEntity(pos)).sendUpdatePacket();
	    		}
	    		EcologyMod.proxy.openGUIAnalyzer(player, (TileAnalyzer)world.getTileEntity(pos));
	    	}
	    	
	    return true;
	}
}
