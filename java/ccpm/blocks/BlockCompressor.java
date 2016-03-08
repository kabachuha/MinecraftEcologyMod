package ccpm.blocks;

import java.util.Arrays;
import java.util.List;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Utils.BlockStateMetadata;
import DummyCore.Utils.IOldCubicBlock;
import DummyCore.Utils.MiscUtils;
import ccpm.gui.CCPMGuis;
import ccpm.tiles.TileCompressor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCompressor extends Block implements IOldCubicBlock, ITileEntityProvider {

	public BlockCompressor() {
		super(Material.rock);
		this.setUnlocalizedName("ccpm.compressor");
		this.setHardness(20.0F);
        this.setResistance(70.0F);
        this.setLightLevel(6.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setStepSound(soundTypeMetal);
        this.setDefaultState(BlockStateMetadata.createDefaultBlockState(this));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCompressor();
	}

	Icon bot;
	Icon pipe;
	Icon side;
	
	@Override
	public Icon getIcon(int side, int meta) {
		if(meta != -1 && side == EnumFacing.UP.getIndex())
			return pipe;
		if(side == EnumFacing.DOWN.getIndex())
			return bot;
		
		
		return this.side;
	}

	@Override
	public Icon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if(side == ((TileCompressor)world.getTileEntity(new BlockPos(x,y,z))).pipeConDir.getIndex())
			return pipe;
		
		return getIcon(side, -1);
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

	@Override
	public void registerBlockIcons(IconRegister ir) {
		bot = ir.registerBlockIcon("ccpm:compressor_bottom");
		pipe = ir.registerBlockIcon("ccpm:pipeConDir");
		side = ir.registerBlockIcon("ccpm:compressor");
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitx, float hitY, float hitZ)
	{
		MiscUtils.openGui(worldIn, pos.getX(), pos.getY(), pos.getZ(), playerIn, CCPMGuis.guiCompressorID);
		return true;
	}
}
