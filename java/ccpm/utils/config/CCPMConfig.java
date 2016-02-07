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
	}

}
