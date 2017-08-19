package ecomod.core.stuff;

import ecomod.common.commands.CommandAddPollution;
import ecomod.common.commands.CommandClearManager;
import ecomod.common.commands.CommandGetPollution;
import ecomod.common.commands.CommandGetTileID;
import ecomod.common.commands.CommandLoadManager;
import ecomod.common.commands.CommandSaveManager;
import ecomod.common.commands.CommandUpdateCache;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class EMCommands {

	public static void onServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandGetPollution());
		event.registerServerCommand(new CommandAddPollution());
		event.registerServerCommand(new CommandClearManager());
		event.registerServerCommand(new CommandLoadManager());
		event.registerServerCommand(new CommandSaveManager());
		event.registerServerCommand(new CommandUpdateCache());
		event.registerServerCommand(new CommandGetTileID());
	}
}
