package ecomod.client.gui;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.client.EMClientUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiPollutionEffectsBook extends GuiScreen
{
	private static final ResourceLocation bookGuiTextures = new ResourceLocation("minecraft:textures/gui/book.png");
	private int pages_excluding_effects = 3;
	private int page = 0;
	private int book_half_w = 146;
    private int book_half_h = 180;
	
	private Set<IAnalyzerPollutionEffect> effects;
	
	private static final String title_string = "item.ecomod.effects_book.name";
	
	private static final ResourceLocation deadbush = new ResourceLocation("minecraft:textures/blocks/deadbush.png");
	
	private GuiButton buttonNextPage_first_page;
	private GuiButton buttonNextPage;
	private GuiButton buttonPrevPage;
	
	public GuiPollutionEffectsBook()
	{
		allowUserInput = true;
		effects = new HashSet<IAnalyzerPollutionEffect>(EcologyMod.proxy.getClientHandler().pollution_effects.values());
	}
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		drawFirstPage();
    }
	
	private int pages()
	{
		return pages_excluding_effects + effects.size();
	}
	
	private void drawFirstPage()
	{
		int start_x = (width - book_half_w) / 2; 
		int start_y = (height - book_half_h) / 2;
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(bookGuiTextures);
		GL11.glColor4f(1, 1, 1, 1);
		
		GuiAnalyzer.drawModalRectWithCustomSizedTexture(start_x, start_y, 20, 1, book_half_w, book_half_h, 256, 256);
		
		fontRendererObj.drawString(I18n.format(title_string), start_x + center(book_half_w, fontRendererObj.getStringWidth(title_string)), start_y + (int)(0.59F * book_half_h), Color.BLACK.getRGB());
		fontRendererObj.drawString(I18n.format(EMConsts.name), start_x + center(book_half_w, fontRendererObj.getStringWidth(EMConsts.name)), start_y + (int)(0.59F * book_half_h) + 20, Color.GRAY.getRGB());
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(deadbush);
		
		GuiAnalyzer.drawModalRectWithCustomSizedTexture(start_x + center(book_half_w, 64), (int)(book_half_h * 0.15F), 0, 0, 64, 64, 16, 16);
	}
	
	private int center(int a, int b)
	{
		return (int)((a - b) /2F);
	}
	
	public void updateScreen()
    {
		if (this.mc.thePlayer == null || !this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
    }
	
	public void switch_page()
	{
		if(page < 0)
			page = 0;
		if(page > pages())
			page = pages();
		
		//buttonNextPage_first_page.enabled = buttonNextPage_first_page.visible = page == 0;
		//buttonNextPage.enabled = buttonNextPage.visible = buttonPrevPage.enabled = buttonPrevPage.visible = page != 0;
		
	}
	
	protected void actionPerformed(GuiButton button)
    {
		
    }
	
	protected void keyTyped(char typedChar, int keyCode)
    {
		if (keyCode == 1 || keyCode == Keyboard.KEY_E)
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
    }
	
	@SideOnly(Side.CLIENT)
    static class NextPageButton extends GuiButton
        {
            private final boolean next;

            public NextPageButton(int id, int x, int y, boolean next)
            {
                super(id, x, y, 23, 13, "");
                this.next = next;
            }

            /**
             * Draws this button to the screen.
             */
            public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
            {
                if (this.visible)
                {
                    boolean flag = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    p_146112_1_.getTextureManager().bindTexture(bookGuiTextures);
                    int k = 0;
                    int l = 192;

                    if (flag)
                    {
                        k += 23;
                    }

                    if (!this.next)
                    {
                        l += 13;
                    }

                    this.drawTexturedModalRect(this.xPosition, this.yPosition, k, l, 23, 13);
                }
            }
        }
	
	@Override
    public boolean doesGuiPauseGame()
	{
        return true;
    }
	
	@Override
    public void initGui()
    {
    	super.initGui();
    	
    	this.buttonList.add(buttonNextPage_first_page = new NextPageButton(1, 105, 160, true));
    //	this.buttonList.add(buttonNextPage = new NextPageButton(1, 105, 160, true));
    	
    	switch_page();
    }
}
