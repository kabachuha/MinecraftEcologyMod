package ccpm.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ccpm.core.CCPM;
import ccpm.tiles.TileEntityFilter;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class BlockFilter extends Block implements ITileEntityProvider {

	public BlockFilter() {
		super(Material.ROCK);
		
		this.setUnlocalizedName("ccpm.filter");
        this.setHardness(1.0F);
        this.setResistance(6.0F);
        //this.setLightLevel(1.0F);
        this.setHarvestLevel("pickaxe", 0);
        //this.lightValue = 5;
        this.setCreativeTab(CCPM.CREATIVE_TAB);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityFilter();
	}
	
    
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand)
    {
		super.randomDisplayTick(state, worldIn, pos, rand);
		
		if(worldIn.isBlockPowered(pos))
		for(int i = -2; i <= 2; i++)
			for(int j = -2; j <=2; j++)
				for(int k = -2; k <=2; k++)
				{
					if (rand.nextInt(16) == 0)
	                {
						worldIn.spawnParticle(rand.nextBoolean() ? EnumParticleTypes.CLOUD : EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D, new int[0]);
	                }
	                
				}
    }
    
}
