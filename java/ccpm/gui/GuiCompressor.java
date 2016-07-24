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
		
		this.elementList.add(new ElemFluTank(this.guiLeft+this.xSize/2-64, this.guiTop+this.ySize/8-8, (IFluidHandler)tile, 0));
		this.elementList.add(new RedstoneIndicator(this.xSize-22, this.guiTop+16, tile));
		this.elementList.add(new ProgressBar(this.guiLeft+this.xSize/2-26, this.guiTop+this.ySize/8+22, (IHasProgress)tile));
	}


	

}
