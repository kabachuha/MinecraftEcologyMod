
package ccpm.gui.element;

import DummyCore.Client.GuiElement;
import ccpm.utils.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class ElemFluTank extends GuiElement {

	int x;
	int y;
	//FluidStack fluid;
	//int capacity;
	
	ResourceLocation text = new ResourceLocation("ccpm:textures/gui/tank.png");
	
	IFluidTank tank;
	
	public ElemFluTank(int x, int y, IFluidTank tank) {
		this.x = x;
		this.y = y;
		//this.fluid = tank.getFluid();
		////capacity = tank.getCapacity();
		
		this.tank = tank;
	}
	
	public ElemFluTank(int x, int y, IFluidHandler tank, int index) {
		this.x = x;
		this.y = y;
		//this.fluid = tank.getTankInfo(EnumFacing.DOWN)[index].fluid;
		//capacity = tank.getTankInfo(EnumFacing.DOWN)[index].capacity;
		
		this.tank = new FluidTank(tank.getTankInfo(EnumFacing.DOWN)[index].fluid, tank.getTankInfo(EnumFacing.DOWN)[index].capacity);
	}

	@Override
	public ResourceLocation getElementTexture() {
		
		return text;
	}

	@Override
	public void draw(int posX, int posY, int mouseX, int mouseY) {
		//DrawUtils.drawTexturedModalRect(posX, posY, 0, 0, 18, 54, 1);
		this.drawTexturedModalRect(posX, posY, 0, 0, 18, 54);
		this.drawTexturedModalRect(posX, posY+53, 0, 71, 18, 1);
		
		FluidStack fluid = tank.getFluid();
		
		if(fluid!=null)
		{
			if(fluid.amount >0) 
			{
				GlStateManager.pushMatrix();
				
				int scale = MiscUtils.pixelatedTextureSize(fluid.amount, tank.getCapacity(), 52);
			//	DrawUtils.drawTexturedModalRect(posX+1, posY+1+(52-scale), 0, 0, 16, scale, 2);
				TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill(fluid).toString());
				
				//DrawUtils.bindTexture(fluid.getFluid().getStill().getResourceDomain(), fluid.getFluid().getStill().getResourcePath());
				//DrawUtils.drawTexturedModalRect(posX+1, posY+1+(52-scale),0,0, 16, scale, 1);
				
				GlStateManager.color((fluid.getFluid().getColor(fluid) >> 16 & 255) / 255.0F, (fluid.getFluid().getColor(fluid) >> 8 & 255) / 255.0F, (fluid.getFluid().getColor(fluid) & 255) / 255.0F, 1.0F);
				
				MiscUtils.drawScaledTexturedRectFromTAS(posX+1, posY+1+(52-scale), icon, 16, scale, 0);
				
				GlStateManager.popMatrix();
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
