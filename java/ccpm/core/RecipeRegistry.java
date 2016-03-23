package ccpm.core;

import java.awt.Color;

import DummyCore.Utils.OreDictUtils;
import ccpm.integration.thaumcraft.Wands;
import ccpm.utils.config.CCPMConfig;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
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
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.items.ItemsTC;

public class RecipeRegistry {

	public RecipeRegistry() {
	}

	public static void init()
	{
		oreDictSetup();
		CCPM.log.info("Registring recipes");
		
		ItemStack pollutionBrick = new ItemStack(CCPM.pollutionBrick);
		ItemStack pollutionBricks = new ItemStack(CCPM.pollutionBricks);
		ItemStack ccpbDust = new ItemStack(CCPM.miscIngredient,1,0);
		ItemStack ccpbTinyDust = new ItemStack(CCPM.miscIngredient,1,1);
		ItemStack ccpbNugget = new ItemStack(CCPM.miscIngredient,1,2);
		
		
		GameRegistry.addSmelting(ccpbDust, pollutionBrick, 20);
		GameRegistry.addSmelting(ccpbTinyDust, ccpbNugget, 10);
		
		ItemStack redbl = new ItemStack(Blocks.redstone_block);
		ItemStack ironb = new ItemStack(Blocks.iron_block);
		ItemStack goldb = new ItemStack(Blocks.gold_block);
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(pollutionBrick, "nuggetCCPB","nuggetCCPB","nuggetCCPB","nuggetCCPB","nuggetCCPB","nuggetCCPB","nuggetCCPB","nuggetCCPB","nuggetCCPB"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CCPM.miscIngredient,9,2), "materialCCPB"));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(ccpbDust, "tinyDustCCPB", "tinyDustCCPB","tinyDustCCPB","tinyDustCCPB","tinyDustCCPB","tinyDustCCPB","tinyDustCCPB","tinyDustCCPB","tinyDustCCPB"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ccpbDust, "dustTinyCCPB", "dustTinyCCPB","dustTinyCCPB","dustTinyCCPB","dustTinyCCPB","dustTinyCCPB","dustTinyCCPB","dustTinyCCPB","dustTinyCCPB"));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(CCPM.miscIngredient,9,1),"dustCCPB"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CCPM.cell,1,0),new Object[]{
		    "IGI",
		    "GRG",
		    "IGI",
		    'I',"blockIron", 'G', "blockGold", 'R', "blockRedstone"}));
		
		
		
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
		ItemStack pollutionBrick = new ItemStack(CCPM.pollutionBrick);
		ItemStack pollutionBricks = new ItemStack(CCPM.pollutionBricks);
		ItemStack ccpbDust = new ItemStack(CCPM.miscIngredient,1,0);
		
		CCPM.log.info("Adding thaumcraft support");
		
		ThaumcraftApi.addSmeltingBonus("dustCCPB", new ItemStack(CCPM.miscIngredient,0,2));
		ThaumcraftApi.addSmeltingBonus("dustCCPB", new ItemStack(CCPM.miscIngredient,0,1));
		
		ResearchCategories.registerCategory((String)CATID, null, (ResourceLocation)iconLoc, (ResourceLocation)new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_1.jpg"), (ResourceLocation) back);
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.respirator,1,OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.PROTECT, 6).add(Aspect.AIR, 16).add(Aspect.SENSES, 8).add(Aspect.LIFE, 8).add(Aspect.CRYSTAL, 9).add(Aspect.METAL, 20).add(Aspect.MECHANISM, 9).add(Aspect.EXCHANGE, 7));
		
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.cell, 1 , 0), new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.ENERGY, 128).add(Aspect.METAL, 64));
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.cell, 1 , 1), new AspectList().add(Aspect.AURA, 16).add(Aspect.ENERGY, 128).add(Aspect.METAL, 64).add(Aspect.ORDER, 64));
		
		ThaumcraftApi.registerObjectTag("materialCCPB", new AspectList().add(Aspect.ENTROPY, 8).add(Aspect.FLUX, 6).add(Aspect.DEATH, 8));
		
		ThaumcraftApi.addLootBagItem(new ItemStack(CCPM.pollutionBrick), 25, 0,1,2);
		
		ResearchItem ri = new ResearchItem("CCPMCELL", CATID, new AspectList().add(Aspect.ENERGY, 32).add(Aspect.AURA, 16).add(Aspect.MECHANISM, 8), 0, 0, 1, new ItemStack(CCPM.cell,1,1));
		
		ItemStack thb = new ItemStack(BlocksTC.metal,1,0);
		//ItemStack blg = new ItemStack(Blocks.gold_block);
		String blg = "blockGold";
		ItemStack main = new ItemStack(ItemsTC.alumentum);
		ItemStack focus = new ItemStack(ItemsTC.focusEqualTrade);
		ItemStack totem = new ItemStack(BlocksTC.auraTotem,1,1);
		
		if(Loader.isModLoaded("thaumicbases")||Loader.isModLoaded("Thaumic Bases"))
		{
			thb = new ItemStack(GameRegistry.findBlock("thaumicbases", "thauminiteBlock"));
		}
		
		InfusionRecipe cellrec = ThaumcraftApi.addInfusionCraftingRecipe("CCPMCELL", new ItemStack(CCPM.cell,1,1), 3, new AspectList().add(Aspect.ENERGY, 128).add(Aspect.ORDER, 64).add(Aspect.CRYSTAL, 32).add(Aspect.AURA, 8).add(Aspect.MECHANISM, 64), main, new Object[]{focus,thb,blg,totem,thb,blg,thb,blg,totem,thb,blg,focus});
		
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
		
		ResearchItem advThaum = new ResearchItem("CCPMADVTHAUM", CATID, new AspectList(new ItemStack(ItemsTC.primordialPearl,1)), 3, 9, -6, new ItemStack(CCPM.advThaum));
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
		totem = new ItemStack(BlocksTC.auraTotem,1,1);
		ItemStack bv = new ItemStack(BlocksTC.metal,1,1);
		ItemStack eldr = new ItemStack(BlocksTC.nodeStabilizer);
		
		
		InfusionRecipe advRec = ThaumcraftApi.addInfusionCraftingRecipe("CCPMADVTHAUM", new ItemStack(CCPM.advThaum), 5, al, new ItemStack(ItemsTC.primordialPearl), new Object[]{eye,matrix,bv,matrix,totem,matrix,gear,matrix,eldr,matrix});
		
		advThaum.setPages(new ResearchPage("ccpm.advThaum"), new ResearchPage(advRec));
		
		advThaum.registerResearchItem();
		
		ThaumcraftApi.addWarpToResearch("CCPMADVTHAUM", 16);
		ThaumcraftApi.addWarpToItem(new ItemStack(CCPM.advThaum), 16);
		
		ThaumcraftApi.registerObjectTag(new ItemStack(CCPM.advThaum), al);
		
		
		ResearchItem ccpbUsing = new ResearchItem("CCPMCCPB", CATID, new AspectList().add(Aspect.CRAFT, 8).add(Aspect.FLUX, 16).add(Aspect.ENTROPY, 9).add(Aspect.METAL, 28), -3, -3, 3, new ItemStack(CCPM.pollutionBrick));
		
		ccpbUsing.setPages(new ResearchPage("ccpm.ccpb"));
		
		ccpbUsing.setSpecial();
		ccpbUsing.registerResearchItem();
		
		ThaumcraftApi.addWarpToResearch("CCPMCCPB", 1);
		
		GolemMaterial.register(new GolemMaterial("CCPB", new String[]{"CCPMCCPB"}, new ResourceLocation("ccpm:textures/entity/golem/mat_pollution.png"), new Color(82, 96, 65).getRGB(), 15, 7, 3, new ItemStack(CCPM.pollutionBricks), new ItemStack(CCPM.pistons), new EnumGolemTrait[]{EnumGolemTrait.BLASTPROOF, EnumGolemTrait.FIREPROOF, EnumGolemTrait.LIGHT}));
		
		Wands.init();
		
		ResearchItem ccpbWand = new ResearchItem("ROD_CCPB", CATID, new AspectList().add(Aspect.FLUX, 16).add(Aspect.AURA, 9).add(Aspect.ENERGY, 28), -6, -6, 3, new ItemStack(CCPM.miscIngredient,1,4));
		
		ccpbWand.setParents("CCPMCCPB");
		
		InfusionRecipe ccpbWandCrafting = ThaumcraftApi.addInfusionCraftingRecipe("ROD_CCPB", new ItemStack(CCPM.miscIngredient,1,4), 3, new AspectList().add(Aspect.FLUX, 6).add(Aspect.ENERGY, 64).add(Aspect.AURA, 32).add(Aspect.CRAFT, 80), pollutionBricks, new Object[]{"materialCCPB", "blockCCPB", "dustCCPB", "tinyDustCCPB", "nuggetCCPB", new ItemStack(ItemsTC.salisMundus)});
		
		
		ccpbWand.setPages(new ResearchPage("ccpm.ccpb.wand"), new ResearchPage(ccpbWandCrafting));
		
		ccpbWand.registerResearchItem();
		
ResearchItem ccpbWandInv = new ResearchItem("ROD_CCPB_INVERTED", CATID, new AspectList().add(Aspect.FLUX, 16).add(Aspect.AURA, 9).add(Aspect.ENERGY, 28).add(Aspect.EXCHANGE, 16), -8, -4, 3, new ItemStack(CCPM.miscIngredient,1,5));
		
	ccpbWandInv.setParents("ROD_CCPB");

		InfusionRecipe ccpbWandInvCrafting = ThaumcraftApi.addInfusionCraftingRecipe("ROD_CCPB", new ItemStack(CCPM.miscIngredient,1,5), 4, new AspectList().add(Aspect.EXCHANGE, 64).add(Aspect.ENERGY, 64), new ItemStack(CCPM.miscIngredient,1,4), new Object[]{"materialCCPB", "blockCCPB", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.bucketPure), new ItemStack(CCPM.filter)});
		
		
		ccpbWandInv.setPages(new ResearchPage("ccpm.ccpb.wand.inv"), new ResearchPage(ccpbWandInvCrafting));
		
		ccpbWandInv.registerResearchItem();
		
		
		ResearchItem ccpbStaff = new ResearchItem("ROD_STAFF_CCPB", CATID, new AspectList().add(Aspect.FLUX, 16).add(Aspect.AURA, 9).add(Aspect.ENERGY, 28), -8, 8, 3, new ItemStack(CCPM.miscIngredient,1,6));
		
		ccpbStaff.setParents("ROD_CCPB");
		
		InfusionRecipe ccpbStaffCrafting = ThaumcraftApi.addInfusionCraftingRecipe("ROD_STAFF_CCPB", new ItemStack(CCPM.miscIngredient,1,6), 5, new AspectList().add(Aspect.ENERGY, 128).add(Aspect.AURA, 32), pollutionBricks, new Object[]{new ItemStack(CCPM.miscIngredient,1,4), new ItemStack(CCPM.miscIngredient,1,4)});
		
		
		ccpbStaff.setPages(new ResearchPage("ccpm.ccpb.staff"), new ResearchPage(ccpbStaffCrafting));
		
		ccpbStaff.registerResearchItem();
		
		
		ResearchItem ccpbStaffInv = new ResearchItem("ROD_STAFF_CCPB_INVERTED", CATID, new AspectList().add(Aspect.FLUX, 16).add(Aspect.AURA, 9).add(Aspect.ENERGY, 28), -6, 8, 3, new ItemStack(CCPM.miscIngredient,1,7));
		
		ccpbStaffInv.setParents("ROD_CCPB_INVERTED", "ROD_STAFF_CCPB");
		
		InfusionRecipe ccpbStaffInvCrafting = ThaumcraftApi.addInfusionCraftingRecipe("ROD_STAFF_CCPB_INVERTED", new ItemStack(CCPM.miscIngredient,1,7), 6, new AspectList().add(Aspect.ENERGY, 256).add(Aspect.AURA, 64), pollutionBricks, new Object[]{new ItemStack(CCPM.miscIngredient,1,5), new ItemStack(CCPM.miscIngredient,1,5)});
		
		
		ccpbStaffInv.setPages(new ResearchPage("ccpm.ccpb.staff.inv"), new ResearchPage(ccpbStaffInvCrafting));
		
		ccpbStaffInv.registerResearchItem();
		
		
		ResearchItem ccpbCap = new ResearchItem("CAP_CCPB", CATID, new AspectList().add(Aspect.AURA, 8).add(Aspect.FLUX, 16).add(Aspect.ENTROPY, 9).add(Aspect.ENERGY, 16), -5, -6, 3, new ItemStack(CCPM.miscIngredient,1,3));
		
		ccpbCap.setParents("CCPMCCPB");
		
		InfusionRecipe capRec = ThaumcraftApi.addInfusionCraftingRecipe("CAP_CCPB", new ItemStack(CCPM.miscIngredient,1,3), 3, new AspectList().add(Aspect.ENERGY, 64).add(Aspect.AURA, 16).add(Aspect.FLUX, 16), pollutionBricks, new Object[]{"materialCCPB", "nuggetCCPB","materialCCPB", "nuggetCCPB","materialCCPB", "dustCCPB", new ItemStack(ItemsTC.salisMundus),new ItemStack(ItemsTC.salisMundus),new ItemStack(ItemsTC.salisMundus),new ItemStack(ItemsTC.salisMundus)});
		
		ccpbCap.setPages(new ResearchPage("ccpm.ccpb.cap"), new ResearchPage(capRec));
		
		ccpbCap.registerResearchItem();
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
		
		OreDictionary.registerOre("materialCCPB", CCPM.pollutionBrick);
		
		OreDictionary.registerOre("blockCCPB", CCPM.pollutionBricks);
		
		OreDictionary.registerOre("dustCCPB", new ItemStack(CCPM.miscIngredient,1,0));
		
		OreDictionary.registerOre("tinyDustCCPB", new ItemStack(CCPM.miscIngredient,1,1));
		
		OreDictionary.registerOre("dustTinyCCPB", new ItemStack(CCPM.miscIngredient,1,1));
		
		OreDictionary.registerOre("nuggetCCPB", new ItemStack(CCPM.miscIngredient,1,2));
	}
	
	public static void setupChests()
	{
		
	}
	
}
