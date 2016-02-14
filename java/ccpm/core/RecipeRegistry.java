package ccpm.core;

import ccpm.utils.config.CCPMConfig;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;

public class RecipeRegistry {

	public RecipeRegistry() {
		// TODO Auto-generated constructor stub
	}

	public static void init()
	{
		CCPM.log.info("[CCPM]Registring recipes");
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
		
		//if(Loader.isModLoaded("Botania"))
		//	botan();
	}
	
	public static final ResourceLocation back = new ResourceLocation("thaumcraft","textures/gui/gui_research_back_over.png");
	
	public static void thaum()
	{
		CCPM.log.info("Adding thaumcraft support");
		ResearchCategories.registerCategory((String)CATID, null, (ResourceLocation)iconLoc, (ResourceLocation)new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"), (ResourceLocation) back);
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.respirator,1,OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.PROTECT, 6).add(Aspect.AIR, 16).add(Aspect.SENSES, 8).add(Aspect.LIFE, 8).add(Aspect.CRYSTAL, 9).add(Aspect.METAL, 20).add(Aspect.MECHANISM, 9).add(Aspect.EXCHANGE, 7));
		
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.cell, 1 , 0), new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.ENERGY, 128).add(Aspect.METAL, 64));
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.cell, 1 , 1), new AspectList().add(Aspect.AURA, 16).add(Aspect.ENERGY, 128).add(Aspect.METAL, 64).add(Aspect.ORDER, 64));
		
		ResearchItem ri = new ResearchItem("CCPMCELL", CATID, new AspectList().add(Aspect.ENERGY, 32).add(Aspect.AURA, 16).add(Aspect.MECHANISM, 8), 0, 0, 1, new ItemStack(CCPM.cell,1,1));
		
		ItemStack thb = new ItemStack(BlocksTC.metal,1,0);
		ItemStack blg = new ItemStack(Blocks.gold_block);
		ItemStack main = new ItemStack(Blocks.redstone_block);
		
		InfusionRecipe cellrec = ThaumcraftApi.addInfusionCraftingRecipe("CCPMCELL", new ItemStack(CCPM.cell,1,1), 3, new AspectList().add(Aspect.ENERGY, 128).add(Aspect.ORDER, 64).add(Aspect.CRYSTAL, 32).add(Aspect.AURA, 8).add(Aspect.MECHANISM, 64), main, new ItemStack[]{thb,blg,thb,blg,thb,blg,thb,blg});
		
		ri.setParents("INFUSION");
		ri.setPages(new ResearchPage("ccpm.cell"), new ResearchPage(cellrec));
		
		ri.registerResearchItem();
		
		ItemStack dummyRespirator = new ItemStack(CCPM.respirator);
		dummyRespirator.addEnchantment(Enchantment.fireAspect, 1);
		
		ResearchItem rii = new ResearchItem("CCPMREV",CATID, new AspectList().add(Aspect.PROTECT, 10).add(Aspect.AURA, 16).add(Aspect.AIR, 6), 2, 3, 3, dummyRespirator);
		rii.setRound();
		rii.setSpecial();
		rii.setParents("CCPMCELL");
		
		InfusionRecipe respinf = ThaumcraftApi.addInfusionCraftingRecipe("CCPMREV", new Object[]{"revealing", new NBTTagInt(1)}, 5, new AspectList().add(Aspect.SENSES, 32).add(Aspect.AURA, 16).add(Aspect.PROTECT, 16).add(Aspect.AIR, 64), new ItemStack(CCPM.respirator,1,OreDictionary.WILDCARD_VALUE), new ItemStack[]{new ItemStack(ItemsTC.goggles,1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.slime_ball), new ItemStack(CCPM.cell,1,1)});
		
		rii.setPages(new ResearchPage("ccpm.resp"), new ResearchPage(respinf));
		
		rii.registerResearchItem();
	}
	
	public static void botan()
	{
		//FMLLog.info("[CCPM]Adding botania support");
		
		
		//BotaniaAPI.registerRuneAltarRecipe(new ItemStack(CCPM.cell,1,2), 1000, "manaSteel", "manaPearl", "manaDiamond", new ItemStack(Blocks.diamond_block), new ItemStack(Blocks.redstone_block));
	}
	
	public static final String CATID = "CCPM";
	
	public static final ResourceLocation iconLoc = new ResourceLocation(CCPM.MODID+":textures/items/repsirator.png");
}
