package ecomod.core.stuff;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import ecomod.common.commands.*;

public class EMCommands {

	public static void onServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandGetPollution());
		event.registerServerCommand(new CommandAddPollution());
		event.registerServerCommand(new CommandClearManager());
		event.registerServerCommand(new CommandLoadManager());
		event.registerServerCommand(new CommandSaveManager());
		event.registerServerCommand(new CommandGetTileID());
		event.registerServerCommand(new CommandTEPC());
	}
}
