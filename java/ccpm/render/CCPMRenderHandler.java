package ccpm.render;

import org.lwjgl.opengl.GL11;

import DummyCore.Utils.DrawUtils;
import ccpm.api.IRespirator;
import ccpm.core.CCPM;
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
	
	public static final ResourceLocation respTexture = new ResourceLocation(CCPM.MODID, "textures/hud/respHud");

	public CCPMRenderHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogColor(EntityViewRenderEvent.FogColors event)
	{
		if(isPlayerInSmog(Minecraft.getMinecraft().thePlayer))
		{
			event.red = 61;
			event.green = 54;
			event.blue = 54;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogRender(EntityViewRenderEvent.RenderFogEvent event)
	{
		if(isPlayerInSmog(Minecraft.getMinecraft().thePlayer))
		{
			GL11.glFogf(GL11.GL_FOG_START, 0.7F);
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
		// Resolution of the Minecraft
		ScaledResolution scRes = event.resolution;
		
		if(event.type == ElementType.ALL)
			return;
		
		if(event.type != ElementType.HELMET)
			return;
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		if(player.getCurrentArmor(0) == null || !(player.getCurrentArmor(0).getItem() instanceof IRespirator))
			return;
		//TODO Add the glass cracking by the respirator damage
		ItemStack respStack = player.getCurrentArmor(0);
		IRespirator resp = (IRespirator) respStack.getItem();
		
		if(resp.renderHud())
		{
			//The magic of tessellator is starting
			int h = scRes.getScaledHeight();
			int w = scRes.getScaledWidth();

			Tessellator tess = Tessellator.instance;
			
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
