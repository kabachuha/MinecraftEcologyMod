package ccpm.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Utils.BlockStateMetadata;
import DummyCore.Utils.IOldCubicBlock;
import ccpm.tiles.TileAdvThaum;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAdvThaum extends Block implements IOldCubicBlock, ITileEntityProvider  {

	public BlockAdvThaum() {
		super(Material.rock);
		this.setUnlocalizedName("ccpm.energycell.thaum");
		this.setHardness(10.0F);
        this.setResistance(60.0F);
        this.setLightLevel(20.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setStepSound(soundTypeMetal);
        this.setDefaultState(BlockStateMetadata.createDefaultBlockState(this));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileAdvThaum("advThaum",0);
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
	public Icon getIcon(int side, int meta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IBlockState> listPossibleStates(Block b) {
		return Arrays.asList(new IBlockState[]{this.getDefaultState()});
	}

	@Override
	public void registerBlockIcons(IconRegister ir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getDCRenderID() {
		return RenderAccessLibrary.RENDER_ID_NONE;
	}

	

}
