package ecomod.common.tiles;

import net.minecraft.tileentity.TileEntity;

public class TileFrame extends TileEntity
{
	public TileFrame()
	{
		//Empty TileEntity for rendering purposes
	}

	@Override
	public boolean canUpdate() {
		return false;
	}
}
