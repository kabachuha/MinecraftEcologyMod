package DummyCore.Client;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * ---From DummyCore---
 * 
 * A very simple GUI 'abstract' class(not really abstract, but is intended to be)
 * This allows a modmaker to create a very simple GUI without having to draw it at all
 * @author Modbder(https://github.com/Modbder)
 *
 */
public class GuiCommon extends GuiContainer{
	
	public List<GuiElement> elementList = new ArrayList<GuiElement>();
	public TileEntity genericTile;
	//TODO change to a custom .png image to support resource packs properly
	public ResourceLocation guiGenLocation = new ResourceLocation("textures/gui/container/dispenser.png");
	public ResourceLocation slotLocation = new ResourceLocation("textures/gui/container/dispenser.png");

	/**
	 * A default constructor. Deprecated. Use the one below
	 * @param c - the container(inventory) for this GUI
	 */
	public GuiCommon(Container c) {
		super(c);
	}
	
	
	/**
	 * The constructor you should use.
	 * @param c - the container(inventory) for this GUI
	 * @param tile - the TileEntity for this GUI
	 */
	public GuiCommon(Container c, TileEntity tile) {
		this(c);
		genericTile = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f1,int i1, int i2) {
		GL11.glColor3f(1, 1, 1);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.mc.renderEngine.bindTexture(guiGenLocation);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(k+60, l+16, 6, 6, 28, 54);
        this.drawTexturedModalRect(k+88, l+16, 6, 6, 28, 54);
		for(int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i)
		{
			Slot slt = (Slot) this.inventorySlots.inventorySlots.get(i);
			renderSlot(slt);
			GL11.glColor3f(1, 1, 1);
		}
		for(int i = 0; i < this.elementList.size(); ++i)
		{
			GuiElement element = elementList.get(i);
			Minecraft.getMinecraft().renderEngine.bindTexture(element.getElementTexture());
			element.draw(k+element.getX(),l+element.getY(),i1,i2);
			GL11.glColor3f(1, 1, 1);
		}
		GL11.glColor3f(1, 1, 1);
	}
	
	public void renderSlot(Slot slt)
	{
		GL11.glColor3f(1, 1, 1);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.mc.renderEngine.bindTexture(slotLocation);
		this.drawTexturedModalRect(k+slt.xDisplayPosition-1, l+slt.yDisplayPosition-1, 7, 83, 18, 18);
	}
	
	

}
