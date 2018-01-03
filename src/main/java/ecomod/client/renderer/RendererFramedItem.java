package ecomod.client.renderer;

import org.lwjgl.opengl.GL11;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import ecomod.common.blocks.BlockFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class RendererFramedItem implements IItemRenderer {

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
		int t = 0;
		if(item.getItem() == Item.getItemFromBlock(EcomodBlocks.FRAME))
			t = item.getItemDamage() == 1 ? 1 : 0;
		if(item.getItem() == Item.getItemFromBlock(EcomodBlocks.ADVANCED_FILTER))
			t = 1;
		
		GL11.glPushMatrix();
		
		if(type == ItemRenderType.INVENTORY)
		{
			GL11.glTranslatef(0F, -0.1F, 0F);
		}
		
		if(type == ItemRenderType.ENTITY)
		{
			GL11.glTranslatef(-0.5F, 0F, -0.5F);
		}
		
		if(RenderItem.renderInFrame)
		{
			GL11.glTranslatef(0F, -0.34F, -0.4F);
			GL11.glScalef(0.8F, 0.8F, 0.8F);
		}
		
		RendererFramedTile.renderFrame(0D, 0D, 0D, 1F, 1F, EcomodBlocks.FRAME.getIcon(0, t), EcomodBlocks.FRAME.getIcon(0, t+2));
		
		if(item.getItem() != Item.getItemFromBlock(EcomodBlocks.FRAME))
		{
			float core_size = 1/2F;
		
			Tessellator tess = Tessellator.instance;
			GL11.glPushMatrix();
		
			GL11.glTranslatef(0.25F, 0.25F, 0.25F);
		
			Minecraft.getMinecraft().getTextureManager().bindTexture(((EcomodBlocks.OC_ANALYZER_ADAPTER != null && item.getItem() == Item.getItemFromBlock(EcomodBlocks.OC_ANALYZER_ADAPTER)) || item.getItem() == Item.getItemFromBlock(EcomodBlocks.ANALYZER)) ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
		
			IIcon texture;
			
			if(EcomodBlocks.OC_ANALYZER_ADAPTER != null && item.getItem() == Item.getItemFromBlock(EcomodBlocks.OC_ANALYZER_ADAPTER) && BlockFrame.oc_adapter != null)
			{
				texture = BlockFrame.oc_adapter.getIcon(2, 0);
			}
			else if(item.getItem() == Item.getItemFromBlock(EcomodBlocks.ANALYZER))
				texture = EcomodItems.CORE.getIconFromDamage(2);
			else if(item.getItem() == Item.getItemFromBlock(EcomodBlocks.ADVANCED_FILTER))
				texture = EcomodItems.CORE.getIconFromDamage(1);
			else
				texture = EcomodItems.CORE.getIconFromDamage(0);
		
			tess.startDrawingQuads();
			tess.setNormal(0, -1F, 0);
			tess.addVertexWithUV(0, 0, 0, texture.getMinU(), texture.getMinV());
			tess.addVertexWithUV(core_size, 0, 0, texture.getMaxU(), texture.getMinV());
			tess.addVertexWithUV(core_size, 0, core_size, texture.getMaxU(), texture.getMaxV());
			tess.addVertexWithUV(0, 0, core_size, texture.getMinU(), texture.getMaxV());
			tess.draw();
		
			tess.startDrawingQuads();
			tess.setNormal(0, 1F, 0);
			tess.addVertexWithUV(0, core_size, core_size, texture.getMinU(), texture.getMinV());
			tess.addVertexWithUV(core_size, core_size, core_size, texture.getMaxU(), texture.getMinV());
			tess.addVertexWithUV(core_size, core_size, 0, texture.getMaxU(), texture.getMaxV());
			tess.addVertexWithUV(0, core_size, 0, texture.getMinU(), texture.getMaxV());
			tess.draw();
		
		
			tess.startDrawingQuads();
			tess.setNormal(-1F, 0, 0);
			tess.addVertexWithUV(0, 0, core_size, texture.getMinU(), texture.getMinV());
			tess.addVertexWithUV(0, core_size, core_size, texture.getMaxU(), texture.getMinV());
			tess.addVertexWithUV(0, core_size, 0, texture.getMaxU(), texture.getMaxV());
			tess.addVertexWithUV(0, 0, 0, texture.getMinU(), texture.getMaxV());
			tess.draw();
		
			tess.startDrawingQuads();
			tess.setNormal(1F, 0, 0);
			tess.addVertexWithUV(core_size, 0, 0, texture.getMinU(), texture.getMinV());
			tess.addVertexWithUV(core_size, core_size, 0, texture.getMaxU(), texture.getMinV());
			tess.addVertexWithUV(core_size, core_size, core_size, texture.getMaxU(), texture.getMaxV());
			tess.addVertexWithUV(core_size, 0, core_size, texture.getMinU(), texture.getMaxV());
			tess.draw();
		
		
			tess.startDrawingQuads();
			tess.setNormal(0, 0, -1F);
			tess.addVertexWithUV(0, 0, 0, texture.getMinU(), texture.getMinV());
			tess.addVertexWithUV(0, core_size, 0, texture.getMaxU(), texture.getMinV());
			tess.addVertexWithUV(core_size, core_size, 0, texture.getMaxU(), texture.getMaxV());
			tess.addVertexWithUV(core_size, 0, 0, texture.getMinU(), texture.getMaxV());
			tess.draw();
		
			tess.startDrawingQuads();
			tess.setNormal(0, 0, 1F);
			tess.addVertexWithUV(core_size, 0, core_size, texture.getMinU(), texture.getMinV());
			tess.addVertexWithUV(core_size, core_size, core_size, texture.getMaxU(), texture.getMinV());
			tess.addVertexWithUV(0, core_size, core_size, texture.getMaxU(), texture.getMaxV());
			tess.addVertexWithUV(0, 0, core_size, texture.getMinU(), texture.getMaxV());
			tess.draw();
		
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
	}

}
