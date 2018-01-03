package ecomod.core.stuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import ecomod.api.EcomodStuff;
import ecomod.core.EMConsts;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.fluids.FluidRegistry;

public class EMAchievements
{
	public static AchievementPage ECOMOD_ACHIEVEMENTS = null;
	public static Map<String, Achievement> ACHS = new HashMap<String, Achievement>();
	
	public static void setup()
	{
		ACHS.clear();
		
		Achievement filter_core = ach("filter_core", 4, 2, new ItemStack(EcomodItems.CORE, 1, 0), null);
		ach("analyzer_core", 5, 0, new ItemStack(EcomodItems.CORE,1,2), filter_core);
		ach("advanced_core", 3, 0, new ItemStack(EcomodItems.CORE,1,1), filter_core);
		ach("respirator", 2, 2, new ItemStack(EcomodItems.RESPIRATOR), filter_core);
		
		Achievement frame_basic = ach("basic_frame", -3, 2, new ItemStack(EcomodBlocks.FRAME, 1, 0), null);
		ach("advanced_frame", -3, 0, new ItemStack(EcomodBlocks.FRAME, 1, 1), frame_basic).setSpecial();
		
		ach("poisonous_sleep", -3, -2, new ItemStack(Items.bed),
				ach("bad_sleep", -3, -3, new ItemStack(Items.bed), null)
				).setSpecial();
		ach("smog", -2, -3, new ItemStack(EcomodItems.RESPIRATOR), null);
		//ach("concentrated_pollution", -2, -2, new ItemStack(EcomodBlocks.FLUID_POLLUTION), null).setSpecial();
		ach("very_polluted_food", -1, -2, new ItemStack(Items.poisonous_potato),
				ach("polluted_food", -1, -3, new ItemStack(Items.poisonous_potato), null)
				).setSpecial();
		ach("acid_rain", 0, -3, null, null);
		ach("no_fish", 1, -3, new ItemStack(Items.fish), null);
		ach("no_bonemeal", 2, -3, new ItemStack(Items.dye, 1, 15), null);
		
		ECOMOD_ACHIEVEMENTS = new AchievementPage(EMConsts.name, ACHS.values().toArray(new Achievement[ACHS.size()]));
		AchievementPage.registerAchievementPage(ECOMOD_ACHIEVEMENTS);
	}
	
	private static Achievement ach(String name, int x, int y, ItemStack item, @Nullable Achievement parent)
	{
		Achievement a = new Achievement(EMConsts.modid+"."+name, EMConsts.modid+"."+name, x, y, item, parent);
		
		if(!ACHS.containsKey(name))
		{
			ACHS.put(name, a);
			a.registerStat();
		}
		return a;
	}
}
