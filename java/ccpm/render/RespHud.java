package ccpm.render;

import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import ccpm.items.RespiratorBase;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RespHud {

	public static final ResourceLocation respTexture = new ResourceLocation(CCPM.MODID+":textures/hud/respHud.png");
	public RespHud() {
		
	}

	public boolean display() {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		ItemStack respStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(respStack != null && respStack.getItem() instanceof IRespirator)
		{
		//FMLLog.info("CKP CKP CKP");
		IRespirator resp = (IRespirator) respStack.getItem();
		
		if(resp.renderHud())
		{
			return CCPMConfig.enableHUD;
		}
		}
		return false;
	}

	public void draw(int i, int j, float partialTicks, ScaledResolution res) {
		int h = res.getScaledHeight();
		int w = res.getScaledWidth();

		Tessellator tess = Tessellator.getInstance();
		//Bind the texture
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(respTexture.getResourceDomain(), respTexture.getResourcePath()));
		VertexBuffer vb = tess.getBuffer();
		
		int z = 128;
		
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);
		
		vb.pos(0, h, z).tex(0, 1).endVertex();
		
		vb.pos(w, h, z).tex(1, 1).endVertex();
		
		vb.pos(w, 0, z).tex(1, 0).endVertex();
		
		vb.pos(0, 0, z).tex(0, 0).endVertex();
		
		tess.draw();
	}

	public boolean displayInGUIs() {

		return false;
	}

}
