package ccpm.gui;

import DummyCore.Client.GuiCommon;
import DummyCore.Utils.ContainerInventory;
import ccpm.api.IHasProgress;
import ccpm.gui.element.ElemFluTank;
import ccpm.gui.element.ProgressBar;
import ccpm.gui.element.RedstoneIndicator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidHandler;

public class GuiAdvFilter extends GuiCommon {


	public GuiAdvFilter(Container c, TileEntity tile) {
		super(c, tile);
		this.elementList.add(new ElemFluTank(this.xSize/2+16, this.ySize/8-9, (IFluidHandler)tile, 0));
		this.elementList.add(new RedstoneIndicator(this.xSize-22, this.guiTop-8, tile));
		this.elementList.add(new ProgressBar(this.xSize/2-12, this.ySize/8-8, (IHasProgress)tile));
	}

	
	
	
	
}
