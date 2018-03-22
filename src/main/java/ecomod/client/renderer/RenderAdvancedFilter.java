package ecomod.client.renderer;

import org.lwjgl.opengl.GL11;

import ecomod.api.EcomodItems;
import ecomod.common.tiles.TileAdvancedFilter;
import ecomod.common.utils.EMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class RenderAdvancedFilter extends TileEntitySpecialRenderer<TileAdvancedFilter>
{
	public static TextureAtlasSprite vent_s = null;
	
	@Override
	public void renderTileEntityAt(TileAdvancedFilter te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
		
		renderVents(te, x, y, z, 1F, 1F);
	}
	
	private void renderVents(TileAdvancedFilter tile, double x, double y, double z, float scale, float brightness)
	{
		for(EnumFacing dir : EnumFacing.VALUES)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(x+0.25D + 1/32F + 1/128F, y+0.25D + 1/32F, z+0.5D);
			renderVent(tile.vent_rotation, dir, 1F);
			GL11.glPopMatrix();
		}
	}
	
	private void renderVent(float rotation, EnumFacing dir, float scale)
	{
		float t = 1/16F + scale / 4F;
		
		GL11.glTranslatef(t * dir.getFrontOffsetX(), t * dir.getFrontOffsetY(), t * dir.getFrontOffsetZ());
		
		GL11.glScalef(0.4F, 0.4F, 0.4F);
		
		GL11.glPushMatrix();
		
		if (dir == EnumFacing.UP || dir == EnumFacing.DOWN)
		{
			GL11.glRotatef(90F, 1, 0, 0);
			GL11.glTranslatef(0.025F, -0.5F, -0.5F);
		}
		else if(dir == EnumFacing.EAST || dir == EnumFacing.WEST)
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
		
		if (dir == EnumFacing.UP || dir == EnumFacing.DOWN)
		{
			GL11.glRotatef(rotation, 0, 0, 1);
		}
		else if(dir == EnumFacing.EAST || dir == EnumFacing.WEST)
		{
			GL11.glRotatef(rotation, 0, 0, 1);
		}
		else
			GL11.glRotatef(rotation, dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		GL11.glColor4f(0.87F, 0.87F, 0.87F, 1F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		
        EMUtils.renderItemIn2D(vent_s.getMinU(), vent_s.getMinV(), vent_s.getMaxU(), vent_s.getMaxV(), vent_s.getIconWidth(), vent_s.getIconHeight(), 0.0625F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
	}
}