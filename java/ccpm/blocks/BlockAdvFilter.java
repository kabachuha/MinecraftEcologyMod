package ccpm.blocks;

import javax.annotation.Nullable;

import ccpm.core.CCPM;
import ccpm.tiles.AdvancedAirFilter;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAdvFilter extends BlockFilter {

	public BlockAdvFilter() 
	{
		super();
		
        this.setHardness(20.0F);
        this.setResistance(90.0F);
        //this.setLightLevel(1.0F);
        this.setHarvestLevel("pickaxe", 2);
        //this.lightValue = 5;      
	}

	
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new AdvancedAirFilter();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		playerIn.openGui(CCPM.instance, CCPMConfig.guiAdvFilterId, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
