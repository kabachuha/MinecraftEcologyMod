package ccpm.render;

import DummyCore.Utils.DrawUtils;
import DummyCore.Utils.EnumGuiPosition;
import DummyCore.Utils.IHUDElement;
import DummyCore.Utils.TessellatorWrapper;
import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import ccpm.items.RespiratorBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RespHud implements IHUDElement {

	public static final ResourceLocation respTexture = new ResourceLocation(CCPM.MODID+":textures/hud/respHud.png");
	public RespHud() {
		
	}

	@Override
	public int getXOffset() {

		return 0;
	}

	@Override
	public int getYOffset() {

		return 0;
	}

	@Override
	public EnumGuiPosition offsetPoint() {

		return EnumGuiPosition.TOPLEFT;
	}

	@Override
	public boolean display() {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		ItemStack respStack = player.getEquipmentInSlot(4);
		if(respStack != null && respStack.getItem() instanceof IRespirator)
		{
		//FMLLog.info("CKP CKP CKP");
		IRespirator resp = (IRespirator) respStack.getItem();
		
		if(resp.renderHud())
		{
			return true;
		}
		}
		return false;
	}

	@Override
	public void draw(int i, int j, float partialTicks, ScaledResolution res) {
		int h = res.getScaledHeight();
		int w = res.getScaledWidth();

		TessellatorWrapper tess = TessellatorWrapper.instance;
		//Bind the texture
		DrawUtils.bindTexture(respTexture.getResourceDomain(), respTexture.getResourcePath());
		tess.startDrawingQuads();
		
		tess.addVertexWithUV(0, h, 0, 0, 1);
		
		tess.addVertexWithUV(w, h, 0, 1, 1);
		
		tess.addVertexWithUV(w, 0, 0, 1, 0);
		
		tess.addVertexWithUV(0, 0, 0, 0, 0);
		
		if(Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(4) != null && Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(4).getItem() instanceof RespiratorBase)
		{
			if(((RespiratorBase)Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(4).getItem()).isRev(Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(4)))
				tess.setColorRGBA_F(1, 0, 1, Minecraft.getMinecraft().theWorld.rand.nextFloat());
		}
		
		tess.draw();
	}

	@Override
	public boolean displayInGUIs() {

		return true;
	}

}
