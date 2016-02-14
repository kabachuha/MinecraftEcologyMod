package ccpm.blocks;

import java.util.ArrayList;
import java.util.List;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Utils.BlockStateMetadata;
import DummyCore.Utils.IOldCubicBlock;
import DummyCore.Utils.ReflectionProvider;
import ccpm.core.CCPM;
import ccpm.tiles.TileEnergyCellBasic;
import ccpm.tiles.TileEnergyCellMana;
import ccpm.tiles.TileEnergyCellRf;
import ccpm.tiles.TileEnergyCellThaumium;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnergyCellBase extends Block implements ITileEntityProvider, IOldCubicBlock {

	public Icon[] icons = new Icon[2];

	public BlockEnergyCellBase() {
		super(Material.rock);
		
		this.setUnlocalizedName("ccpm.energycell");
        this.setHardness(1.0F);
        this.setResistance(6.0F);
        this.setLightLevel(1.0F);
        this.setHarvestLevel("pickaxe", 0);
        this.setStepSound(soundTypeMetal);
        this.lightValue = 5;
	}

	@Override
	public TileEntity createNewTileEntity(World w, int m) {
		if((Loader.isModLoaded("thaumcraft") || Loader.isModLoaded("Thaumcraft")) && m == 1)
		{
			return new TileEnergyCellThaumium("ccpmCellThaum", 0);
		}
		//if(Loader.isModLoaded("Botania") && m == 2)
		//{
		//	return new TileEnergyCellMana("ccpmCellMana", 10000);
		//}
		
		return new TileEnergyCellRf("ccpmCellRf", 100000);
	}

	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
    	for(int i = 0; i < icons.length; ++i)
    		p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
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
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int i, int j)
    {
		return icons[j];
    }

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IconRegister reg)
    {		
			icons[0] = reg.registerBlockIcon("ccpm:ccpmEnergyCellRf");
			icons[1] = reg.registerBlockIcon("ccpm:ccpmEnergyCellThaumium");
			//icons[2] = reg.registerBlockIcon("ccpm:ccpmEnergyCellMana");
    }

	@Override
	public Icon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return getIcon(side,BlockStateMetadata.getBlockMetadata(world, x, y, z));
	}

	@Override
	public List<IBlockState> listPossibleStates(Block b) {
		ArrayList<IBlockState> retLst = new ArrayList<IBlockState>();
		for(int i = 0; i < icons.length; ++i)
			retLst.add(getStateFromMeta(i));
		return retLst;
	}

	@Override
	public int getDCRenderID() {
		return RenderAccessLibrary.RENDER_ID_CUBE;
	}
	
	
	
}
