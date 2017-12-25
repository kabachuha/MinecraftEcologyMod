package ecomod.common.blocks;

import ecomod.api.EcomodStuff;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAnalyzer extends Block implements ITileEntityProvider {

	public BlockAnalyzer()
	{
		super(Material.rock);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		this.setHardness(8F);
		this.setResistance(5F);
		
		this.setHarvestLevel("pickaxe", 1);
	}

	public boolean isNormalCube()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileAnalyzer();
	}

	@Override
	public MapColor getMapColor(int p_149728_1_) {
		return MapColor.brownColor;
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int meta, float hitx, float hity, float hitz) {
		
		if(playerIn == null ? true : !playerIn.isSneaking())
    	{
    		if(!worldIn.isRemote)
    		{
    			((TileAnalyzer)worldIn.getTileEntity(x, y, z)).sendUpdatePacket();
    		}
    		EcologyMod.proxy.openGUIAnalyzer(playerIn, (TileAnalyzer)worldIn.getTileEntity(x, y, z));
    	}
    	
		return true;
	}
	
	public String getUnlocalizedName()
	{
		return EMConsts.modid+".analyzer";
	}
}
