package ecomod.common.utils;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.newmc.EMBlockPos;

public class PositionedEmissionObject extends Pair<EMBlockPos, PollutionData>
{
	EMBlockPos position;
	PollutionData emission;
	
	public PositionedEmissionObject(int x, int y, int z, PollutionData emission)
	{
		this(new EMBlockPos(x, y, z), emission);
	}
	
	public PositionedEmissionObject(EMBlockPos pos, PollutionData emission)
	{
		position = pos;
		this.emission = emission;
	}
	
	@Override
	public PollutionData setValue(PollutionData arg0) {
		emission = arg0;
		return emission;
	}

	@Override
	public EMBlockPos getLeft()
	{
		return position;
	}

	@Override
	public PollutionData getRight()
	{
		return emission;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((emission == null) ? 0 : emission.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PositionedEmissionObject other = (PositionedEmissionObject) obj;
		if (emission == null) {
			if (other.emission != null)
				return false;
		} else if (!emission.equals(other.emission))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}
	
	public int getChunkX()
	{
		return position.getX() >> 4;
	}
	
	public int getChunkZ()
	{
		return position.getZ() >> 4;
	}
}