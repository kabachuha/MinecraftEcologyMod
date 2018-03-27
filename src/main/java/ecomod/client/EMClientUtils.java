package ecomod.client;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;


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
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		float dx = (width)/2F;
		float dy = (height)/2F;
		GL11.glTranslatef(dx, dy, 0);
		GL11.glRotatef(rotation, 0, 0, -1);
		GL11.glTranslatef(-dx, -dy, 0);

		float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0, (double)(height), 0, (double)(u * f), (double)((v + (float)height) * f1));
        tessellator.addVertexWithUV((double)(width), (double)(height), 0, (double)((u + (float)width) * f), (double)((v + (float)height) * f1));
        tessellator.addVertexWithUV((double)(width), 0, 0, (double)((u + (float)width) * f), (double)(v * f1));
        tessellator.addVertexWithUV(0, 0, 0, (double)(u * f), (double)(v * f1));
        tessellator.draw();
		
        GL11.glPopMatrix();
	}
	
	public static void drawRotatedTexture(int xCoord, int yCoord, IIcon textureSprite, int widthIn, int heightIn, float rotation)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(xCoord, yCoord, 0);
		float dx = (widthIn)/2F;
		float dy = (heightIn)/2F;
		GL11.glTranslatef(dx, dy, 0);
		GL11.glRotatef(rotation, 0, 0, -1);
		GL11.glTranslatef(-dx, -dy, 0);
		
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0, (double)(heightIn), 0, (double)textureSprite.getMinU(), (double)textureSprite.getMaxV());
        tessellator.addVertexWithUV((double)(widthIn), (double)(heightIn), 0, (double)textureSprite.getMaxU(), (double)textureSprite.getMaxV());
        tessellator.addVertexWithUV((double)(widthIn), 0, 0, (double)textureSprite.getMaxU(), (double)textureSprite.getMinV());
        tessellator.addVertexWithUV(0, 0, 0, (double)textureSprite.getMinU(), (double)textureSprite.getMinV());
        tessellator.draw();
		
		GL11.glPopMatrix();
	}
}