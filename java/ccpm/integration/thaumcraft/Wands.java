package ccpm.integration.thaumcraft;

import ccpm.core.CCPM;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;

public class Wands {

	
	public static WandCap CCPB_CAP;
	public static WandRod CCPB_ROD;
	public static WandRod CCPB_ROD_INVERTED;
	public static WandRod STAFF_CCPB_ROD;
	public static WandRod STAFF_CCPB_ROD_INVERTED;
	
	public static void init()
	{
		CCPB_CAP = new WandCap("CCPB", 0.99F,0, new ItemStack(CCPM.miscIngredient,1,3), 3, new ResourceLocation("ccpm:textures/blocks/bricks_pollution"));
		CCPB_ROD = new WandRod("CCPB", 300, new ItemStack(CCPM.miscIngredient,1,4), 3, new WandRodPollutedOnUpdate(), new ResourceLocation("ccpm:textures/blocks/bricks_pollution"));
		CCPB_ROD_INVERTED = new WandRod("CCPB_INVERTED", 320, new ItemStack(CCPM.miscIngredient,1,5), 3, new WandRodPollutedInvertedOnUpdate(), new ResourceLocation("ccpm:textures/items/wand/ccpb_mat_inverted"));
		
		STAFF_CCPB_ROD = new WandRod("STAFF_CCPB", 320, new ItemStack(CCPM.miscIngredient,1,6), 3, new WandRodPollutedOnUpdate(), new ResourceLocation("ccpm:textures/blocks/bricks_pollution"));
		STAFF_CCPB_ROD_INVERTED = new WandRod("STAFF_CCPB_INVERTED", 340, new ItemStack(CCPM.miscIngredient,1,7), 3, new WandRodPollutedInvertedOnUpdate(), new ResourceLocation("ccpm:textures/items/wand/ccpb_mat_inverted"));
		
		STAFF_CCPB_ROD.setStaff(true);
		STAFF_CCPB_ROD_INVERTED.setStaff(true);
	}

}
