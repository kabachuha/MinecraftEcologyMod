package ccpm.blocks;

import java.util.List;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Utils.BlockStateMetadata;
import DummyCore.Utils.IOldCubicBlock;
import ccpm.tiles.TileEntityAnalyser;
import ccpm.tiles.TileEntityFilter;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

public class BlockAnalyser extends Block implements ITileEntityProvider, IOldCubicBlock {

	public BlockAnalyser() {
		
			super(Material.rock);
			
			this.setUnlocalizedName("ccpm.analyser");
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
			return new TileEntityAnalyser();
		}
		
		Icon i = null;
		
		
		@Override
	    @SideOnly(Side.CLIENT)
	    public void registerBlockIcons(IconRegister reg)
	    {		
			i = reg.registerBlockIcon("ccpm:analyser");
	    }

		@Override
		@SideOnly(Side.CLIENT)
		public Icon getIcon(int side, int meta) {
			return this.i;
		}

		@Override
		public Icon getIcon(IBlockAccess world, int x, int y, int z, int side) {
			return i;
		}

		@Override
		public List<IBlockState> listPossibleStates(Block b) {
			return Arrays.asList(new IBlockState[]{getDefaultState()});
		}

		@Override
		public int getDCRenderID() {
			return RenderAccessLibrary.RENDER_ID_CUBE;
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
}
