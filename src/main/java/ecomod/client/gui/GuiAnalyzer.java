package ecomod.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.PollutionData;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.common.utils.AnalyzerPollutionEffect;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

public class GuiAnalyzer extends GuiScreen
{
	private PollutionData pollution = null;//new PollutionData(500000, 400000, 450000);
	
	private Date last_update_time = null;
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	
	private GuiButton buttonAnalyze;
	
	private GuiButton buttonUp;
	
	private GuiButton buttonDown;
	
	private TileAnalyzer te;
	
	private List<IAnalyzerPollutionEffect> effects = new ArrayList<IAnalyzerPollutionEffect>();
	
	private static boolean inited_first = false;
	
	private int startIndex;
	
	public GuiAnalyzer(TileAnalyzer tile)
	{
		super();
		te = tile;
		
		allowUserInput = true;
		
		if(!inited_first)
		{
			inited_first = true;
		}
		
		startIndex = 0;
	}
	
	/**
     * Draws the screen and all the components in it.
     */
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		//Background
		drawRect(0, 0, width, height, new Color(198, 198, 198, 150).getRGB());
		//pollution = null;
		int xt1 = (int)(width * 0.66);
		
		if(buttonList.size() == 0)
			initGui();
		
		if(pollution != null)
		{
			drawRect(xt1, buttonAnalyze.yPosition + buttonAnalyze.height + 10, width, height, new Color(198, 198, 198).getRGB());
		}
		
		//Energy indicator
		this.drawVerticalLine(width-110-1, height-30-2, height-10+1, Color.BLACK.getRGB());
		this.drawVerticalLine(width-10, height-30-2, height-10+1, Color.BLACK.getRGB());
		this.drawHorizontalLine(width-110, width-11, height-30-1, Color.BLACK.getRGB());
		this.drawHorizontalLine(width-110, width-11, height-10, Color.BLACK.getRGB());
		
		EMUtils.drawHorizontalGradientRect(width-110, height-30, width-10, height-10, new Color(40, 13, 13).getRGB(), new Color(145, 13, 13).getRGB(), this.zLevel);
		
		drawRect(width-110 + (int)(100 * ((float)te.getEnergyStored()/te.getMaxEnergyStored())), height-30, width-10, height-10, Color.DARK_GRAY.getRGB());
		//end Energy indicator
		
		if(fontRendererObj == null)
			return;//Wait for fontRendererObj

		if(pollution == null)
			this.drawString(fontRendererObj, I18n.format("gui.ecomod.text.energy", new Object[0]), width-110, height-40-1, Color.ORANGE.getRGB());
		else
			this.drawStringNoShadow(fontRendererObj, I18n.format("gui.ecomod.text.energy", new Object[0]), width-110, height-40-1, Color.RED.getRGB());
		
		this.drawVerticalLine(xt1, 0, height, Color.BLACK.getRGB());
		this.drawHorizontalLine(xt1, width, buttonAnalyze.yPosition + buttonAnalyze.height + 10, Color.DARK_GRAY.getRGB());
		this.drawVerticalLine(buttonAnalyze.xPosition-10, 0, buttonAnalyze.yPosition+buttonAnalyze.height+10, Color.DARK_GRAY.getRGB());
		
		this.drawString(fontRendererObj, I18n.format("gui.ecomod.text.chunk_position", new Object[0]), xt1+4, 11, Color.CYAN.getRGB());
		this.drawString(fontRendererObj, te.getChunkCoords().toString(), xt1+4, 21, Color.CYAN.getRGB());
		
		if(pollution == null)
		{
			this.drawString(fontRendererObj, I18n.format("gui.ecomod.text.no_data.0", new Object[0]), xt1+4, buttonAnalyze.yPosition + buttonAnalyze.height + 10 + 2, Color.MAGENTA.getRGB());
			this.drawString(fontRendererObj, I18n.format("gui.ecomod.text.no_data.1", new Object[0]), xt1+4, buttonAnalyze.yPosition + buttonAnalyze.height + 10 + 11, Color.MAGENTA.getRGB());
		}
		else
		{
			int strt = buttonAnalyze.yPosition + buttonAnalyze.height + 10;
			
			this.drawStringNoShadow(fontRendererObj, I18n.format("gui.ecomod.text.chunk_pollution", new Object[0]), xt1+4, strt+2, Color.BLACK.getRGB());
			
			this.drawHorizontalLine(xt1, width, strt + 11, Color.BLACK.getRGB());
			
			if(last_update_time != null && last_update_time.getTime() != -1)
				this.drawStringNoShadow(fontRendererObj, I18n.format("gui.ecomod.text.analyzed", new Object[0])+" "+DATE_FORMAT.format(last_update_time), xt1+4, strt+13, Color.BLACK.getRGB());
			
			this.drawHorizontalLine(xt1, width, strt + 22, Color.BLACK.getRGB());
/*
			this.drawStringNoShadow(fontRendererObj, "Air Pollution: "+pollution.getAirPollution(), xt1+4, strt+34, new Color(255, 255, 126).getRGB());//0xffff7e
			
			this.drawStringNoShadow(fontRendererObj, "Water Pollution: "+pollution.getWaterPollution(), xt1+4, strt+54, new Color(60, 212, 252).getRGB());//0x3cd4fc
			
			this.drawStringNoShadow(fontRendererObj, "Soil Pollution: "+pollution.getSoilPollution(), xt1+4, strt+74, new Color(89, 61, 41).getRGB());//0x593d29
*/
			if(pollution.getAirPollution() < 0.1D)
				this.drawStringNoShadow(fontRendererObj, "0", xt1+4+105, strt+42, new Color(255, 255, 126).getRGB());
			else
				this.drawStringNoShadow(fontRendererObj, ""+pollution.getAirPollution(), xt1+4+105, strt+42, new Color(255, 255, 126).getRGB());
			
			if(pollution.getWaterPollution() < 0.1D)
				this.drawStringNoShadow(fontRendererObj, "0", xt1+4+105, strt+62, new Color(60, 212, 252).getRGB());
			else
				this.drawStringNoShadow(fontRendererObj, ""+pollution.getWaterPollution(), xt1+4+105, strt+62, new Color(60, 212, 252).getRGB());
			
			if(pollution.getSoilPollution() < 0.1D)
				this.drawStringNoShadow(fontRendererObj, "0", xt1+4+105, strt+82, new Color(89, 61, 41).getRGB());
			else
				this.drawStringNoShadow(fontRendererObj, ""+pollution.getSoilPollution(), xt1+4+105, strt+82, new Color(89, 61, 41).getRGB());
			
			this.drawStringNoShadow(fontRendererObj, I18n.format("gui.ecomod.text.pollution_effects", new Object[0]), xt1/2-50, 10, Color.BLACK.getRGB());
			
			this.drawHorizontalLine(0, xt1, buttonAnalyze.yPosition + buttonAnalyze.height + 10, Color.DARK_GRAY.getRGB());
			
			GL11.glColor4f(1, 1, 1, 1);
			
			ResourceLocation lang_texture = EMUtils.resloc("textures/gui/analyzer/pollution_local/"+Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase()+".png");
			
			if(!Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toString().toLowerCase().equals("en_us"))
			{
				try
				{
					IResource ir = Minecraft.getMinecraft().getResourceManager().getResource(lang_texture);
					if(ir == null || ir.getInputStream() == null || ir.getInputStream().available() <= 0)
					{
						lang_texture = EMUtils.resloc("textures/gui/analyzer/pollution_local/en_us.png");
					}
				}
				catch (IOException e)
				{
					lang_texture = EMUtils.resloc("textures/gui/analyzer/pollution_local/en_us.png");
				}
			}
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(lang_texture);
			this.drawModalRectWithCustomSizedTexture(xt1+4, strt+34, 0, 0, 100, 60, 100, 60);
			
			
			updateEffects();
			this.drawGrid(xt1, 0, buttonAnalyze.yPosition + buttonAnalyze.height + 11, xt1-1, height, mouseX, mouseY);
		}
		
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	
    	if(mouseX > buttonAnalyze.xPosition && mouseX < buttonAnalyze.xPosition + buttonAnalyze.width && mouseY > buttonAnalyze.yPosition && mouseY < buttonAnalyze.yPosition + buttonAnalyze.height)
		{
			if(!buttonAnalyze.enabled)
			{
				List<String> no_energy_text = new ArrayList<String>();
				no_energy_text.add(I18n.format("gui.ecomod.text.no_energy.0", new Object[0]));
				no_energy_text.add(I18n.format("gui.ecomod.text.no_energy.1", new Object[0]));
				this.drawHoveringText(no_energy_text, mouseX, mouseY+10, fontRendererObj);
			}
		}
		
		if(mouseX >= width-110 && mouseX <= width-10 && mouseY >= height-30 && mouseY <= height-10)
		{
			List<String> lst = new ArrayList<String>();
			
			lst.add(I18n.format("gui.ecomod.text.energy", new Object[0])+" "+te.getEnergyStored());
			lst.add(I18n.format("gui.ecomod.text.max_energy", new Object[0])+" "+te.getMaxEnergyStored());
			lst.add(I18n.format("gui.ecomod.text.filling", new Object[0])+" "+(int)(100 * ((float)te.getEnergyStored()/te.getMaxEnergyStored()))+"%");
			
			this.drawHoveringText(lst, mouseX, mouseY, this.fontRendererObj);
		}
    }
	
	private static final int header_width = 80;
	private static final int icon_size = 50;
	
	
	public void drawGrid(int xt1, int startX, int startY, int endX, int endY, int mouseX, int mouseY)
	{
        if(effects.size() > 0)
        {	
        	int num = Math.min(effects.size(), (endY - startY) / icon_size);
        
        	for(int h = startIndex; h < num + startIndex; h++)
        	{
        		if(effects.size() > h)
        		{
        			IAnalyzerPollutionEffect iape = effects.get(h);
        			
        			if(iape != null)
        			{
        				int drawStartX = 0;
        				int drawStartY = startY + 51 * (h - startIndex);
        				
        				//Icon
        				GL11.glColor4f(1, 1, 1, 1);
        				
        				Minecraft.getMinecraft().getTextureManager().bindTexture(iape.getIcon() == null ? IAnalyzerPollutionEffect.BLANK_ICON : iape.getIcon());
        				this.drawModalRectWithCustomSizedTexture(drawStartX, drawStartY, 0, 0, icon_size, icon_size, icon_size, icon_size);
        				
        				//Header
        				this.drawVerticalLine(drawStartX + 50, drawStartY-1, drawStartY + 50, Color.DARK_GRAY.getRGB());
        				
        				//drawString(fontRendererObj, I18n.format(iape.getHeader(), new Object[0]), drawStartX + 51 + 4, drawStartY + 4, Color.ORANGE.getRGB());
        				this.fontRendererObj.drawSplitString(I18n.format(iape.getHeader(), new Object[0]), drawStartX + 51 + 4, drawStartY + 4, header_width,Color.ORANGE.getRGB());
        				
        				this.drawVerticalLine(drawStartX + 50 + header_width, drawStartY-1, drawStartY + 50, Color.DARK_GRAY.getRGB());
        				
        				//Description
        				int textStartX = drawStartX + 51 + header_width + 4;
        				this.fontRendererObj.drawSplitString(I18n.format(iape.getDescription(), new Object[0]), textStartX, drawStartY + 4, endX-4-textStartX, Color.WHITE.getRGB());
        				
        				//Splitting line
        				this.drawHorizontalLine(drawStartX, endX, drawStartY + 50, Color.DARK_GRAY.getRGB());
        			}
        		}
        	}
        }
	}
	
	protected void actionPerformed(GuiButton button)
    {
        if (button.enabled && button.visible)
        {
        	if(button.id == 0)
        	{
        		EMPacketHandler.WRAPPER.sendToServer(new EMPacketString("A"+te.xCoord+";"+te.yCoord+";"+te.zCoord+";"+te.getWorldObj().provider.dimensionId));
        	}
        	
        	if(button.id == 1)
        	{
        		if(startIndex >=1)
        			startIndex--;
        	}
        	
        	if(button.id == 2)
        	{
        		if(startIndex < effects.size() - 2)
        			startIndex++;
        	}
        }
    }
	
	private boolean needScrollBar()
	{
		return pollution != null && effects.size() * icon_size > height - (buttonAnalyze.yPosition + buttonAnalyze.height + 10); 
	}
	
	private void updateEffects()
	{
		effects.clear();
		
		if(EcologyMod.proxy.getClientHandler().pollution_effects == null || EcologyMod.proxy.getClientHandler().pollution_effects.isEmpty())
		{
			effects.add(AnalyzerPollutionEffect.createSimple("effects_not_found", PollutionData.getEmpty(), TriggeringType.AND));
			return;
		}
		
		for(IAnalyzerPollutionEffect iape : EcologyMod.proxy.getClientHandler().pollution_effects.values())
		{
			if(iape.getTriggeringType() == IAnalyzerPollutionEffect.TriggeringType.AND)
			{
				if(pollution.compareTo(iape.getTriggerringPollution()) >= 0)
					effects.add(iape);
			}
			else if (iape.getTriggeringType() == IAnalyzerPollutionEffect.TriggeringType.OR)
			{
				if(pollution.compareOR(iape.getTriggerringPollution()) >= 0)
					effects.add(iape);
			}
		}
	}
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui()
    {
    	super.initGui();
    	
    	this.buttonList.add(buttonAnalyze = new GuiButton(0, width-110, 10, 100, 20, I18n.format("gui.ecomod.button.analyze_chunk", new Object[0])));
    	
    	this.buttonList.add(buttonUp = new ButtonUpDown(1, (int)(width * 0.66) + 4, height / 2 - (int)(ButtonUpDown.sizeY * 1.5F), true));
    	this.buttonList.add(buttonDown = new ButtonUpDown(2, (int)(width * 0.66) + 4, height / 2 + ButtonUpDown.sizeY, false));
    }
    
    public void updateScreen()
    {
        super.updateScreen();
        
        if(buttonAnalyze != null)
        if(te.getEnergyStored() == te.getMaxEnergyStored())
        {
        	if(!buttonAnalyze.enabled)
        		buttonAnalyze.enabled = true;
        }
        else
        {
        	if(buttonAnalyze.enabled)
        		buttonAnalyze.enabled = false;
        }
        
        if(effects.size() > 0)
        {
        	if(buttonUp != null)
        	if(!buttonUp.enabled)
        	{
        		buttonUp.enabled = buttonUp.visible = true;
        	}
        	
        	if(buttonDown != null)
        	if(!buttonDown.enabled)
        	{
        		buttonDown.enabled = buttonDown.visible = true;
        	}
        }
        else
    	{
        	if(buttonUp != null)
    		if(buttonUp.enabled)
    		{
    			buttonUp.enabled = buttonUp.visible = false;
    		}
        	
        	if(buttonDown != null)
    		if(buttonDown.enabled)
    		{
    			buttonDown.enabled = buttonDown.visible = false;
    		}
    	}
        
        if(pollution != te.pollution)
        	pollution = te.pollution;
        
        if(last_update_time != new Date(te.last_analyzed) && te.last_analyzed != -1)
        	last_update_time = new Date(te.last_analyzed);
        
        if (this.mc.thePlayer == null || !this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    public void drawStringNoShadow(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawString(text, x, y, color);
    }
    
    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)x, (double)(y + height), 0.0D, (double)(u * f), (double)((v + (float)height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), 0.0D, (double)((u + (float)width) * f), (double)((v + (float)height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)y, 0.0D, (double)((u + (float)width) * f), (double)(v * f1));
        tessellator.addVertexWithUV((double)x, (double)y, 0.0D, (double)(u * f), (double)(v * f1));
        tessellator.draw();
    }
    
    
    
    @Override
	public void handleMouseInput() {
		super.handleMouseInput();
		
		if(effects.size() > 0)
		{
			int k = Mouse.getEventDWheel();
		
			if(k != 0)
			{
				if(k > 0)
				{
					if(startIndex >=1)
					{
        				startIndex--;
        				if(buttonUp != null)
        				{
        					((ButtonUpDown)buttonUp).activeticks = 20;
        					buttonUp.func_146113_a(this.mc.getSoundHandler());
        				}
					}
				}
				else
				{
					if(startIndex < effects.size() - 2)
					{
						startIndex++;
						if(buttonDown != null)
						{
							((ButtonUpDown)buttonDown).activeticks = 20;
							buttonDown.func_146113_a(this.mc.getSoundHandler());
						}
					}
				}
			}
		}
	}

	@Override
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
		
		if(keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER)
		{
			actionPerformed(buttonAnalyze);
			buttonAnalyze.func_146113_a(this.mc.getSoundHandler());
		}
		
		if(keyCode == Keyboard.KEY_W || keyCode == Keyboard.KEY_UP)
		{
			actionPerformed(buttonUp); 
			buttonUp.func_146113_a(this.mc.getSoundHandler());
			((ButtonUpDown)buttonUp).activeticks = 20;
		}
		
		if(keyCode == Keyboard.KEY_S || keyCode == Keyboard.KEY_DOWN)
		{
			actionPerformed(buttonDown); 
			buttonDown.func_146113_a(this.mc.getSoundHandler());
			((ButtonUpDown)buttonDown).activeticks = 20;
		}
    }

	
	private static class ButtonUpDown extends GuiButton
    {
    	public ButtonUpDown(int buttonId, int x, int y, boolean up) {
			super(buttonId, x, y, sizeX, sizeY, "");
			isUp = up;
		}
    	
		public static final int sizeX = 22;
    	public static final int sizeY = 14;
    	public boolean isUp;
    	public int activeticks = 0;
    	
    	@Override
    	public void drawButton(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                
                if(activeticks > 0)
                {
                	flag = true;
                	activeticks--;
                }
                
                GL11.glColor4f(1, 1, 1, 1);
                
                mc.getTextureManager().bindTexture(new ResourceLocation("ecomod:textures/gui/analyzer/icons/buttons/updown.png"));
                
                int i = 0;
                int j = 0;
                
                if(isUp)
                {
                	j += 14;
                }
                
                if(flag)
                {
                	i += 22;
                }
                
                drawTexturedModalRect(this.xPosition, this.yPosition, i, j, sizeX, sizeY);
            }
        }
    }
}
