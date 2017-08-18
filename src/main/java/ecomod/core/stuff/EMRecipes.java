package ecomod.core.stuff;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EMRecipes
{
	public static void doPreInit()
	{
		
	}
	
	public static void doInit()
	{
		GameRegistry.addShapedRecipe(new ItemStack(EcomodBlocks.FRAME, 1, 0), new Object[]{"XYX", "YDY", "XYX", 'X', Blocks.IRON_BLOCK, 'Y', new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.SILVER.getMetadata()), 'D', Items.DIAMOND});
		GameRegistry.addShapedRecipe(new ItemStack(EcomodBlocks.FRAME, 1, 1), new Object[]{"XYX", "YDY", "XYX", 'X', Blocks.DIAMOND_BLOCK, 'Y', Blocks.SEA_LANTERN, 'D', new ItemStack(EcomodBlocks.FRAME, 1, 0)});
		
		GameRegistry.addShapedRecipe(new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 0), new Object[]{"XXX", "XOX", "XXX", 'X', Blocks.PISTON, 'O', new ItemStack(EcomodBlocks.FRAME, 1, 0)});
		GameRegistry.addShapedRecipe(new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 1), new Object[]{"QXQ", "XIX", "QXQ", 'X', Blocks.RAIL, 'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 'Q', Items.QUARTZ});
		
		GameRegistry.addShapedRecipe(new ItemStack(EcomodItems.CORE, 1, 0), new Object[]{"LCL", "KSW", "LXL", 'L', Blocks.LEAVES, 'C', Blocks.COAL_BLOCK, 'K', Blocks.CLAY, 'S', Blocks.SLIME_BLOCK, 'W', Blocks.WOOL, 'X', Blocks.SAND});
		GameRegistry.addShapedRecipe(new ItemStack(EcomodItems.CORE, 1, 1), new Object[]{"AXS", "XCX", "SXS", 'C', new ItemStack(EcomodItems.CORE, 1, 0), 'X', new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 1), 'A', new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 0), 'S', Blocks.SPONGE});
		GameRegistry.addShapedRecipe(new ItemStack(EcomodItems.CORE, 1, 2), new Object[]{"MVB", "FCF", "BSM", 'C', Items.COMPARATOR, 'M', Blocks.BROWN_MUSHROOM, 'B', Items.MUSHROOM_STEW, 'S', Blocks.SAND, 'V', new ItemStack(EcomodItems.CRAFT_INGREDIENT,1,1), 'F', new ItemStack(EcomodItems.CORE,1,0)});
		
		GameRegistry.addShapedRecipe(new ItemStack(EcomodItems.RESPIRATOR, 1, 0), new Object[]{"LHL", "GWG", "FVF", 'L', Items.LEATHER, 'H', Items.LEATHER_HELMET, 'G', Blocks.GLASS_PANE, 'F', EcomodBlocks.FILTER, 'V', new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 1), 'W', Blocks.WOOL});
	}
	
	public static void doPostInit()
	{
		
	}
}
