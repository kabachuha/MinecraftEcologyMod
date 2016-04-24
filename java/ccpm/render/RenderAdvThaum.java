package ccpm.render;

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
	
	Lightning light1;
	Lightning light2;
	Lightning light3;
	Lightning light4;
	
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
		
		if(Minecraft.getMinecraft().theWorld.getWorldTime()%20 == 0)
		{
			setupLightnings();
		}
		
		if(light1!=null && light2 !=null && light3!=null && light4 !=null)
		{
			renderLightnings(x+0.5, y+0.5, z+0.5, partialTicks);
		}
		
		RenderHelper.enableStandardItemLighting();
		
		//renderEnderLights(x+0.5D, y+0.5D, z+0.5D);
	}
	
	
	void renderEnderLights(double x, double y, double z)
	{
		 float par2 = Minecraft.getMinecraft().theWorld.getWorldTime();

         while (par2 > 200)
             par2 -= 100;

         RenderHelper.disableStandardItemLighting();
         float var41 = (5 + par2) / 200.0F;
         float var51 = 0.0F;

         if (var41 > 0.8F)
         {
             var51 = (var41 - 0.8F) / 0.2F;
         }

         Random rand = new Random(432L);

         GlStateManager.pushMatrix();
         GlStateManager.translate(x,y,z);
         GlStateManager.disableTexture2D();
         GlStateManager.shadeModel(GL11.GL_SMOOTH);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
         GlStateManager.disableAlpha();
         GlStateManager.enableCull();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         
         TessellatorWrapper tessellator = TessellatorWrapper.instance;
         GlStateManager.scale(0.5, 0.5, 0.5);
         for (int i1 = 0; i1 < (var41 + var41 * var41) / 2.0F * 60.0F; ++i1)
         {
             GlStateManager.rotate(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
             GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
             GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
             GlStateManager.rotate(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
             GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
             GlStateManager.rotate(rand.nextFloat() * 360.0F + var41 * 90.0F, 0.0F, 0.0F, 1.0F);
             //DrawUtils.bindTexture(beams.getResourceDomain(), beams.getResourcePath());
             tessellator.startDrawing(6);
             float var81 = rand.nextFloat() * 20.0F + 5.0F + var51 * 10.0F;
             float var91 = rand.nextFloat() * 2.0F + 1.0F + var51 * 2.0F;
             tessellator.setColorRGBA_I(16777215, (int) (255.0F * (1.0F - var51)));
             tessellator.addVertex(0.0D, 0.0D, 0.0D);
             tessellator.setColorRGBA_I(0, 0);
             tessellator.addVertex(-0.866D * var91, var81, -0.5F * var91);
             tessellator.addVertex(0.866D * var91, var81, -0.5F * var91);
             tessellator.addVertex(0.0D, var81, 1.0F * var91);
             tessellator.addVertex(-0.866D * var91, var81, -0.5F * var91);
             tessellator.draw();
         }
         
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.shadeModel(GL11.GL_FLAT);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.popMatrix();
	}
	
	
	
	void setupLightnings()
	{
		light1 = new Lightning(Minecraft.getMinecraft().theWorld.rand, new Coord3D(0,0,0), new Coord3D(MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50), 1.0F, 1,1,1);
		light2 = new Lightning(Minecraft.getMinecraft().theWorld.rand, new Coord3D(0,0,0), new Coord3D(MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50), 1.0F, 1,1,1);
		light3 = new Lightning(Minecraft.getMinecraft().theWorld.rand, new Coord3D(0,0,0), new Coord3D(MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50), 1.0F, 1,1,1);
		light4 = new Lightning(Minecraft.getMinecraft().theWorld.rand, new Coord3D(0,0,0), new Coord3D(MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50), 1.0F, 1,1,1);
	}
	
	void renderLightnings(double x, double y, double z, float partialTicks)
	{
		GlStateManager.pushMatrix();
		light1.render(x, y, z, partialTicks);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		light2.render(x, y, z, partialTicks);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		light3.render(x, y, z, partialTicks);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		light4.render(x, y, z, partialTicks);
		GlStateManager.popMatrix();
	}

	
	
}
