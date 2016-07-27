package ccpm.blocks;

import java.util.ArrayList;
import java.util.List;

import ccpm.core.CCPM;
import ccpm.tiles.TileEnergyCellBasic;
import ccpm.tiles.TileEnergyCellMana;
import ccpm.tiles.TileEnergyCellRf;
import ccpm.tiles.TileEnergyCellThaumium;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnergyCellBase extends Block implements ITileEntityProvider {

	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	
	public BlockEnergyCellBase() {
		super(Material.ROCK);
		
		this.setUnlocalizedName("ccpm.energycell");
        this.setHardness(1.0F);
        this.setResistance(6.0F);
        this.setLightLevel(1.0F);
        this.setHarvestLevel("pickaxe", 0);
        this.lightValue = 5;
        this.setCreativeTab(CCPM.CREATIVE_TAB);
        this.setDefaultState(createBlockState().getBaseState());
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
    	for(int i = 0; i < Type.values().length; ++i)
    		p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
    }
	
	enum Type implements IStringSerializable
	{
		RF;

		@Override
		public String getName()
		{
			return "rf";
		}
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(TYPE, Type.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(TYPE).ordinal();
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, TYPE);
	}
}
