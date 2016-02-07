package ccpm.render;

import ccpm.core.CCPM;
import ccpm.render.models.ModelRespirator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class RenderRespirator implements IItemRenderer {

	ModelRespirator model = new ModelRespirator();
	ResourceLocation texture = new ResourceLocation(CCPM.MODID, "");
	
	public RenderRespirator() {
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		
		
		
	}

}
