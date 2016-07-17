package ccpm.render;
/*
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import DummyCore.Client.AdvancedModelLoader;
import DummyCore.Client.IItemRenderer;
import DummyCore.Client.IModelCustom;
import DummyCore.Client.RPAwareModel;
import DummyCore.Utils.DrawUtils;
import ccpm.blocks.ItemAdThaum;
import ccpm.tiles.TileAdvThaum;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

public class RenderItemAdvThaum implements IItemRenderer {

	
	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("ccpm:models/Thaumic.obj"));
	ResourceLocation B1 = new ResourceLocation("ccpm:textures/blocks/B1.png");
	ResourceLocation C1 = new ResourceLocation("ccpm:textures/blocks/C1.png");
	ResourceLocation Core = new ResourceLocation("ccpm:textures/blocks/Core.png");
	
	public RenderItemAdvThaum() {
	}

	@Override
	public boolean handleRenderType(ItemStack item, TransformType type) {
		return true;
	}

	@Override
	public void renderItem(TransformType type, ItemStack item) {
		
		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		if(type != TransformType.GROUND)
		{
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		}
		GlStateManager.translate(0.5D, 0.5D, 0.5D);
		
		if(type == TransformType.GUI)
		{
			GlStateManager.translate(0.5D, 0.5D, 0.5D);
		}
		
		DrawUtils.bindTexture(B1.getResourceDomain(), B1.getResourcePath());
		
		model.renderOnly("Cube.001","Cube.002");
		
		DrawUtils.bindTexture(C1.getResourceDomain(), C1.getResourcePath());
		
		model.renderAllExcept("Icosphere","Cube.001","Cube.002");
		
		DrawUtils.bindTexture(Core.getResourceDomain(), Core.getResourcePath());
		
		model.renderPart("Icosphere");
		
		GlStateManager.enableLighting();
		
		RenderHelper.enableGUIStandardItemLighting();
		
		GlStateManager.popMatrix();
	}

	
	@Override
	public Matrix4f handleTransformsFor(ItemStack item, TransformType type) {
		if(type == TransformType.THIRD_PERSON)
			return RPAwareModel.THIRD_PERSON_2D;
		return null;
	}

}*/
