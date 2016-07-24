package ccpm.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MiscUtils
{
	public static void saveInventory(TileEntity t, NBTTagCompound saveTag)
	{
		if(t instanceof IInventory)
		{
			IInventory tile = (IInventory) t;
	        NBTTagList nbttaglist = new NBTTagList();
	        for (int i = 0; i < tile.getSizeInventory(); ++i)
	        {
	            if (tile.getStackInSlot(i) != null)
	            {
	                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
	                nbttagcompound1.setByte("Slot", (byte)i);
	                tile.getStackInSlot(i).writeToNBT(nbttagcompound1);
	                nbttaglist.appendTag(nbttagcompound1);
	            }
	        }
	        saveTag.setTag("Items", nbttaglist);
		}
	}
	
	public static void loadInventory(TileEntity t, NBTTagCompound loadTag)
	{
		if(t instanceof IInventory)
		{
			IInventory tile = (IInventory) t;
			for(int i = 0; i < tile.getSizeInventory(); ++i)
			{
				tile.setInventorySlotContents(i, null);
			}
	        NBTTagList nbttaglist = loadTag.getTagList("Items", 10);
	        for (int i = 0; i < nbttaglist.tagCount(); ++i)
	        {
	            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
	            byte b0 = nbttagcompound1.getByte("Slot");
	
	            if (b0 >= 0 && b0 < tile.getSizeInventory())
	            {
	            	tile.setInventorySlotContents(b0, ItemStack.loadItemStackFromNBT(nbttagcompound1));
	            }
	        }
		}
	}
	
	public static int pixelatedTextureSize(int current, int max, int textureSize)
	{
		if(current > max)
			current = max;
    	float m = (float)current/max*100;
    	float n = m/100*textureSize;
    	return (int)n;
	}
	
	@SideOnly(Side.CLIENT)
	public static void drawScaledTexturedRectFromTAS(int x, int y, TextureAtlasSprite tas, int w, int h, float z)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		double minU = tas.getMinU();
		double maxU = tas.getMaxU();
		double minV = tas.getMinV();
		double maxV = tas.getMaxV();
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer vb = tess.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
		vb.pos(x + 0, y + h, z).tex(minU, minV + ((maxV - minV) * h) / 16D).endVertex();
		vb.pos(x + w, y + h, z).tex(minU + ((maxU - minU) * w) / 16D, minV + ((maxV - minV) * h) / 16D).endVertex();
		vb.pos(x + w, y + 0, z).tex(minU + ((maxU - minU) * w) / 16D, minV).endVertex();
		vb.pos(x + 0, y + 0, z).tex(minU, minV).endVertex();
		tess.draw();
	}
	
	@SideOnly(Side.CLIENT)
    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int sizeX, int sizeY, int zLevel)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer vb = tess.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(x + 0, y + sizeY, zLevel).tex((textureX + 0) * f, (textureY + sizeY) * f1).endVertex();
        vb.pos(x + sizeX, y + sizeY, zLevel).tex((textureX + sizeX) * f, (textureY + sizeY) * f1).endVertex();
        vb.pos(x + sizeX, y + 0, zLevel).tex((textureX + sizeX) * f, (textureY + 0) * f1).endVertex();
        vb.pos(x + 0, y + 0, zLevel).tex((textureX + 0) * f, (textureY + 0) * f1).endVertex();
        tess.draw();
    }
}
