package ccpm.gui;

import DummyCore.Client.GuiCommon;
import ccpm.api.IHasProgress;
import ccpm.gui.element.ElemFluTank;
import ccpm.gui.element.ProgressBar;
import ccpm.gui.element.RedstoneIndicator;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

public class GuiCompressor extends GuiCommon {

	public GuiCompressor(Container c, TileEntity tile) {
		super(c, tile);
		
		this.elementList.add(new ElemFluTank(this.xSize/2-12, this.ySize/8-27, (IFluidHandler)tile, 0));
		this.elementList.add(new RedstoneIndicator(this.xSize-18, this.guiTop-2, tile));
		this.elementList.add(new ProgressBar(this.xSize/2+16, this.ySize/8-8, (IHasProgress)tile));
	}

}
