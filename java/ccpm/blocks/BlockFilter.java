package ccpm.blocks;

import ccpm.tiles.TileEntityFilter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockFilter extends Block implements ITileEntityProvider {

	public BlockFilter() {
		super(Material.rock);
		
		this.setBlockName("ccpm.filter");
        this.setHardness(1.0F);
        this.setResistance(6.0F);
        //this.setLightLevel(1.0F);
        this.setHarvestLevel("pickaxe", 0);
        this.setStepSound(soundTypeMetal);
        //this.lightValue = 5;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityFilter();
	}
	
	IIcon i = null;
	
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int i, int j)
    {
		return this.i;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {		
		i = reg.registerIcon("ccpm:filter");
    }
    

}
