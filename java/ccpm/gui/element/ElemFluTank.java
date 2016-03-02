package ccpm.gui.element;

import DummyCore.Client.GuiElement;
import DummyCore.Utils.DrawUtils;
import DummyCore.Utils.MathUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;

public class ElemFluTank extends GuiElement {

	int x;
	int y;
	IFluidTank tank;
	
	ResourceLocation text = new ResourceLocation("");
	
	public ElemFluTank(int x, int y, IFluidTank tank) {
		this.x = x;
		this.y = y;
		this.tank = tank;
	}

	@Override
	public ResourceLocation getElementTexture() {
		
		return text;
	}

	@Override
	public void draw(int posX, int posY, int mouseX, int mouseY) {
		int scale = MathUtils.pixelatedTextureSize(tank.getFluidAmount(), tank.getCapacity(), 52);
		
		DrawUtils.drawTexturedModalRect(posX, posY, 0, 0, 18, 54, 0);
		
		DrawUtils.bindTexture(tank.getFluid().getFluid().getStill().getResourceDomain(), tank.getFluid().getFluid().getStill().getResourcePath());
		
		DrawUtils.drawTexturedModalRect(posX+1, posY+1, textureX, textureY, sizeX, sizeY, zLevel);
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

}
