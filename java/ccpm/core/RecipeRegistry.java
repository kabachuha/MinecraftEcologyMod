package ccpm.core;

import DummyCore.Utils.OreDictUtils;
import ccpm.utils.config.CCPMConfig;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
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
	}

	public static void init()
	{
		oreDictSetup();
		CCPM.log.info("Registring recipes");
		ItemStack redbl = new ItemStack(Blocks.redstone_block);
		ItemStack ironb = new ItemStack(Blocks.iron_block);
		ItemStack goldb = new ItemStack(Blocks.gold_block);
		

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CCPM.cell,1,0),new Object[]{
		    "IGI",
		    "GRG",
		    "IGI",
		    'I',"blockIron", 'G', "blockGold", 'R', "blockRedstone"}));
		
		ItemStack pollutionBrick = new ItemStack(CCPM.pollutionBrick);
		ItemStack pollutionBricks = new ItemStack(CCPM.pollutionBricks);
		
		GameRegistry.addShapedRecipe(pollutionBricks, 
			"BB",
			"BB", 'B',pollutionBrick);
		
		GameRegistry.addShapedRecipe(new ItemStack(CCPM.pollArmor[0]), 
			"PPP",
			"PHP",'P',pollutionBricks,'H',new ItemStack(Items.iron_helmet));
		
		GameRegistry.addShapedRecipe(new ItemStack(CCPM.pollArmor[1]), 
				"P P",
				"PCP",
				"PPP",'P',pollutionBricks,'C',new ItemStack(Items.iron_chestplate));
		
		GameRegistry.addShapedRecipe(new ItemStack(CCPM.pollArmor[2]), 
				"PPP",
				"PLP",
				"P P",'P',pollutionBricks,'L',new ItemStack(Items.iron_leggings));
		
		GameRegistry.addShapedRecipe(new ItemStack(CCPM.pollArmor[3]), 
				"PPP",
				"PBP",'P',pollutionBricks,'B',new ItemStack(Items.iron_boots));
		
		ItemStack glass = new ItemStack(Blocks.glass_pane);
		ItemStack helm = new ItemStack(Items.leather_helmet,1,OreDictionary.WILDCARD_VALUE);
		ItemStack wool = new ItemStack(Blocks.wool,1,OreDictionary.WILDCARD_VALUE);
		ItemStack leath = new ItemStack(Items.leather);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CCPM.respirator), new Object[]{
			    "LHL",
			    "G G",
			    "LWL",
			    'L', leath, 'G', "paneGlass", 'W', wool, 'H', helm}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CCPM.filter), new Object[]{
			    "ILI",
			    "LRL",
			    "ILI",
			    'L', leath, 'I', "blockIron", 'R', "blockRedstone"}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CCPM.an), new Object[]{
			    "GMG",
			    "MRM",
			    "GMG",
			    'G', "blockGold", 'M', mushroom, 'R', "blockRedstone"}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CCPM.baf), new Object[]{
			    "DFD",
			    "FSF",
			    "DFD",
			    'D', "blockDiamond", 'F', new ItemStack(CCPM.filter), 'S', new ItemStack(Blocks.sea_lantern)}));
		
		GameRegistry.addShapelessRecipe(new ItemStack(CCPM.pistons), new ItemStack(Blocks.piston), new ItemStack(Blocks.piston), new ItemStack(Blocks.piston), new ItemStack(Blocks.piston), new ItemStack(Blocks.piston), new ItemStack(Blocks.piston), new ItemStack(Blocks.piston), new ItemStack(Blocks.piston), new ItemStack(Blocks.piston));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CCPM.compressor), new Object[]{
			    "RPR",
			    "POP",
			    "RPR",
			    'R', "blockRedstone", 'O', new ItemStack(Blocks.obsidian), 'P', new ItemStack(CCPM.pistons)}));
		
		if(Loader.isModLoaded("Thaumcraft")||Loader.isModLoaded("thaumcraft"))
			thaum();
		
		//if(Loader.isModLoaded("Botania"))
		//	botan();
	}
	
	public static final ResourceLocation back = new ResourceLocation("thaumcraft","textures/gui/gui_research_back_over.png");
	
	public static void thaum()
	{
		CCPM.log.info("Adding thaumcraft support");
		ResearchCategories.registerCategory((String)CATID, null, (ResourceLocation)iconLoc, (ResourceLocation)new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_1.jpg"), (ResourceLocation) back);
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.respirator,1,OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.PROTECT, 6).add(Aspect.AIR, 16).add(Aspect.SENSES, 8).add(Aspect.LIFE, 8).add(Aspect.CRYSTAL, 9).add(Aspect.METAL, 20).add(Aspect.MECHANISM, 9).add(Aspect.EXCHANGE, 7));
		
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.cell, 1 , 0), new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.ENERGY, 128).add(Aspect.METAL, 64));
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.cell, 1 , 1), new AspectList().add(Aspect.AURA, 16).add(Aspect.ENERGY, 128).add(Aspect.METAL, 64).add(Aspect.ORDER, 64));
		
		ResearchItem ri = new ResearchItem("CCPMCELL", CATID, new AspectList().add(Aspect.ENERGY, 32).add(Aspect.AURA, 16).add(Aspect.MECHANISM, 8), 0, 0, 1, new ItemStack(CCPM.cell,1,1));
		
		ItemStack thb = new ItemStack(BlocksTC.metal,1,0);
		//ItemStack blg = new ItemStack(Blocks.gold_block);
		String blg = "blockGold";
		ItemStack main = new ItemStack(Blocks.redstone_block);
		
		if(Loader.isModLoaded("thaumicbases")||Loader.isModLoaded("Thaumic Bases"))
		{
			thb = new ItemStack(GameRegistry.findBlock("thaumicbases", "thauminiteBlock"));
		}
		
		InfusionRecipe cellrec = ThaumcraftApi.addInfusionCraftingRecipe("CCPMCELL", new ItemStack(CCPM.cell,1,1), 3, new AspectList().add(Aspect.ENERGY, 128).add(Aspect.ORDER, 64).add(Aspect.CRYSTAL, 32).add(Aspect.AURA, 8).add(Aspect.MECHANISM, 64), main, new Object[]{thb,blg,thb,blg,thb,blg,thb,blg});
		
		ri.setParents("INFUSION");
		ri.setPages(new ResearchPage("ccpm.cell"), new ResearchPage(cellrec));
		
		ri.registerResearchItem();
		
		ItemStack dummyRespirator = new ItemStack(CCPM.respirator);
		dummyRespirator.addEnchantment(Enchantment.fireAspect, 1);
		
		ResearchItem rii = new ResearchItem("CCPMREV",CATID, new AspectList().add(Aspect.PROTECT, 10).add(Aspect.AURA, 16).add(Aspect.AIR, 6), 2, 3, 3, dummyRespirator);
		rii.setRound();
		rii.setSpecial();
		rii.setParents("CCPMCELL");
		
		InfusionRecipe respinf = ThaumcraftApi.addInfusionCraftingRecipe("CCPMREV", new Object[]{"revealing", new NBTTagInt(1)}, 5, new AspectList().add(Aspect.SENSES, 32).add(Aspect.AURA, 16).add(Aspect.PROTECT, 16).add(Aspect.AIR, 64), new ItemStack(CCPM.respirator,1,OreDictionary.WILDCARD_VALUE), new ItemStack[]{new ItemStack(ItemsTC.goggles,1, OreDictionary.WILDCARD_VALUE), new ItemStack(Blocks.slime_block), new ItemStack(CCPM.cell,1,1)});
		
		rii.setPages(new ResearchPage("ccpm.resp"), new ResearchPage(respinf));
		
		rii.registerResearchItem();
		
		ResearchItem advThaum = new ResearchItem("CCPMADVTHAUM", CATID, new AspectList(new ItemStack(ItemsTC.primordialPearl,1)), 3, 9, -6, new ItemStack(ItemsTC.primordialPearl));
		advThaum.setParents("CCPMCELL","CCPMREV");
		advThaum.setRound();
		advThaum.setSpecial();
		
		AspectList al = new AspectList();
		
		for(Aspect a : Aspect.getPrimalAspects())
		{
			al.add(a, 64);
		}
		
		al.add(Aspect.AURA, 32);
		al.add(Aspect.CRYSTAL, 64);
		al.add(Aspect.DARKNESS, 32);
		al.add(Aspect.ELDRITCH, 48);
		al.add(Aspect.ENERGY, 128);
		al.add(Aspect.EXCHANGE, 48);
		al.add(Aspect.MECHANISM, 64);
		
		ItemStack eye = new ItemStack(ItemsTC.eldritchEye);
		ItemStack gear = new ItemStack(ItemsTC.gear,1,2);
		ItemStack matrix = new ItemStack(BlocksTC.infusionMatrix);
		ItemStack totem = new ItemStack(BlocksTC.auraTotem,1,0);
		ItemStack bv = new ItemStack(BlocksTC.metal,1,1);
		ItemStack eldr = new ItemStack(BlocksTC.nodeStabilizer);
		
		
		InfusionRecipe advRec = ThaumcraftApi.addInfusionCraftingRecipe("CCPMADVTHAUM", new ItemStack(CCPM.advThaum), 5, al, new ItemStack(ItemsTC.primordialPearl), new Object[]{eye,matrix,bv,matrix,totem,matrix,gear,matrix,eldr,matrix});
		
		advThaum.setPages(new ResearchPage("ccpm.advThaum"), new ResearchPage(advRec));
		
		advThaum.registerResearchItem();
		
		ThaumcraftApi.addWarpToResearch("CCPMADVTHAUM", 16);
		ThaumcraftApi.addWarpToItem(new ItemStack(CCPM.advThaum), 16);
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.advThaum), al);
	}
	
	public static void botan()
	{
		//FMLLog.info("[CCPM]Adding botania support");
		
		
		//BotaniaAPI.registerRuneAltarRecipe(new ItemStack(CCPM.cell,1,2), 1000, "manaSteel", "manaPearl", "manaDiamond", new ItemStack(Blocks.diamond_block), new ItemStack(Blocks.redstone_block));
	}
	
	public static final String CATID = "CCPM";
	
	public static final ResourceLocation iconLoc = new ResourceLocation(CCPM.MODID+":textures/items/repsirator.png");
	
	public static String mushroom = "blockMushroom";
	
	public static void oreDictSetup()
	{
		CCPM.log.info("Registering OreDictionary entries");
		if(!OreDictionary.doesOreNameExist(mushroom))
		{
			OreDictionary.registerOre(mushroom, Blocks.brown_mushroom_block);
			OreDictionary.registerOre(mushroom, Blocks.red_mushroom_block);
		}
		
		OreDictionary.registerOre("ingotBrick", CCPM.pollutionBrick);
		OreDictionary.registerOre("ingotBrickNether", CCPM.pollutionBrick);
		
		if(!OreDictionary.doesOreNameExist("blockBrick"))
		{
			OreDictionary.registerOre("blockBrick", Blocks.brick_block);
			OreDictionary.registerOre("blockBrick", Blocks.nether_brick);
			OreDictionary.registerOre("blockBrick", Blocks.stonebrick);
		}
		
		OreDictionary.registerOre("blockBrick", CCPM.pollutionBricks);
	}
	
	
}
