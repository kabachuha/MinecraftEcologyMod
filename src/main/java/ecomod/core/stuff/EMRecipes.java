package ecomod.core.stuff;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import ecomod.core.EcologyMod;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class EMRecipes
{
	public static void doPreInit()
	{
		
	}
	
	public static void doInit()
	{
		EcologyMod.log.info("Registering recipes");
		// Frames
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(EcomodBlocks.FRAME, 1, 0), new Object[]{"XYX", "YDY", "XYX", 'X', "blockIron", 'Y', new ItemStack(Blocks.stained_hardened_clay, 1, 7), 'D', "gemDiamond"}
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(EcomodBlocks.FRAME, 1, 1), new Object[]{"XYX", "YDY", "XYX", 'X', "blockDiamond", 'Y', Items.blaze_rod, 'D', new ItemStack(EcomodBlocks.FRAME, 1, 0)}
				));
		
		// Craft ingredients
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 0), new Object[]{"XXX", "XOX", "XXX", 'X', Blocks.piston, 'O', new ItemStack(EcomodBlocks.FRAME, 1, 0)}
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 1), new Object[]{"QXQ", "XIX", "QXQ", 'X', Blocks.rail, 'I', Blocks.heavy_weighted_pressure_plate, 'Q', "gemQuartz"}
				));
		
		// Machine cores
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(EcomodItems.CORE, 1, 0), new Object[]{"LCL", "KSW", "LXL", 'L', "treeLeaves", 'C', "blockCoal", 'K', Blocks.clay, 'S', "blockSlime", 'W', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), 'X', "sand"}
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(EcomodItems.CORE, 1, 1), new Object[]{"AXS", "XCX", "SXS", 'C', new ItemStack(EcomodItems.CORE, 1, 0), 'X', new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 1), 'A', new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 0), 'S', new ItemStack(Blocks.sponge, 1, 0)}
		));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(EcomodItems.CORE, 1, 2), new Object[]{"MVB", "FCF", "BSM", 'C', OreDictionary.doesOreNameExist("circuitAdvanced") ? "circuitAdvanced" : new ItemStack(Items.comparator), 'M', Blocks.brown_mushroom, 'B', Items.mushroom_stew, 'S', "sand", 'V', new ItemStack(EcomodItems.CRAFT_INGREDIENT,1,1), 'F', new ItemStack(EcomodItems.CORE,1,0)}
		));
		
		//Respirator
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(EcomodItems.RESPIRATOR, 1, 0), new Object[]{"LHL", "GWG", "FVF", 'L', "leather", 'H', Items.leather_helmet, 'G', "paneGlassColorless", 'F', EcomodBlocks.FILTER, 'V', new ItemStack(EcomodItems.CRAFT_INGREDIENT, 1, 1), 'W', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE)}
		));
	}
	
	public static void doPostInit()
	{
		
	}
}
