package ecomod.client.renderer;

import org.lwjgl.opengl.GL11;

import ecomod.api.EcomodItems;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class RenderItemCore implements IItemRenderer {
	
	public static final net.minecraft.block.Block model = new DummyBlock();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return helper == ItemRendererHelper.BLOCK_3D || helper == ItemRendererHelper.INVENTORY_BLOCK || helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION || helper == ItemRendererHelper.EQUIPPED_BLOCK;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		if(data.length > 0 && data[0] instanceof RenderBlocks)
		{
			RenderBlocks blockRender = (RenderBlocks)data[0];
			
			GL11.glPushMatrix();
			
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			
			if (Blocks.piston.getRenderBlockPass() != 0)
	        {
	            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	        }
	        else
	        {
	            GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
	            GL11.glDisable(GL11.GL_BLEND);
	        }
			
			if (RenderItem.renderInFrame)
            {
				if(item.getItemDamage() == 0)
					GL11.glScalef(0.7F, 0.7F, 0.7F);
                GL11.glTranslatef(0F, 0F, -0.04F);
            }
			
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			
			if(type == ItemRenderType.EQUIPPED)
				GL11.glTranslatef(1.3F, 1F, 1.3F);
			
			if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
				GL11.glTranslatef(0F, 1.3F, 1F);
			
			int l = item.getItem().getColorFromItemStack(item, 0);
	        float f3 = (float)(l >> 16 & 255) / 255.0F;
	        float f4 = (float)(l >> 8 & 255) / 255.0F;
	        float f = (float)(l & 255) / 255.0F;
			
			GL11.glColor4f(f3, f4, f, 1.0F);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(item.getItemDamage() == 2 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
			
			blockRender.renderBlockAsItem(model, item.getItemDamage(), 1F);
			
			GL11.glPopMatrix();
		}
	}

	private static class DummyBlock extends net.minecraft.block.Block
	{
		public DummyBlock()
		{
			super(Material.cactus);
		}

		@Override
		public IIcon getIcon(int side, int meta) {
			return EcomodItems.CORE.getIconFromDamage(meta);
		}
	}
}
