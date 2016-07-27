package ccpm.blocks;

import java.util.Arrays;
import java.util.List;

import ccpm.core.CCPM;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockPollutionBricks extends Block {

	public BlockPollutionBricks() {
		super(Material.ROCK);
		this.setUnlocalizedName("ccpm.bricks.pollution");
        this.setHardness(4.0F);
        this.setResistance(60.0F);
        this.setLightLevel(6.0F);
        this.setHarvestLevel("pickaxe", 2);
        //this.lightValue = 5;
        this.setCreativeTab(CCPM.CREATIVE_TAB);
	}

	@Override
	public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon)
	{
		return true;
	}
}
