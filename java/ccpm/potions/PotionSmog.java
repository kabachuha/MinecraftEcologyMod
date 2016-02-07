package ccpm.potions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionSmog extends Potion {

	public PotionSmog(int p_i1573_1_, boolean p_i1573_2_, int p_i1573_3_) {
		super(p_i1573_1_, p_i1573_2_, p_i1573_3_);
		this.setEffectiveness(0.25D);
		this.setPotionName("potion.smog");
	}

	public boolean isUsable()
	{
		return true;
	}
	
	public boolean isBadEffect()
	{
		return true;
	}
	
	//static final ResourceLocation texture = new ResourceLocation("ccpm", "textures/potions.png");
	
	//@SideOnly(Side.CLIENT)
    //public int getStatusIconIndex()
    //{
    //	Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    //    return super.getStatusIconIndex();
    //}
}
