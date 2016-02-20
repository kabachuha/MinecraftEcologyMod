package ccpm.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Utils.BlockStateMetadata;
import DummyCore.Utils.IOldCubicBlock;
import ccpm.tiles.TileEntityFilter;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class BlockFilter extends Block implements ITileEntityProvider, IOldCubicBlock {

	public BlockFilter() {
		super(Material.rock);
		
		this.setUnlocalizedName("ccpm.filter");
        this.setHardness(1.0F);
        this.setResistance(6.0F);
        //this.setLightLevel(1.0F);
        this.setHarvestLevel("pickaxe", 0);
        this.setStepSound(soundTypeMetal);
        //this.lightValue = 5;
        this.setDefaultState(BlockStateMetadata.createDefaultBlockState(this));
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityFilter();
	}
	
	Icon i = null;
	
	@Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int i, int j)
    {
		return this.i;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IconRegister reg)
    {		
		i = reg.registerBlockIcon("ccpm:filter");
    }

	@Override
	public Icon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return getIcon(side,BlockStateMetadata.getBlockMetadata(world, x, y, z));
	}

	public int damageDropped(IBlockState state)
    {
    	return BlockStateMetadata.getMetaFromState(state);
    }
    
    public IBlockState getStateFromMeta(int meta)
    {
    	return this.getDefaultState().withProperty(BlockStateMetadata.METADATA, meta);
    }
    
    public int getMetaFromState(IBlockState state)
    {
    	return BlockStateMetadata.getMetaFromState(state);
    }

    protected BlockState createBlockState()
    {
    	return new BlockState(this,BlockStateMetadata.METADATA);
    }
	
	@Override
	public List<IBlockState> listPossibleStates(Block b) {
		return Arrays.asList(new IBlockState[]{this.getDefaultState()});
	}

	@Override
	public int getDCRenderID() {
		return RenderAccessLibrary.RENDER_ID_CUBE;
	}
    
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
		super.randomDisplayTick(worldIn, pos, state, rand);
		
		if(worldIn.isBlockPowered(pos))
		for(int i = -2; i <= 2; i++)
			for(int j = -2; j <=2; j++)
				for(int k = -2; k <=2; k++)
				{
					if (rand.nextInt(8) == 0)
	                {
						worldIn.spawnParticle(rand.nextBoolean() ? EnumParticleTypes.CLOUD : EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D, new int[0]);
	                }
	                
				}
    }
    
}
