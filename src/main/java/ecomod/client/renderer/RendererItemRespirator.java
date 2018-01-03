package ecomod.client.renderer;

import org.lwjgl.opengl.GL11;

import ecomod.common.utils.EMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class RendererItemRespirator implements IItemRenderer
{
	private static final ResourceLocation armor_texture = EMUtils.resloc("textures/models/armor/respirator_layer_1.png");
	private static final ModelRenderer render = new RenderRespirator(null, null).setupRespirator(false);
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		if(type == ItemRenderType.INVENTORY)
		{
			GL11.glRotatef(45, 0, 1, 0);
			GL11.glRotatef(-30, 1, 0, 0);
			GL11.glTranslatef(-0.45F, -0.1F, 0.0F);
			GL11.glScalef(0.9F, 0.9F, 0.9F);
		}
		if(type == ItemRenderType.ENTITY)
		{
			GL11.glTranslatef(-0.5F, 0.1F, -0.5F);
		}
		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glRotatef(-90, 0, 1, 0);
			GL11.glTranslatef(-0.3F, 0.4F, -0.3F);
		}
		if(RenderItem.renderInFrame)
		{
			GL11.glScalef(0.8F, 0.8F, 0.8F);
			GL11.glRotatef(90, 0, 1, 0);
			GL11.glTranslatef(-1.125F, -0.3F, 0.7F);
		}
		
		if(type == ItemRenderType.EQUIPPED)
		{
			GL11.glTranslatef(-0.25F, -0.4F, 0.65F);
			GL11.glScalef(1.25F, 1.25F, 1.25F);
			GL11.glRotatef(-90, 1, 0, 0);
			GL11.glRotatef(45, 0, 0, 1);
			//GL11.glRotatef(15, 0, 1, 0);
		}
		
		GL11.glPushMatrix();
		if(type == ItemRenderType.EQUIPPED)
			GL11.glTranslatef(0.0F, -0.75F, 0.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(armor_texture);
		render.render(0.125F);
		render.postRender(0.125F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
