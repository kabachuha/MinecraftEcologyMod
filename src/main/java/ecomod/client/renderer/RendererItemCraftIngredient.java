package ecomod.client.renderer;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import ecomod.api.EcomodItems;
import ecomod.core.EcologyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class RendererItemCraftIngredient implements IItemRenderer
{
	private static final float rotational_period_ticks = 20;
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION || ((helper == ItemRendererHelper.BLOCK_3D || helper == ItemRendererHelper.INVENTORY_BLOCK) && item.getItemDamage() == 0);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		Random random = Minecraft.getMinecraft().theWorld.rand;
		
		if(item.getItemDamage() == 0 && data.length > 0 && data[0] instanceof RenderBlocks) // Piston array
		{
			RenderBlocks blockRender = (RenderBlocks)data[0];
			float scale = 1 / 3F;
			
			GL11.glPushMatrix();
			
			if (RenderItem.renderInFrame)
            {
                GL11.glRotatef(270.0F, 0.0F, 0F, 1.0F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(-0.1F, 0F, 0.35F);
            }
			
			if(type == ItemRenderType.INVENTORY)
			{
				GL11.glTranslatef(0F, -0.3F, 0F);
				GL11.glRotatef(45, 0F, 1F, 0F);
				GL11.glRotatef(45, 1F, 0F, 0F);
				GL11.glScalef(1.2F, 1.2F, 1.2F);
			}
			
			if(type == ItemRenderType.ENTITY)
			{
				GL11.glTranslatef(0, 0.5F, 0);
			}
			
			if(type == ItemRenderType.EQUIPPED)
			{
				GL11.glScalef(0.8F, 0.8F, 0.8F);
				GL11.glTranslatef(1F, 0.7F, 0F);
				GL11.glRotatef(45, 0F, 1F, 0F);
				GL11.glRotatef(-30, 1F, 0F, 0F);
				GL11.glRotatef(15, 0F, 0F, 1F);
			}
			
			if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
			{
				GL11.glRotatef(30, 0F, 0F, 1F);
				GL11.glTranslatef(1.1F, 0F, 0.8F);
			}
			
			// P P P
			// P 0 P
			// P P P
			
			renderPiston(blockRender, -1, -1, 0, scale);
			renderPiston(blockRender, 0, -1, 0, scale);
			renderPiston(blockRender, 1, -1, 0, scale);
			
			renderPiston(blockRender, -1, -1, -1, scale);
			renderPiston(blockRender, 1, -1, -1, scale);
			
			renderPiston(blockRender, -1, -1, -2, scale);
			renderPiston(blockRender, 0, -1, -2, scale);
			renderPiston(blockRender, 1, -1, -2, scale);
			
			GL11.glPopMatrix();
		}
		else if(item.getItemDamage() == 1) // Vent
		{
			IIcon icon = EcomodItems.CRAFT_INGREDIENT.getIconFromDamage(1);
			
			GL11.glPushMatrix();
			
			if(type == ItemRenderType.ENTITY && data.length > 1 && data[1] instanceof EntityItem)
			{
				EntityItem entityItem = (EntityItem)data[1];
				
				if(entityItem == null)
				{
					GL11.glPopMatrix();
					return;
				}
				
				GL11.glTranslatef(-0.05F, 0.2F, -0.05F);
				
				GL11.glRotated((Minecraft.getMinecraft().theWorld.getWorldTime() % rotational_period_ticks) * 360 / rotational_period_ticks, 0, 0, 1);
				
				GL11.glTranslatef(0F, -0.15F, 0F);
				
	            if (RenderItem.renderInFrame)
	            {
	                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
	                GL11.glTranslatef(0F, -0.1F, 0F);
	            }

	            float f6 = 1.0F;
	            float f7 = 0.5F;
	            float f8 = 0.25F;
	            float f9 = 0.0625F;
	            float f10 = 0.021875F;
	            int j = item.stackSize;
	            byte b0;

	            if (j < 2)
	            {
	                b0 = 1;
	            }
	            else if (j < 16)
	            {
	                b0 = 2;
	            }
	            else if (j < 32)
	            {
	                b0 = 3;
	            }
	            else
	            {
	                b0 = 4;
	            }

	            GL11.glTranslatef(-f7, -f8, -((f9 + f10) * (float)b0 / 2.0F));

	            for (int k = 0; k < b0; ++k)
	            {
	            	GL11.glTranslatef(0f, 0f, f9 + f10);

	                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);

	                GL11.glColor4f(1, 1, 1, 1.0F);
	                ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), f9);
	            }

			}
			else if(type == ItemRenderType.INVENTORY)
			{
				GL11.glColor4f(1, 1, 1, 1);
				
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);
				
				GL11.glScalef(0.65F, 0.65F, 0.65F);
				
				GL11.glTranslatef(12F, 12F, 0);
				
				GL11.glRotated((Minecraft.getMinecraft().theWorld.getWorldTime() % rotational_period_ticks) * 360 / rotational_period_ticks, 0, 0, 1);
				
				GL11.glTranslatef(-8F, -8F, 0);
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
				
				Tessellator.instance.startDrawingQuads();
				Tessellator.instance.addVertexWithUV(0, 0, 0, icon.getMinU(), icon.getMinV());
				Tessellator.instance.addVertexWithUV(0, icon.getIconHeight(), 0, icon.getMinU(), icon.getMaxV());
				Tessellator.instance.addVertexWithUV(icon.getIconWidth(), icon.getIconHeight(), 0, icon.getMaxU(), icon.getMaxV());
				Tessellator.instance.addVertexWithUV(icon.getIconWidth(), 0, 0, icon.getMaxU(), icon.getMinV());
				Tessellator.instance.draw();
			}
			else
			{
				if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
				{
					GL11.glTranslatef(0.35F, 0.5F, 0F);
					GL11.glRotated((Minecraft.getMinecraft().theWorld.getWorldTime() % rotational_period_ticks) * 360 / rotational_period_ticks, 0, 0, 1);
					
					GL11.glScalef(0.75F, 0.75F, 0.75F);
					GL11.glTranslatef(-0.5F, -0.5F, 0F);
				}
				else
				{
					GL11.glScalef(0.5F, 0.5F, 0.5F);
				
					GL11.glTranslatef(1F, 0, 1F);
				
					GL11.glRotated((Minecraft.getMinecraft().theWorld.getWorldTime() % rotational_period_ticks) * 360 / rotational_period_ticks, 0, 0, 1);
				
					GL11.glTranslatef(-0.5F, -0.5F, -0.75F);
				}
				
				
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
				
				ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
			}
			
			GL11.glPopMatrix();
		}
	}

	public static void renderPiston(RenderBlocks renderBlock, float x, float y, float z, float scale)
	{
		ItemStack is = new ItemStack(Blocks.piston);
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
		
		GL11.glScalef(scale, scale, scale);
		
		GL11.glTranslatef(x, y, z);
		
		int l = is.getItem().getColorFromItemStack(is, 0);
        float f3 = (float)(l >> 16 & 255) / 255.0F;
        float f4 = (float)(l >> 8 & 255) / 255.0F;
        float f = (float)(l & 255) / 255.0F;
		
		GL11.glColor4f(f3, f4, f, 1.0F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		
		renderBlock.renderBlockAsItem(Blocks.piston, 0, 1F);
		
		GL11.glPopMatrix();
	}
}
