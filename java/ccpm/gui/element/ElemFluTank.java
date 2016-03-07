package ccpm.gui.element;

import DummyCore.Client.GuiElement;
import DummyCore.Utils.DrawUtils;
import DummyCore.Utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class ElemFluTank extends GuiElement {

	int x;
	int y;
	FluidStack fluid;
	int capacity;
	
	ResourceLocation text = new ResourceLocation("ccpm:textures/gui/tank.png");
	
	public ElemFluTank(int x, int y, IFluidTank tank) {
		this.x = x;
		this.y = y;
		this.fluid = tank.getFluid();
		capacity = tank.getCapacity();
	}
	
	public ElemFluTank(int x, int y, IFluidHandler tank, int index) {
		this.x = x;
		this.y = y;
		this.fluid = tank.getTankInfo(EnumFacing.DOWN)[index].fluid;
		capacity = tank.getTankInfo(EnumFacing.DOWN)[index].capacity;
	}

	@Override
	public ResourceLocation getElementTexture() {
		
		return text;
	}

	@Override
	public void draw(int posX, int posY, int mouseX, int mouseY) {
		DrawUtils.drawTexturedModalRect(posX, posY, 0, 0, 18, 54, 0);
		if(fluid!=null)
		{
			if(fluid.amount >0) 
			{
				DrawUtils.bindTexture(fluid.getFluid().getStill().getResourceDomain(), fluid.getFluid().getStill().getResourcePath());
				int scale = MathUtils.pixelatedTextureSize(fluid.amount, capacity, 52);
				DrawUtils.drawTexturedModalRect(posX+1, posY+1+(52-scale), 0, 0, 16, scale, 1);
			}
		}
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
