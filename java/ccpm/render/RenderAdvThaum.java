package ccpm.render;
/*
import java.util.Random;

import org.lwjgl.opengl.GL11;

import DummyCore.Client.AdvancedModelLoader;
import DummyCore.Client.IModelCustom;
import DummyCore.Utils.Coord3D;
import DummyCore.Utils.DrawUtils;
import DummyCore.Utils.Lightning;
import DummyCore.Utils.MathUtils;
import DummyCore.Utils.TessellatorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderAdvThaum extends TileEntitySpecialRenderer {

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("ccpm:models/Thaumic.obj"));
	ResourceLocation B1 = new ResourceLocation("ccpm:textures/blocks/B1.png");
	ResourceLocation C1 = new ResourceLocation("ccpm:textures/blocks/C1.png");
	ResourceLocation Core = new ResourceLocation("ccpm:textures/blocks/Core.png");
	//ResourceLocation beams = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		if(te == null)
			return;
		
		RenderHelper.disableStandardItemLighting();
		
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x+0.5D, y+0.5D, z+0.5D);
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		
		DrawUtils.bindTexture(B1.getResourceDomain(), B1.getResourcePath());
		
		model.renderOnly("Cube.001","Cube.002");
		
		DrawUtils.bindTexture(C1.getResourceDomain(), C1.getResourcePath());
		
		model.renderAllExcept("Icosphere","Cube.001","Cube.002");
		
		//GlStateManager.enableLighting();
		
		//GlStateManager.popMatrix();
		
		
		//GlStateManager.pushMatrix();
		
		//GlStateManager.disableLighting();
		
		//GlStateManager.scale(0.5, 0.5, 0.5);
		
		//float movement = Minecraft.getMinecraft().theWorld.getWorldTime()%20;
		
		//if(movement > 10)movement = 10-movement+10;
		
		//GlStateManager.translate(x+0.5D, y+0.5D+(movement/10), z+0.5D);
		
		GlStateManager.rotate((Minecraft.getMinecraft().theWorld.getWorldTime()%45)*8 + partialTicks, 0, 1, 0);
		
		DrawUtils.bindTexture(Core.getResourceDomain(), Core.getResourcePath());
		
		model.renderPart("Icosphere");
		
		GlStateManager.enableLighting();
		
		GlStateManager.popMatrix();
		
		RenderHelper.enableStandardItemLighting();
	}
}*/
