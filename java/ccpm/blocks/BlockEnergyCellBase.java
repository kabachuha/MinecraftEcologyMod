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

public class BlockEnergyCellBase extends Block implements ITileEntityProvider {

	static int vars = 1;
	
	public BlockEnergyCellBase() {
		super(Material.ROCK);
		
		this.setUnlocalizedName("ccpm.energycell");
        this.setHardness(1.0F);
        this.setResistance(6.0F);
        this.setLightLevel(1.0F);
        this.setHarvestLevel("pickaxe", 0);
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
    	for(int i = 0; i < vars; ++i)
    		p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
    }
}
