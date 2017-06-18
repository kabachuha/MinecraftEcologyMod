package ecomod.core.stuff;

import ecomod.common.tiles.TileFilter;
import ecomod.common.utils.EMUtils;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EMTiles
{
	public static void doPreInit()
	{
		GameRegistry.registerTileEntity(TileFilter.class, EMUtils.resloc("tile_filter").toString());
	}
}
