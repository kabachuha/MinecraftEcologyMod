package ecomod.common.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.common.items.ItemCore;
import ecomod.common.tiles.TileFrame;
import ecomod.core.EMConsts;
import ecomod.core.stuff.EMConfig;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFrame extends Block implements ITileEntityProvider
{
	public static ItemStack oc_adapter = null;
	
	//Meta: 0 - Basic, 1 - Advanced
	
	public BlockFrame()
	{
		super(Material.rock);
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);

		this.setHardness(10F);
		this.setResistance(3.5F);
	}

	
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
		list.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
		list.add(new ItemStack(Item.getItemFromBlock(this), 1, 1));
    }
	
	public int damageDropped(int meta)
    {
		return meta;
    }
	
	public MapColor getMapColor(int meta)
    {
		return meta == 0 ? MapColor.grayColor : MapColor.diamondColor;
    }
	
	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return false;
	}


	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, world.getBlockMetadata(x, y, z));
	}


	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int meta, float hitx, float hity, float hitz)
	{
		meta = worldIn.getBlockMetadata(x, y, z);
		if(!worldIn.isRemote)
		{
			if(player != null)
			{
				ItemStack pi = player.getHeldItem();
				
				if(pi != null && pi.stackSize > 0)
				{
					if(pi.getItem() instanceof ItemCore)
					{
						if(meta == 0)
						{
							if(pi.getItemDamage() == 0)//Filter
							{
								worldIn.playAuxSFX(1021, x, y, z, 0);
								
								worldIn.setBlock(x, y, z, EcomodBlocks.FILTER);
								
								worldIn.notifyBlockChange(x, y, z, EcomodBlocks.FILTER);
								
								--pi.stackSize;
								
								player.setCurrentItemOrArmor(0, pi.stackSize <= 0 ? null : pi);
								
								return true;
							}
							
							if(pi.getItemDamage() == 2)//Analyzer
							{
								worldIn.playAuxSFX(1021, x, y, z, 0);
								
								worldIn.setBlock(x, y, z, EcomodBlocks.ANALYZER);
								
								worldIn.notifyBlockChange(x, y, z, EcomodBlocks.ANALYZER);
								
								--pi.stackSize;
								
								player.setCurrentItemOrArmor(0, pi.stackSize <= 0 ? null : pi);
								
								return true;
							}
						}
						else if(meta == 1)
						{
							if(pi.getItemDamage() == 1 && EMConfig.enable_advanced_filter)//Advanced Filter
							{
								worldIn.playAuxSFX(1021, x, y, z, 0);
								
								worldIn.setBlock(x, y, z, EcomodBlocks.ADVANCED_FILTER);
								
								worldIn.notifyBlockChange(x, y, z, EcomodBlocks.ADVANCED_FILTER);
								
								--pi.stackSize;
								
								player.setCurrentItemOrArmor(0, pi.stackSize <= 0 ? null : pi);
								
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
								worldIn.playAuxSFX(1021, x, y, z, 0);
								
								worldIn.setBlock(x, y, z, EcomodBlocks.OC_ANALYZER_ADAPTER);
								
								worldIn.notifyBlockChange(x, y, z, EcomodBlocks.OC_ANALYZER_ADAPTER);
								
								--pi.stackSize;
								
								player.setCurrentItemOrArmor(0, pi.stackSize <= 0 ? null : pi);
								
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
	public String getUnlocalizedName() {
		return "tile."+EMConsts.modid+".frame";
	}

	public IIcon icon$basic_frame_1;
	public IIcon icon$basic_frame_2;
	public IIcon icon$advanced_frame_1;
	public IIcon icon$advanced_frame_2;

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if(meta == 0)
			return icon$basic_frame_1;
		
		if(meta == 1)
			return icon$advanced_frame_1;
		
		if(meta == 2)
			return icon$basic_frame_2;
		
		if(meta == 3)
			return icon$advanced_frame_2;
		
		return super.getIcon(side, meta);
	}


	@Override
	public void registerBlockIcons(IIconRegister reg)
	{
		icon$basic_frame_1 = reg.registerIcon("ecomod:basic_frame_1");
		icon$basic_frame_2 = reg.registerIcon("ecomod:basic_frame_2");
		icon$advanced_frame_1 = reg.registerIcon("ecomod:advanced_frame_1");
		icon$advanced_frame_2 = reg.registerIcon("ecomod:advanced_frame_2");
	}
	
	@Override
    public int getRenderBlockPass()
    {
        return 0;
    }
	
	@Override
	public int getRenderType()
    {
        return 226;
    }
	
	@Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileFrame();
	}
}
