package ccpm.blocks;

import java.util.Arrays;
import java.util.List;

import ccpm.core.CCPM;
import ccpm.tiles.TileEntityAnalyser;
import ccpm.tiles.TileEntityFilter;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class BlockAnalyser extends Block implements ITileEntityProvider {

	public BlockAnalyser() {
		
			super(Material.ROCK);
			
			this.setUnlocalizedName("ccpm.analyser");
	        this.setHardness(1.0F);
	        this.setResistance(6.0F);
	        //this.setLightLevel(1.0F);
	        this.setHarvestLevel("pickaxe", 0);
	        //this.lightValue = 5;
	        this.setCreativeTab(CCPM.CREATIVE_TAB);
		}

		@Override
		public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
			return new TileEntityAnalyser();
		}
}
