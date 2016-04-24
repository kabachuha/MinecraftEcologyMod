package ccpm.render;

import javax.vecmath.Matrix4f;

import DummyCore.Client.AdvancedModelLoader;
import DummyCore.Client.IItemRenderer;
import DummyCore.Client.IModelCustom;
import DummyCore.Client.RPAwareModel;
import DummyCore.Utils.DrawUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderPortableAnalyzer implements IItemRenderer {

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("ccpm:models/portable_analyzer.obj"));
	
	ResourceLocation Handle = new ResourceLocation("ccpm:textures/blocks/analyzer_handle.png");
	ResourceLocation Core = new ResourceLocation("ccpm:textures/blocks/analyzer_core.png");
	
	public RenderPortableAnalyzer() {
		// TODO Auto-generated constructor stub
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
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		GlStateManager.translate(0.5D, 0.5D, 0.5D);
		
		if(type == TransformType.GUI)
		{
			GlStateManager.translate(0.5D, 0.5D, 0.5D);
		}
		
		if(type == TransformType.THIRD_PERSON)
		{
			GlStateManager.rotate(45, 0, 0, 1.0F);
			GlStateManager.translate(-1.5, 2.1, 0.49);
		}
		
		DrawUtils.bindTexture(Handle.getResourceDomain(), Handle.getResourcePath());
		
		model.renderAllExcept("Cube");
		
		DrawUtils.bindTexture(Core.getResourceDomain(), Core.getResourcePath());
		
		model.renderPart("Cube");
		
		GlStateManager.enableLighting();
		
		RenderHelper.enableGUIStandardItemLighting();
		
		GlStateManager.popMatrix();
	}

	@Override
	public Matrix4f handleTransformsFor(ItemStack item, TransformType type) {
		if(type == TransformType.THIRD_PERSON)
			return RPAwareModel.THIRD_PERSON_3D;
		return null;
	}

}
