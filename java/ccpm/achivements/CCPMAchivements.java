package ccpm.achivements;

import java.util.Hashtable;

import ccpm.core.CCPM;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

public class CCPMAchivements {

	public static Hashtable<String, Achievement> achievementList = new Hashtable<String, Achievement>();
	
	public static void init()
	{
		if(!achievementList.isEmpty())achievementList.clear();
		
		Achievement resp = addAchievement(0,0,new ItemStack(CCPM.respirator),null,"ccpm.respirator", false);
		
		addAchLast(OffType.RIGHT, CCPM.an, "ccpm.analyser");
		addAchLast(OffType.UPRIGHT, CCPM.filter, "ccpm.filter");
		addAchLast(OffType.RIGHT, CCPM.baf, "ccpm.advfilter").setSpecial();
		addAchLast(OffType.RIGHT, CCPM.pistons, "ccpm.pistons");
		addAchLast(OffType.LEFT, CCPM.compressor,"ccpm.compressor");
		addAchLast(OffType.LEFT, CCPM.pollutionBrick,"ccpm.brick");
		Achievement bricks = addAchLast(OffType.UPLEFT, CCPM.pollutionBricks, "ccpm.bricks");
		addAchievement(bricks.displayRow-10, bricks.displayColumn+4, new ItemStack(CCPM.pollArmor[0]), bricks, "ccpm.helm", false);
		addAchievement(bricks.displayRow-10, bricks.displayColumn+2, new ItemStack(CCPM.pollArmor[1]), bricks, "ccpm.chest", false);
		addAchievement(bricks.displayRow-10, bricks.displayColumn-2, new ItemStack(CCPM.pollArmor[2]), bricks, "ccpm.legs", false);
		addAchievement(bricks.displayRow-10, bricks.displayColumn-4, new ItemStack(CCPM.pollArmor[3]), bricks, "ccpm.boots", false);
		
		addAch(OffType.UPLEFT, new ItemStack(CCPM.cell), resp, "ccpm.cell");
		
		if(Loader.isModLoaded("Thaumcraft"))
		{
			addAchLast(OffType.UP, new ItemStack(CCPM.cell,1,1), "ccpm.thaum");
			addAchLast(OffType.LEFT, new ItemStack(CCPM.advThaum), "ccpm.advthaum");
			ItemStack stk = new ItemStack(CCPM.respirator);
			stk.addEnchantment(Enchantment.protection, 1);
			addAch(OffType.RIGHT, stk, achievementList.get("ccpm.thaum"), "ccpm.rev");
		}
		
		Achievement ach[] = new Achievement[achievementList.values().toArray().length];
		
		for (int i = 0; i < achievementList.values().toArray().length; i++) {
			ach[i] = (Achievement) achievementList.values().toArray()[i];
			
		}
		
		AchievementPage.registerAchievementPage(new AchievementPage(CCPM.NAME, ach));
		
	}
	
	
	
	public static Achievement addAchievement(int x, int y,ItemStack display, Achievement parent, String name, boolean isSpecial)
	{
		Achievement beeingRegistered = new Achievement(name, name, x, y, display, parent);
		if(isSpecial)
			beeingRegistered.setSpecial();
		if(parent == null)
			beeingRegistered.initIndependentStat();
		beeingRegistered.registerStat();
		achievementList.put(name, beeingRegistered);
		return beeingRegistered;
	}
	
	public static Achievement addAch(OffType off, ItemStack disp, Achievement par, String name)
	{
		int x,y;
		switch(off)
		{
		case DOWN:
			x=par.displayRow;
			y=par.displayColumn-2;
			break;
		case DOWNLEFT:
			x=par.displayRow-2;
			y=par.displayColumn-2;
			break;
		case DOWNRIGHT:
			x=par.displayRow+2;
			y=par.displayColumn-2;
			break;
		case LEFT:
			x=par.displayRow-2;
			y=par.displayColumn;
			break;
		case RIGHT:
			x=par.displayRow+2;
			y=par.displayColumn;
			break;
		case UP:
			x=par.displayRow;
			y=par.displayColumn+2;
			break;
		case UPLEFT:
			x=par.displayRow-2;
			y=par.displayColumn+2;
			break;
		case UPRIGHT:
			x=par.displayRow+2;
			y=par.displayColumn+2;
			break;
		default:
			x=0;
			y=0;
			break;
			
		}
		
		if(par == null)
		{
			NullPointerException ex = new NullPointerException();
			
			FMLCommonHandler.instance().raiseException(ex, "Achievement's parent is null!", true);
		}
		
		Achievement beeingRegistered = new Achievement(name, name, x, y, disp, par);
		
		beeingRegistered.registerStat();
		achievementList.put(name, beeingRegistered);
		return beeingRegistered;
	}
	
	
	public static Achievement addAchLast(OffType off, ItemStack disp, String name)
	{
		return addAch(off,disp,(Achievement)achievementList.values().toArray()[achievementList.size()-1], name);
	}
	
	public static Achievement addAchLast(OffType off, Item disp, String name)
	{
		return addAch(off,new ItemStack(disp),(Achievement)achievementList.values().toArray()[achievementList.size()-1], name);
	}
	
	public static Achievement addAchLast(OffType off, Block disp, String name)
	{
		return addAch(off,new ItemStack(disp),(Achievement)achievementList.values().toArray()[achievementList.size()-1], name);
	}

}
