package ccpm.blocks;

import java.util.Arrays;
import java.util.List;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Utils.BlockStateMetadata;
import DummyCore.Utils.IOldCubicBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;

public class BlockPollutionBricks extends Block implements IOldCubicBlock {

	public BlockPollutionBricks() {
		super(Material.rock);
		this.setUnlocalizedName("ccpm.bricks.pollution");
        this.setHardness(4.0F);
        this.setResistance(60.0F);
        this.setLightLevel(6.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setStepSound(soundTypeStone);
        //this.lightValue = 5;
        this.setDefaultState(BlockStateMetadata.createDefaultBlockState(this));
	}

	Icon icon;
	
	@Override
	public Icon getIcon(int side, int meta) {
		return icon;
	}

	@Override
	public Icon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return icon;
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
	public void registerBlockIcons(IconRegister ir) {
		icon = ir.registerBlockIcon("ccpm:bricks_pollution");
	}

	@Override
	public int getDCRenderID() {
		return RenderAccessLibrary.RENDER_ID_CUBE;
	}

}
