package ecomod.client;

import java.awt.Color;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class EMClientUtils
{
	public static void drawVerticalLine(int x, int startY, int endY, int color)
	{
		if (endY < startY)
        {
            int i = startY;
            startY = endY;
            endY = i;
        }

        Gui.drawRect(x, startY + 1, x + 1, endY, color);
	}
	
	public static void drawHorizontalLine(int startX, int endX, int y, int color)
    {
        if (endX < startX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        Gui.drawRect(startX, y, endX + 1, y + 1, color);
    }
	
	public static void drawPixelFrameSize(int startX, int startY, int sizeX, int sizeY, int color)
	{
		drawHorizontalLine(startX, startX+sizeX-1, startY, color);
		drawHorizontalLine(startX, startX+sizeX-1, startY+sizeY-1, color);
		drawVerticalLine  (startX, startY, startY+sizeY-1, color);
		drawVerticalLine  (startX+sizeX-1, startY, startY+sizeY-1, color);
	}
	
	public static void drawPixelFrameSize(int startX, int startY, int sizeX, int sizeY, Color color)
	{
		drawPixelFrameSize(startX, startY, sizeX, sizeY, color.getRGB());
	}
	
	public static void drawRotatedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight, float rotation)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		float dx = (width)/2F;
		float dy = (height)/2F;
		GlStateManager.translate(dx, dy, 0);
		GlStateManager.rotate(rotation, 0, 0, -1);
		GlStateManager.translate(-dx, -dy, 0);

		float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0, (double)(height), 0.0D).tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex();
        bufferbuilder.pos((double)(width), (double)(height), 0.0D).tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex();
        bufferbuilder.pos((double)(width), 0, 0.0D).tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex();
        bufferbuilder.pos(0, 0, 0).tex((double)(u * f), (double)(v * f1)).endVertex();
        tessellator.draw();
		
		GlStateManager.popMatrix();
	}
	
	public static void drawRotatedTexture(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn, float rotation)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(xCoord, yCoord, 0);
		float dx = (widthIn)/2F;
		float dy = (heightIn)/2F;
		GlStateManager.translate(dx, dy, 0);
		GlStateManager.rotate(rotation, 0, 0, -1);
		GlStateManager.translate(-dx, -dy, 0);
		
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0, (double)(heightIn), 0).tex((double)textureSprite.getMinU(), (double)textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos((double)(widthIn), (double)(heightIn), 0).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos((double)(widthIn), 0, 0).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMinV()).endVertex();
        bufferbuilder.pos(0, 0, 0).tex((double)textureSprite.getMinU(), (double)textureSprite.getMinV()).endVertex();
        tessellator.draw();
		
		GlStateManager.popMatrix();
	}
}