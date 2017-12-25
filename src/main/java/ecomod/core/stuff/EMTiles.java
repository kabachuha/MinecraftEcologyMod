package ecomod.core.stuff;

import cpw.mods.fml.common.registry.GameRegistry;
import ecomod.common.tiles.*;
import ecomod.common.utils.EMUtils;

public class EMTiles
{
	public static void doPreInit()
	{
		GameRegistry.registerTileEntity(TileFilter.class, EMUtils.resloc("tile_filter").toString());
		GameRegistry.registerTileEntity(TileAdvancedFilter.class, EMUtils.resloc("tile_advanced_filter").toString());
		GameRegistry.registerTileEntity(TileAnalyzer.class, EMUtils.resloc("tile_analyzer").toString());
	}
}
