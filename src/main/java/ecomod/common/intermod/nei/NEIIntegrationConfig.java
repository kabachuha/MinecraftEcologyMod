package ecomod.common.intermod.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ecomod.core.stuff.EMIntermod;

public class NEIIntegrationConfig implements IConfigureNEI {

	@Override
	public void loadConfig()
	{
		EMIntermod.log.info("Initializing NEI plugin! "+getName());
		register(new NEIHandlerManuallyAssembly());
	}

	@Override
	public String getName()
	{
		return "EcologyMod|NEI";
	}

	@Override
	public String getVersion()
	{
		return "1.0.0";
	}

	private void register(TemplateRecipeHandler handler)
	{
		API.registerRecipeHandler(handler);
		API.registerUsageHandler(handler);
	}
}
