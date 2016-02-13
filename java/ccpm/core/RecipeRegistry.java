package ccpm.core;

import ccpm.utils.config.CCPMConfig;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

public class RecipeRegistry {

	public RecipeRegistry() {
		// TODO Auto-generated constructor stub
	}

	public static void init()
	{
		FMLLog.info("[CCPM]Registring recipes");
		ItemStack redbl = new ItemStack(Blocks.redstone_block);
		ItemStack ironb = new ItemStack(Blocks.iron_block);
		ItemStack goldb = new ItemStack(Blocks.gold_block);

		GameRegistry.addRecipe(new ItemStack(CCPM.cell,1,0),
		    "IGI",
		    "GRG",
		    "IGI",
		    'I',ironb, 'G', goldb, 'R', redbl);
		
		ItemStack glass = new ItemStack(Blocks.glass_pane);
		ItemStack helm = new ItemStack(Items.leather_helmet,1,OreDictionary.WILDCARD_VALUE);
		ItemStack wool = new ItemStack(Blocks.wool,1,OreDictionary.WILDCARD_VALUE);
		ItemStack leath = new ItemStack(Items.leather);
		
		GameRegistry.addRecipe(new ItemStack(CCPM.respirator),
			    "LHL",
			    "G G",
			    "LWL",
			    'L', leath, 'G', glass, 'W', wool, 'H', helm);
		
		if(Loader.isModLoaded("Thaumcraft")||Loader.isModLoaded("thaumcraft"))
			thaum();
		
		if(Loader.isModLoaded("Botania"))
			botan();
	}
	
	public static void thaum()
	{
		FMLLog.info("[CCPM]Adding thaumcraft support");
		ResearchCategories.registerCategory((String)CATID, (ResourceLocation)iconLoc, (ResourceLocation)new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.respirator,1,OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.ARMOR, 6).add(Aspect.AIR, 16).add(Aspect.CLOTH, 9).add(Aspect.POISON, 8).add(Aspect.HEAL, 8).add(Aspect.CRYSTAL, 9).add(Aspect.METAL, 20).add(Aspect.MECHANISM, 9).add(Aspect.EXCHANGE, 7));
		
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.cell, 1 , 0), new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.ENERGY, 128).add(Aspect.METAL, 64));
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.cell, 1 , 1), new AspectList().add(Aspect.AURA, 16).add(Aspect.ENERGY, 128).add(Aspect.METAL, 64).add(Aspect.MAGIC, 64));
		
		ResearchItem ri = new ResearchItem("CCPMCELL", CATID, new AspectList().add(Aspect.ENERGY, 32).add(Aspect.MAGIC, 16).add(Aspect.MECHANISM, 8), 0, 0, 1, new ItemStack(CCPM.cell,1,1));
		
		ItemStack thb = ItemApi.getBlock("blockCosmeticSolid", 4);
		ItemStack blg = new ItemStack(Blocks.gold_block);
		ItemStack main = CCPMConfig.useNode ? ItemApi.getItem("itemJarNode", 0) : new ItemStack(Blocks.redstone_block);
		
		InfusionRecipe cellrec = ThaumcraftApi.addInfusionCraftingRecipe("CCPMCELL", new ItemStack(CCPM.cell,1,1), 3, new AspectList().add(Aspect.ENERGY, 128).add(Aspect.MAGIC, 64).add(Aspect.CRYSTAL, 32).add(Aspect.AURA, 8).add(Aspect.MECHANISM, 64), main, new ItemStack[]{thb,blg,thb,blg,thb,blg,thb,blg});
		
		ri.setParents("INFUSION");
		ri.setPages(new ResearchPage("ccpm.cell"), new ResearchPage(cellrec));
		
		ri.registerResearchItem();
		
		ItemStack dummyRespirator = new ItemStack(CCPM.respirator);
		dummyRespirator.addEnchantment(Enchantment.fireAspect, 1);
		
		ResearchItem rii = new ResearchItem("CCPMREV",CATID, new AspectList().add(Aspect.ARMOR, 10).add(Aspect.AURA, 16).add(Aspect.AIR, 6), 2, 3, 3, dummyRespirator);
		rii.setRound();
		rii.setSpecial();
		rii.setParents("CCPMCELL");
		
		InfusionRecipe respinf = ThaumcraftApi.addInfusionCraftingRecipe("CCPMREV", new Object[]{"revealing", new NBTTagInt(1)}, 5, new AspectList().add(Aspect.SENSES, 32).add(Aspect.AURA, 16).add(Aspect.ARMOR, 16).add(Aspect.AIR, 64), new ItemStack(CCPM.respirator,1,OreDictionary.WILDCARD_VALUE), new ItemStack[]{ItemApi.getItem("itemGoggles", OreDictionary.WILDCARD_VALUE), new ItemStack(Items.slime_ball), new ItemStack(CCPM.cell,1,1)});
		
		rii.setPages(new ResearchPage("ccpm.resp"), new ResearchPage(respinf));
		
		rii.registerResearchItem();
	}
	
	public static void botan()
	{
		FMLLog.info("[CCPM]Adding botania support");
		
		
		BotaniaAPI.registerRuneAltarRecipe(new ItemStack(CCPM.cell,1,2), 1000, "manaSteel", "manaPearl", "manaDiamond", new ItemStack(Blocks.diamond_block), new ItemStack(Blocks.redstone_block));
	}
	
	public static final String CATID = "CCPM";
	
	public static final ResourceLocation iconLoc = new ResourceLocation(CCPM.MODID+":textures/items/repsirator.png");
}
