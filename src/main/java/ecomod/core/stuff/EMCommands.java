package ecomod.core.stuff;

import ecomod.common.commands.*;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class EMCommands {

	public static void onServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandGetPollution());
		event.registerServerCommand(new CommandAddPollution());
		event.registerServerCommand(new CommandClearManager());
		event.registerServerCommand(new CommandLoadManager());
		event.registerServerCommand(new CommandSaveManager());
		//event.registerServerCommand(new CommandUpdateCache());
		event.registerServerCommand(new CommandGetTileID());
		event.registerServerCommand(new CommandTEPC());
	}
}
