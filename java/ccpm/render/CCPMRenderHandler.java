package ccpm.render;

import org.lwjgl.opengl.GL11;

import DummyCore.Utils.DrawUtils;
import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class CCPMRenderHandler {
	
	public static final ResourceLocation respTexture = new ResourceLocation(CCPM.MODID+":textures/hud/respHud.png");

	public CCPMRenderHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogColor(EntityViewRenderEvent.FogColors event)
	{
		
		if(isPlayerInSmog(Minecraft.getMinecraft().thePlayer))
		{
			//FMLLog.info("Coloring fog");
			event.red = 0.61F;
			event.green = 0.54F;
			event.blue = 0.54F;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogRender(EntityViewRenderEvent.RenderFogEvent event)
	{
		
		if(isPlayerInSmog(Minecraft.getMinecraft().thePlayer))
		{
			//FMLLog.info("Rendering fog");
			GL11.glFogf(GL11.GL_FOG_START, 1.5F); // 0.7
			GL11.glFogf(GL11.GL_FOG_END, 3.5F);
		}
	}
	
	
	
	
	boolean isPlayerInSmog(EntityPlayer p)
	{
		return p.isPotionActive(CCPM.smog);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderHud(RenderGameOverlayEvent.Pre event)
	{
		//FMLLog.info("renderHud called");
		// Resolution of the Minecraft
		ScaledResolution scRes = event.resolution;
		
		if(event.type != ElementType.EXPERIENCE)
			return;
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		
		
		//TODO Add the glass cracking by the respirator damage
		ItemStack respStack = player.getEquipmentInSlot(4);
		if(respStack != null && respStack.getItem() instanceof IRespirator)
		{
		//FMLLog.info("CKP CKP CKP");
		IRespirator resp = (IRespirator) respStack.getItem();
		
		if(resp.renderHud())
		{
			//FMLLog.info("Rendering Hud");
			//The magic of tessellator is starting
			int h = scRes.getScaledHeight();
			int w = scRes.getScaledWidth();

			Tessellator tess = Tessellator.instance;
			//Bind the texture
			DrawUtils.bindTexture(respTexture.getResourceDomain(), respTexture.getResourcePath());
			
			tess.startDrawingQuads();
			
			tess.addVertexWithUV(0, h, -90D, 0, 1);
			
			tess.addVertexWithUV(w, h, -90D, 1, 1);
			
			tess.addVertexWithUV(w, 0, -90D, 1, 0);
			
			tess.addVertexWithUV(0, 0, -90D, 0, 0);
			
			tess.draw();
		}
	}
	}
}
