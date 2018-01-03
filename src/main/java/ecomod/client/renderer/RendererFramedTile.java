package ecomod.client.renderer;

import org.lwjgl.opengl.GL11;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import ecomod.common.blocks.BlockFrame;
import ecomod.common.tiles.TileAdvancedFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class RendererFramedTile extends TileEntitySpecialRenderer
{
	protected final int type;
	
	// 0 - Basic, 1 - Advanced
	public RendererFramedTile(int type)
	{
		super();
		this.type = type;
	}
	
	public RendererFramedTile()
	{
		super();
		this.type = -1;
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick)
	{
		int t = type;
		if(t == 2 || t == 3)
			t = 0;
		if(t == -1)
		{
			t = tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord) == 1 ? 1 : 0;
		}
		renderFrame(x, y, z, 1F, 1F, EcomodBlocks.FRAME.getIcon(0, t), EcomodBlocks.FRAME.getIcon(0, t+2));
		
		if(type != -1)
		{
			renderCore(x, y, z, 1F, 1F);
		}
		
		if(type == 1)
			renderVents((TileAdvancedFilter)tile, x, y, z, 1F, 1F);
	}

	public static void renderFrame(double x, double y, double z, float scale, float brightness, IIcon texture_1, IIcon texture_2)
	{
		float section_size = scale / 4F;
		float core_size = scale / 2F;
		
		Tessellator tess = Tessellator.instance;
		
		GL11.glPushMatrix();
		
		GL11.glColor4f(brightness, brightness, brightness, 1.0F);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		
		//H
		
		tess.startDrawingQuads();
		tess.setNormal(0, -1F, 0);
		tess.addVertexWithUV(x, y, z, texture_1.getMinU(), texture_1.getMinV());
		tess.addVertexWithUV(x+1F, y, z, texture_1.getMaxU(), texture_1.getMinV());
		tess.addVertexWithUV(x+1F, y, z+1F, texture_1.getMaxU(), texture_1.getMaxV());
		tess.addVertexWithUV(x, y, z+1F, texture_1.getMinU(), texture_1.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(0, 1F, 0);
		tess.addVertexWithUV(x, y+section_size, z+1F, texture_2.getMinU(), texture_2.getMinV());
		tess.addVertexWithUV(x+1F, y+section_size, z+1F, texture_2.getMaxU(), texture_2.getMinV());
		tess.addVertexWithUV(x+1F, y+section_size, z, texture_2.getMaxU(), texture_2.getMaxV());
		tess.addVertexWithUV(x, y+section_size, z, texture_2.getMinU(), texture_2.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(0, -1F, 0);
		tess.addVertexWithUV(x, y+section_size+core_size, z, texture_2.getMinU(), texture_2.getMinV());
		tess.addVertexWithUV(x+1F, y+section_size+core_size, z, texture_2.getMaxU(), texture_2.getMinV());
		tess.addVertexWithUV(x+1F, y+section_size+core_size, z+1F, texture_2.getMaxU(), texture_2.getMaxV());
		tess.addVertexWithUV(x, y+section_size+core_size, z+1F, texture_2.getMinU(), texture_2.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(0, 1F, 0);
		tess.addVertexWithUV(x, y+1F, z+1F, texture_1.getMinU(), texture_1.getMinV());
		tess.addVertexWithUV(x+1F, y+1F, z+1F, texture_1.getMaxU(), texture_1.getMinV());
		tess.addVertexWithUV(x+1F, y+1F, z, texture_1.getMaxU(), texture_1.getMaxV());
		tess.addVertexWithUV(x, y+1F, z, texture_1.getMinU(), texture_1.getMaxV());
		tess.draw();
		
		// W
		
		tess.startDrawingQuads();
		tess.setNormal(-1F, 0, 0);
		tess.addVertexWithUV(x, y, z+1F, texture_1.getMinU(), texture_1.getMinV());
		tess.addVertexWithUV(x, y+1F, z+1F, texture_1.getMaxU(), texture_1.getMinV());
		tess.addVertexWithUV(x, y+1F, z, texture_1.getMaxU(), texture_1.getMaxV());
		tess.addVertexWithUV(x, y, z, texture_1.getMinU(), texture_1.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(1F, 0, 0);
		tess.addVertexWithUV(x+section_size, y, z, texture_2.getMinU(), texture_2.getMinV());
		tess.addVertexWithUV(x+section_size, y+1F, z, texture_2.getMaxU(), texture_2.getMinV());
		tess.addVertexWithUV(x+section_size, y+1F, z+1F, texture_2.getMaxU(), texture_2.getMaxV());
		tess.addVertexWithUV(x+section_size, y, z+1F, texture_2.getMinU(), texture_2.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(-1F, 0, 0);
		tess.addVertexWithUV(x+section_size+core_size, y, z+1F, texture_2.getMinU(), texture_2.getMinV());
		tess.addVertexWithUV(x+section_size+core_size, y+1F, z+1F, texture_2.getMaxU(), texture_2.getMinV());
		tess.addVertexWithUV(x+section_size+core_size, y+1F, z, texture_2.getMaxU(), texture_2.getMaxV());
		tess.addVertexWithUV(x+section_size+core_size, y, z, texture_2.getMinU(), texture_2.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(1F, 0, 0);
		tess.addVertexWithUV(x+1F, y, z, texture_1.getMinU(), texture_1.getMinV());
		tess.addVertexWithUV(x+1F, y+1F, z, texture_1.getMaxU(), texture_1.getMinV());
		tess.addVertexWithUV(x+1F, y+1F, z+1F, texture_1.getMaxU(), texture_1.getMaxV());
		tess.addVertexWithUV(x+1F, y, z+1F, texture_1.getMinU(), texture_1.getMaxV());
		tess.draw();
		
		// L
		
		tess.startDrawingQuads();
		tess.setNormal(0, 0, -1F);
		tess.addVertexWithUV(x, y, z, texture_1.getMinU(), texture_1.getMinV());
		tess.addVertexWithUV(x, y+1F, z, texture_1.getMaxU(), texture_1.getMinV());
		tess.addVertexWithUV(x+1F, y+1F, z, texture_1.getMaxU(), texture_1.getMaxV());
		tess.addVertexWithUV(x+1F, y, z, texture_1.getMinU(), texture_1.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(0, 0, 1F);
		tess.addVertexWithUV(x+1F, y, z+section_size, texture_2.getMinU(), texture_2.getMinV());
		tess.addVertexWithUV(x+1F, y+1F, z+section_size, texture_2.getMaxU(), texture_2.getMinV());
		tess.addVertexWithUV(x, y+1F, z+section_size, texture_2.getMaxU(), texture_2.getMaxV());
		tess.addVertexWithUV(x, y, z+section_size, texture_2.getMinU(), texture_2.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(0, 0, -1F);
		tess.addVertexWithUV(x, y, z+section_size+core_size, texture_2.getMinU(), texture_2.getMinV());
		tess.addVertexWithUV(x, y+1F, z+section_size+core_size, texture_2.getMaxU(), texture_2.getMinV());
		tess.addVertexWithUV(x+1F, y+1F, z+section_size+core_size, texture_2.getMaxU(), texture_2.getMaxV());
		tess.addVertexWithUV(x+1F, y, z+section_size+core_size, texture_2.getMinU(), texture_2.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(0, 0, 1F);
		tess.addVertexWithUV(x+1F, y, z+1F, texture_1.getMinU(), texture_1.getMinV());
		tess.addVertexWithUV(x+1F, y+1F, z+1F, texture_1.getMaxU(), texture_1.getMinV());
		tess.addVertexWithUV(x, y+1F, z+1F, texture_1.getMaxU(), texture_1.getMaxV());
		tess.addVertexWithUV(x, y, z+1F, texture_1.getMinU(), texture_1.getMaxV());
		tess.draw();
		
		GL11.glPopMatrix();
	}
	
	public void renderCore(double x, double y, double z, float scale, float brightness)
	{
		float section_size = scale / 4F;
		float core_size = scale / 2F;
		
		x = x + section_size;
		y = y + section_size;
		z = z + section_size;
		
		Tessellator tess = Tessellator.instance;
		GL11.glPushMatrix();
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(type == 2 || type == 3 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
		
		IIcon texture;
		
		if(type == 2)
			texture = EcomodItems.CORE.getIconFromDamage(2);
		else if(type == 3 && BlockFrame.oc_adapter != null)
			texture = BlockFrame.oc_adapter.getIcon(2, 0);
		else
			texture = EcomodItems.CORE.getIconFromDamage(0);
		
		tess.startDrawingQuads();
		tess.setNormal(0, -1F, 0);
		tess.addVertexWithUV(x, y, z, texture.getMinU(), texture.getMinV());
		tess.addVertexWithUV(x+core_size, y, z, texture.getMaxU(), texture.getMinV());
		tess.addVertexWithUV(x+core_size, y, z+core_size, texture.getMaxU(), texture.getMaxV());
		tess.addVertexWithUV(x, y, z+core_size, texture.getMinU(), texture.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(0, 1F, 0);
		tess.addVertexWithUV(x, y+core_size, z+core_size, texture.getMinU(), texture.getMinV());
		tess.addVertexWithUV(x+core_size, y+core_size, z+core_size, texture.getMaxU(), texture.getMinV());
		tess.addVertexWithUV(x+core_size, y+core_size, z, texture.getMaxU(), texture.getMaxV());
		tess.addVertexWithUV(x, y+core_size, z, texture.getMinU(), texture.getMaxV());
		tess.draw();
		
		
		tess.startDrawingQuads();
		tess.setNormal(-1F, 0, 0);
		tess.addVertexWithUV(x, y, z+core_size, texture.getMinU(), texture.getMinV());
		tess.addVertexWithUV(x, y+core_size, z+core_size, texture.getMaxU(), texture.getMinV());
		tess.addVertexWithUV(x, y+core_size, z, texture.getMaxU(), texture.getMaxV());
		tess.addVertexWithUV(x, y, z, texture.getMinU(), texture.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(1F, 0, 0);
		tess.addVertexWithUV(x+core_size, y, z, texture.getMinU(), texture.getMinV());
		tess.addVertexWithUV(x+core_size, y+core_size, z, texture.getMaxU(), texture.getMinV());
		tess.addVertexWithUV(x+core_size, y+core_size, z+core_size, texture.getMaxU(), texture.getMaxV());
		tess.addVertexWithUV(x+core_size, y, z+core_size, texture.getMinU(), texture.getMaxV());
		tess.draw();
		
		
		tess.startDrawingQuads();
		tess.setNormal(0, 0, -1F);
		tess.addVertexWithUV(x, y, z, texture.getMinU(), texture.getMinV());
		tess.addVertexWithUV(x, y+core_size, z, texture.getMaxU(), texture.getMinV());
		tess.addVertexWithUV(x+core_size, y+core_size, z, texture.getMaxU(), texture.getMaxV());
		tess.addVertexWithUV(x+core_size, y, z, texture.getMinU(), texture.getMaxV());
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(0, 0, 1F);
		tess.addVertexWithUV(x+core_size, y, z+core_size, texture.getMinU(), texture.getMinV());
		tess.addVertexWithUV(x+core_size, y+core_size, z+core_size, texture.getMaxU(), texture.getMinV());
		tess.addVertexWithUV(x, y+core_size, z+core_size, texture.getMaxU(), texture.getMaxV());
		tess.addVertexWithUV(x, y, z+core_size, texture.getMinU(), texture.getMaxV());
		tess.draw();
		
		GL11.glPopMatrix();
	}
	
	private void renderVents(TileAdvancedFilter tile, double x, double y, double z, float scale, float brightness)
	{
		IIcon icon = EcomodItems.CRAFT_INGREDIENT.getIconFromDamage(1);
		
		Tessellator tess = Tessellator.instance;
		tess.setBrightness((int)(255 * brightness));
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(x+0.25D + 1/32F + 1/128F, y+0.25D + 1/32F, z+0.5D);
			renderVent(tess, tile.vent_rotation, dir, icon, 1F);
			GL11.glPopMatrix();
		}
	}
	
	private void renderVent(Tessellator tess, float rotation, ForgeDirection dir, IIcon icon, float scale)
	{
		float t = 1/16F + scale / 4F;
		
		GL11.glTranslatef(t * dir.offsetX, t * dir.offsetY, t * dir.offsetZ);
		
		GL11.glScalef(0.4F, 0.4F, 0.4F);
		
		GL11.glPushMatrix();
		
		if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN)
		{
			GL11.glRotatef(90F, 1, 0, 0);
			GL11.glTranslatef(0.025F, -0.5F, -0.5F);
		}
		else if(dir == ForgeDirection.EAST || dir == ForgeDirection.WEST)
		{
			GL11.glRotatef(90, 0, 1, 0);
			GL11.glTranslatef(-0.5F, 0.05F, 0.5F);
		}
		else
		{
			GL11.glTranslatef(0.03F, 0.03F, 0);
		}
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		
		if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN)
		{
			GL11.glRotatef(rotation, 0, 0, 1);
		}
		else if(dir == ForgeDirection.EAST || dir == ForgeDirection.WEST)
		{
			GL11.glRotatef(rotation, 0, 0, 1);
		}
		else
			GL11.glRotatef(rotation, dir.offsetX, dir.offsetY, dir.offsetZ);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		GL11.glColor4f(0.87F, 0.87F, 0.87F, 1F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
		
        ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
	}
}
