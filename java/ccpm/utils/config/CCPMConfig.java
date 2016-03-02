package ccpm.utils.config;

import DummyCore.Utils.IDummyConfig;
import net.minecraftforge.common.config.Configuration;
import scala.Int;

public class CCPMConfig implements IDummyConfig {

	public static int processingDelay = 60000;
	public static Configuration cfg;
	public static int noPlanting = 200000;
	//public static int smogId = 77;
	public static int smogPoll = 50000;
	public static int wasteId = 30;
	public static float filterRed = 100;
	public static boolean fstab = true;
	public static float waterPoll = 80000;
	public static int pollutionMultiplier = 1;
	//public static boolean useNode = true;
	//public static boolean useSano = false;
	
	public CCPMConfig()
	{
		
	}

	@Override
	public void load(Configuration config) {
		cfg = config;
		processingDelay = cfg.getInt("chunkProcessingDelay", "CORE", 60000, 0, Int.MaxValue(), "Delay(milliseconds) of the world handler processing thread");
        noPlanting = cfg.getInt("pollutionDieTrees", "POLLUTION", 200000, 0, Int.MaxValue(), "Pollution level to unable plantn trees outside");
        //smogId = cfg.getInt("potionSmogId", "POTIONS", 77, 0, Int.MaxValue(), "");
        smogPoll= cfg.getInt("pollutionSmog", "POLLUTION", 50000, 0, Int.MaxValue(), "");
        wasteId = cfg.getInt("wastelandId", "BIOMES", 30, 0, Int.MaxValue(), "Id of the wasteland biome");
        filterRed = cfg.getFloat("filterRedution", "MACHINES", 100, Float.MIN_VALUE, Float.MAX_VALUE, "Amount of the air filter's pollution redution");
        fstab = cfg.getBoolean("filterStabalise", "THAUMCRAFT", true, "Can machines stabalises infusion?");
        //useNode = cfg.getBoolean("useNode", "THAUMCRAFT", true, "Use jar node to craft the energy cell?");
       // useSano = cfg.getBoolean("useSano", "THAUMCRAFT", false, "Use the filter Sano aspect instead of Potentia to work?");
        waterPoll = cfg.getFloat("waterPollution", "POLLUTION", 80000, 0, Float.MAX_VALUE, "Amount of pollution then water starts to be polluted");
        pollutionMultiplier = cfg.getInt("pollutionMultiplier", "POLLUTION", 1, 1, Int.MaxValue(), "Multiplier of pollution prodution");
	}

}
