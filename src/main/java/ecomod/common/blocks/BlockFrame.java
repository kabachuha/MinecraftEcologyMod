package ecomod.common.blocks;

import java.util.List;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.common.items.ItemCore;
import ecomod.core.stuff.EMConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFrame extends Block
{
	@ItemStackHolder("opencomputers:adapter")
	public static ItemStack oc_adapter = null;
	
	//0 - Basic, 1 - Advanced
	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 1);
	
	public BlockFrame()
	{
		super(Material.ROCK);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);

		this.setHardness(10F);
		this.setResistance(3.5F);
	}

	
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
		list.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
		list.add(new ItemStack(Item.getItemFromBlock(this), 1, 1));
    }
	
	public int damageDropped(IBlockState state)
    {
		return state.getValue(TYPE);
    }
	
	public MapColor getMapColor(IBlockState state)
    {
		return state.getValue(TYPE) == 0 ? MapColor.GRAY : MapColor.DIAMOND;
    }
	
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	public IBlockState getStateFromMeta(int meta)
    {
		return getDefaultState().withProperty(TYPE, meta);
    }
	
	public int getMetaFromState(IBlockState state)
    {
		return state.getValue(TYPE);
    }
	
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {TYPE});
    }
	
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	public boolean isFullyOpaque(IBlockState state)
    {
		return true;
    }
	
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return Block.FULL_BLOCK_AABB;
    }

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			if(playerIn != null)
			{
				ItemStack pi = playerIn.getHeldItem(hand);
				
				if(pi != null && pi.stackSize > 0)
				{
					if(EMConfig.enable_manually_assembly && pi.getItem() instanceof ItemCore)
					{
						if(state.getValue(TYPE) == 0)
						{
							if(pi.getMetadata() == 0)//Filter
							{
								worldIn.playEvent(1029, pos, 0);
								
								worldIn.setBlockState(pos, EcomodBlocks.FILTER.getDefaultState());
								
								playerIn.setHeldItem(hand, --pi.stackSize < 1 ? null : pi);
								
								return true;
							}
							
							if(pi.getMetadata() == 2)//Analyzer
							{
								worldIn.playEvent(1029, pos, 0);
								
								worldIn.setBlockState(pos, EcomodBlocks.ANALYZER.getDefaultState());
								
								playerIn.setHeldItem(hand, --pi.stackSize < 1 ? null : pi);
								
								return true;
							}
						}
						else if(state.getValue(TYPE) == 1)
						{
							if(pi.getMetadata() == 1 && EMConfig.enable_advanced_filter)//Advanced Filter
							{
								worldIn.playEvent(1029, pos, 0);
								
								worldIn.setBlockState(pos, EcomodBlocks.ADVANCED_FILTER.getDefaultState());
								
								playerIn.setHeldItem(hand, --pi.stackSize < 1 ? null : pi);
								
								return true;
							}
						}
					}
					else
					{
						if(EMConfig.is_oc_analyzer_interface_crafted_by_right_click)
						if(oc_adapter != null)
						{
							if(oc_adapter.getItem() == pi.getItem())
							{
								worldIn.playEvent(1029, pos, 0);
								
								worldIn.setBlockState(pos, EcomodBlocks.OC_ANALYZER_ADAPTER.getDefaultState());
								
								playerIn.setHeldItem(hand, --pi.stackSize < 1 ? null : pi);
								
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
	    return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
	}
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return false;
	}


	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}
	
	
}
