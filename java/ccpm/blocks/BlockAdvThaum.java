package ccpm.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAdvThaum extends Block/* implements ITileEntityProvider*/  {

	public BlockAdvThaum() {
		super(Material.ROCK);
		this.setUnlocalizedName("ccpm.energycell.thaum");
		this.setHardness(10.0F);
        this.setResistance(60.0F);
        this.setLightLevel(20.0F);
        this.setHarvestLevel("pickaxe", 2);
	}

	//@Override
	//public TileEntity createNewTileEntity(World worldIn, int meta) {
	//	return new TileAdvThaum("advThaum",0);
	//}
}
