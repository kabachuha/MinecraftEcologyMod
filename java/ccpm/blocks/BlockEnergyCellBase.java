package ccpm.blocks;

import java.util.List;

import DummyCore.Utils.ReflectionProvider;
import ccpm.core.CCPM;
import ccpm.tiles.TileEnergyCellBasic;
import ccpm.tiles.TileEnergyCellMana;
import ccpm.tiles.TileEnergyCellRf;
import ccpm.tiles.TileEnergyCellThaumium;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEnergyCellBase extends Block implements ITileEntityProvider {

	public IIcon[] icons = new IIcon[3];

	public BlockEnergyCellBase() {
		super(Material.rock);
		
		this.setBlockName("ccpm.energycell");
        this.setHardness(1.0F);
        this.setResistance(6.0F);
        this.setLightLevel(1.0F);
        this.setHarvestLevel("pickaxe", 0);
        this.setStepSound(soundTypeMetal);
        this.lightValue = 5;
	}

	@Override
	public TileEntity createNewTileEntity(World w, int m) {
		if(Loader.isModLoaded("Thaumcraft") && m == 1)
		{
			return new TileEnergyCellThaumium("ccpmCellThaum", 0);
		}
		if(Loader.isModLoaded("Botania") && m == 2)
		{
			return new TileEnergyCellMana("ccpmCellMana", 10000);
		}
		
		return new TileEnergyCellRf("ccpmCellRf", 100000);
	}

	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
    	for(int i = 0; i < icons.length; ++i)
    		p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
    }

	@Override
    public int damageDropped(int p_149692_1_)
    {
        return p_149692_1_;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int i, int j)
    {
		return icons[j];
    }

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {		
			icons[0] = reg.registerIcon("ccpm:ccpmEnergyCellRf");
			icons[1] = reg.registerIcon("ccpm:ccpmEnergyCellThaumium");
			icons[2] = reg.registerIcon("ccpm:ccpmEnergyCellMana");
    }
}
