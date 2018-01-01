package ecomod.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class RenderRespirator extends ModelBiped
{
	public RenderRespirator(EntityLivingBase entity, ItemStack respirator)
	{
		super(1.0F);
		
		this.bipedHead.addChild(setupRespirator());
		
		this.bipedBody.isHidden = true;
		this.bipedCloak.isHidden = true;
		this.bipedEars.isHidden = true;
		this.bipedLeftArm.isHidden = true;
		this.bipedLeftLeg.isHidden = true;
		this.bipedRightArm.isHidden = true;
		this.bipedRightLeg.isHidden = true;
		this.bipedHeadwear.isHidden = true;
	}
	
	public ModelRenderer setupRespirator()
	{
		ModelRenderer respirator = new ModelRenderer(this, "respirator"){
			@Override
			public void render(float scale)
		    {
				super.render(scale * 0.5F);
		    }
		};
		respirator.offsetX = -0.25F;
		respirator.offsetY = -0.5F/16F;
		respirator.offsetZ = -4.21F/16F;
		
		respirator.rotateAngleY = (float)Math.PI;
		respirator.rotateAngleZ = (float)Math.PI;
		//Respirator base:
		
		respirator.cubeList.add(new ModelBox(respirator, 0, 20, -1, -4, 0, 18, 6, 4, 1F));//Base
		respirator.cubeList.add(new ModelBox(respirator, 4, 22, -1, 4, 0, 1, 6, 1, 1F));//Wall1
		respirator.cubeList.add(new ModelBox(respirator, 4, 22, 16, 4, 0, 1, 6, 1, 1F));//Wall2
		respirator.cubeList.add(new ModelBox(respirator, 2, 22, 2, 12, 0, 12, 1, 1, 1F));//Top
		respirator.cubeList.add(new ModelBox(respirator, 32, 22, 4, -6, 6, 8, 8, 2, 1F));//Core
		respirator.cubeList.add(new ModelBox(respirator, 32, 0, 2, 4, 3, 12, 6, 1, 1F));//Glass
		
		//Respirator filters:
		
		ModelRenderer l_filter = new ModelRenderer(this, 32, 7);
		
		l_filter.rotateAngleY = (float)(Math.PI / 4);
		
		l_filter.offsetX = 0.35F;
		l_filter.offsetY = -0.25F;
		l_filter.offsetZ = 0.35F;
		
		l_filter.addBox(0, 0, 0, 6, 6, 4, 1F);
		
		respirator.addChild(l_filter);
		
		ModelRenderer r_filter = new ModelRenderer(this, 32, 7);
		
		r_filter.rotateAngleY = -(float)(Math.PI / 4);
		
		r_filter.offsetX = 0.0F;
		r_filter.offsetY = -0.25F;
		r_filter.offsetZ = 0.2F;
		
		r_filter.addBox(0, 0, 0, 6, 6, 4, 1F);
		
		respirator.addChild(r_filter);
		
		return respirator;
	}
	
}
