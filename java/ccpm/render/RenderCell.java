package ccpm.render;

import javax.vecmath.Matrix4f;

import DummyCore.Client.IItemRenderer;
import DummyCore.Client.RPAwareModel;
import DummyCore.Utils.Coord3D;
import DummyCore.Utils.DrawUtils;
import DummyCore.Utils.Lightning;
import DummyCore.Utils.MathUtils;
import DummyCore.Utils.TessellatorWrapper;
import ccpm.core.CCPM;
import ccpm.tiles.TileEnergyCellBasic;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class RenderCell extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		// TODO Auto-generated method stub
		
	}


	

	/*
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		CCPM.log.debug("Rend");
		if(!(te instanceof TileEnergyCellBasic))return;
		TileEnergyCellBasic teb = (TileEnergyCellBasic) te;
		Lightning light1 = teb.light1;
		Lightning light2 = teb.light2;
		Lightning light3 = teb.light3;
		Lightning light4 = teb.light4;
		if(light1!=null && light2 != null && light3!=null&& light4!=null)
		{
		GlStateManager.pushMatrix();
		CCPM.log.debug("Rendering lightning");
		light1.render(x+0.5D, y+0.5D, z+0.5D, partialTicks);
		light2.render(x+0.5D, y+0.5D, z+0.5D, partialTicks);
		light3.render(x+0.5D, y+0.5D, z+0.5D, partialTicks);
		light4.render(x+0.5D, y+0.5D, z+0.5D, partialTicks);
		
		GlStateManager.popMatrix();
		}
	}
	*/
/*
	class MMC extends ModelBase
	{
		ModelRenderer ren;
		
		public MMC()
		{
			ren = new ModelRenderer(this, textureHeight, textureHeight);
			
			ren.addBox(0, 0, 0, 16, 16, 16, 1);
			
			ren.setRotationPoint(8, 8, 8);
			
			ren.setTextureSize(textureWidth, textureHeight);
			
			setRotation(ren, 0F, 0F, 0F);
		}
		
		 private void setRotation(ModelRenderer model, float x, float y, float z)
		  {
		    model.rotateAngleX = x;
		    model.rotateAngleY = y;
		    model.rotateAngleZ = z;
		  }
		 
		 public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) //Add Entity entity here
		  {
		    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		  }
		 
		 public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
		  {
			 
			    super.render(entity, f, f1, f2, f3, f4, f5);
			    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			    ren.render(f5);
		  }
	}
	*/
}
