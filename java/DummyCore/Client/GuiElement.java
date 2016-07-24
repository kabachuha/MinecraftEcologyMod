package DummyCore.Client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * ---From DummyCore---
 * 
 * A GuiElement is an additional object that renders for GuiCommon. One GuiCommon can hold up to N GuiElements, where N is positive infinity?
 * @author Modbder(https://github.com/Modbder). Updated to 1.10.2 by Artem226
 *
 */
public abstract class GuiElement {
	int zLevel = 0;
	
	/**
	 * @return The texture to draw your element with
	 */
	public abstract ResourceLocation getElementTexture();

	/**
	 * Do your rendering here
	 * @param posX - the X position on the GuiCommon
	 * @param posY - the Y position on the GuiCommon
	 */
	public abstract void draw(int posX, int posY, int mouseX, int mouseY);
	
	/**
	 * Gets the X position of THIS object on the parent
	 * @return The X position of THIS object on the parent
	 */
	public abstract int getX();
	
	/**
	 * Gets the Y position of THIS object on the parent
	 * @return The Y position of THIS object on the parent
	 */
	public abstract int getY();
	
	//Generic GUI functions 
	
    public void drawTexturedModalRect(int p_73729_1_, int p_73729_2_, int p_73729_3_, int p_73729_4_, int p_73729_5_, int p_73729_6_)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(p_73729_1_ + 0, p_73729_2_ + p_73729_6_, this.zLevel).tex( (p_73729_3_ + 0) * f, (p_73729_4_ + p_73729_6_) * f1).endVertex();
        vb.pos(p_73729_1_ + p_73729_5_, p_73729_2_ + p_73729_6_, this.zLevel).tex( (p_73729_3_ + p_73729_5_) * f, (p_73729_4_ + p_73729_6_) * f1).endVertex();
        vb.pos(p_73729_1_ + p_73729_5_, p_73729_2_ + 0, this.zLevel).tex( (p_73729_3_ + p_73729_5_) * f, (p_73729_4_ + 0) * f1).endVertex();
        vb.pos(p_73729_1_ + 0, p_73729_2_ + 0, this.zLevel).tex( (p_73729_3_ + 0) * f, (p_73729_4_ + 0) * f1).endVertex();
        tessellator.draw();
    }

    public static void func_146110_a(int p_146110_0_, int p_146110_1_, float p_146110_2_, float p_146110_3_, int p_146110_4_, int p_146110_5_, float p_146110_6_, float p_146110_7_)
    {
        float f4 = 1.0F / p_146110_6_;
        float f5 = 1.0F / p_146110_7_;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(p_146110_0_, p_146110_1_ + p_146110_5_, 0.0D).tex(p_146110_2_ * f4, (p_146110_3_ + p_146110_5_) * f5).endVertex();
        vb.pos(p_146110_0_ + p_146110_4_, p_146110_1_ + p_146110_5_, 0.0D).tex((p_146110_2_ + p_146110_4_) * f4, (p_146110_3_ + p_146110_5_) * f5).endVertex();
        vb.pos(p_146110_0_ + p_146110_4_, p_146110_1_, 0.0D).tex((p_146110_2_ + p_146110_4_) * f4, p_146110_3_ * f5);
        vb.pos(p_146110_0_, p_146110_1_, 0.0D).tex(p_146110_2_ * f4, p_146110_3_ * f5).endVertex();
        tessellator.draw();
    }

    public static void func_152125_a(int p_152125_0_, int p_152125_1_, float p_152125_2_, float p_152125_3_, int p_152125_4_, int p_152125_5_, int p_152125_6_, int p_152125_7_, float p_152125_8_, float p_152125_9_)
    {
        float f4 = 1.0F / p_152125_8_;
        float f5 = 1.0F / p_152125_9_;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(p_152125_0_, p_152125_1_ + p_152125_7_, 0.0D).tex(p_152125_2_ * f4, (p_152125_3_ + p_152125_5_) * f5).endVertex();
        vb.pos(p_152125_0_ + p_152125_6_, p_152125_1_ + p_152125_7_, 0.0D).tex((p_152125_2_ + p_152125_4_) * f4, (p_152125_3_ + p_152125_5_) * f5).endVertex();
        vb.pos(p_152125_0_ + p_152125_6_, p_152125_1_, 0.0D).tex((p_152125_2_ + p_152125_4_) * f4, p_152125_3_ * f5).endVertex();
        vb.pos(p_152125_0_, p_152125_1_, 0.0D).tex(p_152125_2_ * f4, p_152125_3_ * f5).endVertex();
        tessellator.draw();
    }

    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos((double)right, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
        vb.pos((double)left, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
        vb.pos((double)left, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
        vb.pos((double)right, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
