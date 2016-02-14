package ccpm.potions;

import ccpm.biomes.Wasteland;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionSmog extends Potion {

	public PotionSmog() {
		super(null, false, Wasteland.wastelandColor);
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
