package ecomod.api.pollution;

import com.google.gson.annotations.SerializedName;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class PollutionData implements Comparable
{
	@SerializedName("air")
	private float air_pollution;
	
	@SerializedName("water")
	private float water_pollution;
	
	@SerializedName("soil")
	private float soil_pollution;
	
	public enum PollutionType
	{
		AIR,
		WATER,
		SOIL
	}
	
	public PollutionData()
	{
	}
	
	public PollutionData(PollutionData p)
	{
		air_pollution = p.getAirPollution();
		water_pollution = p.getWaterPollution();
		soil_pollution = p.getSoilPollution();
	}
	
	public PollutionData(float air, float water, float soil)
	{
		air_pollution = air;
		water_pollution = water;
		soil_pollution = soil;
	}
	
	//Getters/Setters

	/**
	 * @return the air_pollution
	 */
	public float getAirPollution() {
		return air_pollution;
	}

	/**
	 * @param air_pollution the air_pollution to set
	 */
	public void setAirPollution(float air_pollution) {
		this.air_pollution = air_pollution;
	}

	/**
	 * @return the water_pollution
	 */
	public float getWaterPollution() {
		return water_pollution;
	}

	/**
	 * @param water_pollution the water_pollution to set
	 */
	public void setWaterPollution(float water_pollution) {
		this.water_pollution = water_pollution;
	}

	/**
	 * @return the soil_pollution
	 */
	public float getSoilPollution() {
		return soil_pollution;
	}

	/**
	 * @param soil_pollution the soil_pollution to set
	 */
	public void setSoilPollution(float soil_pollution) {
		this.soil_pollution = soil_pollution;
	}
	
	public float get(PollutionType type)
	{
		switch(type)
		{
			case AIR:
				return getAirPollution();
			case WATER:
				return getWaterPollution();
			case SOIL:
				return getSoilPollution();
		}
		
		return 0;
	}
	
	public PollutionData set(PollutionType type, float toset)
	{
		switch(type)
		{
			case AIR:
				air_pollution = toset;
				break;
			case SOIL:
				soil_pollution = toset;
				break;
			case WATER:
				water_pollution = toset;
				break;
		}
		
		return this;
	}

	public PollutionData add(PollutionData d)
	{
		air_pollution += d.getAirPollution();
		water_pollution += d.getWaterPollution();
		soil_pollution += d.getSoilPollution();
		return this;
	}
	
	public PollutionData add(PollutionType type, float amount)
	{
		switch(type)
		{
			case AIR:
				air_pollution += amount;
				break;
			case SOIL:
				soil_pollution += amount;
				break;
			case WATER:
				water_pollution += amount;
				break;
		}
		return this;
	}
	
	public PollutionData addAll(float amount)
	{
		air_pollution += amount;
		water_pollution += amount;
		soil_pollution += amount;
		
		return this;
	}
	
	public PollutionData multiplyAll(float factor)
	{
		air_pollution *= factor;
		water_pollution *= factor;
		soil_pollution *= factor;
		return this;
	}
	
	public PollutionData multiplyAllRound(float factor)
	{
		air_pollution = Math.round(air_pollution * factor);
		water_pollution = Math.round(water_pollution * factor);
		soil_pollution = Math.round(soil_pollution * factor);
		return this;
	}
	
	public PollutionData multiply(PollutionType type, float factor)
	{
		switch(type)
		{
		case AIR:
			air_pollution *= factor;
			break;
		case SOIL:
			soil_pollution *= factor;
			break;
		case WATER:
			water_pollution *= factor;
			break;
		}
		return this;
	}
	
	public PollutionData divideAll(float d)
	{
		if(d == 0)
			return null;
		
		air_pollution /= d;
		water_pollution /= d;
		soil_pollution /= d;
		return this;
	}
	
	public PollutionData divide(PollutionType type, float d)
	{
		if(d == 0)
			return null;
		switch(type)
		{
		case AIR:
			air_pollution /= d;
			break;
		case SOIL:
			soil_pollution /= d;
			break;
		case WATER:
			water_pollution /= d;
			break;
		}
		return this;
	}
	
	@Override
	public String toString()
	{
		return "{\"air\" : "+air_pollution+", \"water\" : "+water_pollution+", \"soil\" : "+soil_pollution+"}";
	}
	
	public static PollutionData getEmpty()
	{
		return new PollutionData(0.0F,0.0F,0.0F);
	}
	
	public PollutionData clone()
	{
		return new PollutionData(air_pollution, water_pollution, soil_pollution);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(air_pollution);
		result = prime * result + Float.floatToIntBits(soil_pollution);
		result = prime * result + Float.floatToIntBits(water_pollution);
		return result;
	}

	public boolean equals(Object pd)
	{
		if(!(pd instanceof PollutionData))
			return false;
		
		PollutionData d = ((PollutionData)pd);
		
		return d.getAirPollution() == getAirPollution() && d.getWaterPollution() == getWaterPollution() && d.getSoilPollution() == getSoilPollution();
	}

	/**
	 * Compare AND (default)
	 */
	@Override
	public int compareTo(Object o)
	{
		PollutionData pd = ((PollutionData)o);
		
		if(air_pollution == pd.getAirPollution() && water_pollution == pd.getWaterPollution() && soil_pollution == pd.getSoilPollution())
			return 0;
		
		if(air_pollution >= pd.getAirPollution() && water_pollution >= pd.getWaterPollution() && soil_pollution >= pd.getSoilPollution())
			return 1;
		
		return -1;
	}
	
	/**
	 * Compare OR
	 * 
	 * @param pd
	 * @return @see {@link Comparable#compareTo(Object)}
	 */
	public int compareOR(PollutionData pd)
	{
		if(air_pollution == pd.getAirPollution() && water_pollution == pd.getWaterPollution() && soil_pollution == pd.getSoilPollution())
			return 0;
		
		if(air_pollution > pd.getAirPollution() || water_pollution > pd.getWaterPollution() || soil_pollution > pd.getSoilPollution())
			return 1;
		
		return -1;
	}
	
	public void writeByteBuf(ByteBuf bb)
	{
		for(PollutionType t : PollutionType.values())
			bb.writeFloat(get(t));
	}
	
	public static PollutionData fromByteBuf(ByteBuf bb)
	{
		PollutionData ret = getEmpty();
		
		for(PollutionType t : PollutionType.values())
			ret.set(t, bb.readFloat());
		
		return ret;
	}
	
	//Leave NBT as double to avoid version conflict
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setDouble("air", getAirPollution());
		nbt.setDouble("water", getWaterPollution());
		nbt.setDouble("soil", getSoilPollution());
		
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt != null)
		{
			if(nbt.hasKey("air"))
				setAirPollution((float)nbt.getDouble("air"));
			if(nbt.hasKey("water"))
				setWaterPollution((float)nbt.getDouble("water"));
			if(nbt.hasKey("soil"))
				setSoilPollution((float)nbt.getDouble("soil"));
		}
	}
	
	public boolean isEmpty()
	{
		return equals(getEmpty());
	}
}
