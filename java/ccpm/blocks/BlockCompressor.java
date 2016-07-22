package ccpm.blocks;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import ccpm.core.CCPM;
import ccpm.tiles.TileCompressor;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCompressor extends Block implements ITileEntityProvider {

	public BlockCompressor() {
		super(Material.ROCK);
		this.setUnlocalizedName("ccpm.compressor");
		this.setHardness(20.0F);
        this.setResistance(70.0F);
        this.setLightLevel(6.0F);
        this.setHarvestLevel("pickaxe", 3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCompressor();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(playerIn.isSneaking())return false;
		
		if(!CCPMConfig.needStructure || isStructureBuilt(pos,worldIn, playerIn))
			playerIn.openGui(CCPM.instance, CCPMConfig.guiCompressorId, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	
	
	private boolean checkIron(BlockPos pos, World w, EntityPlayer p)
	{
		if(w.getBlockState(pos) == Blocks.IRON_BLOCK.getDefaultState())
		{
			return true;
		}
		else
		{
			TextComponentString cct = new TextComponentString("Iron block is missing at "+pos.toString()+"!");
			cct.setStyle(cct.getStyle().setColor(TextFormatting.RED));
			if(CCPMConfig.needStructure)
			p.addChatMessage(cct);
			
			if(!w.isRemote)
				w.newExplosion(p, pos.getX()+0.5F, pos.getY()+0.5F, pos.getZ()+0.5F, 0.1F, true, true);
			
			return false;
		}
	}
	
	private boolean isStructureBuilt(BlockPos p, World w, EntityPlayer ep)
	{
		boolean ret = true;
		
		ret = ret && checkIron(p.add(1, 1, 1),w,ep);//+
		ret = ret && checkIron(p.add(1, -1, 1),w,ep);//+
		ret = ret && checkIron(p.add(-1, 1, -1),w,ep);//+
		ret = ret && checkIron(p.add(-1, -1, -1),w,ep);//+
		ret = ret && checkIron(p.add(1, -1, -1),w,ep);//+
		ret = ret && checkIron(p.add(1, 1, -1),w,ep);//+
		ret = ret && checkIron(p.add(-1, 1, 1),w,ep);//+
		ret = ret && checkIron(p.add(-1, -1, 1),w,ep);//+
		
		if(CCPMConfig.needStructure)
		if(!ret)
		{
			ep.addChatMessage(new TextComponentString("Structure isn't completed!"));
		}
		
		return ret;
	}
	
	
}
