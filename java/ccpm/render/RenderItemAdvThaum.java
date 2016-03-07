package ccpm.render;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import DummyCore.Client.IItemRenderer;
import ccpm.blocks.ItemAdThaum;
import ccpm.tiles.TileAdvThaum;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

public class RenderItemAdvThaum implements IItemRenderer {

	public RenderItemAdvThaum() {
	}

	@Override
	public boolean handleRenderType(ItemStack item, TransformType type) {
		return true;
	}

	@Override
	public void renderItem(TransformType type, ItemStack item) {
		//if(item!=null)
		//TileEntityRendererDispatcher.instance.renderTileEntity(new TileAdvThaum("null", 0), 0, 0);
	}

	
	@Override
	public Matrix4f handleTransformsFor(ItemStack item, TransformType type) {
		return ForgeHooksClient.getMatrix(ModelRotation.X0_Y0);
	}

}
