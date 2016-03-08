package ccpm.gui.element;

import DummyCore.Client.GuiElement;
import DummyCore.Utils.DrawUtils;
import DummyCore.Utils.TessellatorWrapper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RedstoneIndicator extends GuiElement {

	ResourceLocation pow = new ResourceLocation("ccpm:textures/gui/rt.png");
	ResourceLocation unpow = new ResourceLocation("ccpm:textures/gui/rtoff.png");
	int x;
	int y;
	
	TileEntity ttc;
	public RedstoneIndicator(int x, int y, TileEntity tile) {
		super();
		ttc = tile;
		this.x = x;
		this.y = y;
	}

	@Override
	public ResourceLocation getElementTexture() {
		return ttc.getWorld().isBlockPowered(ttc.getPos()) ? pow : unpow;
	}

	@Override
	public void draw(int posX, int posY, int mouseX, int mouseY) {
		DrawUtils.drawTexturedModalRect(posX, posY, 0, 0, 16, 16, 0);
		
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

}
