package ecomod.api.pollution;

import com.google.gson.annotations.SerializedName;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PollutionData implements Comparable
{
	@SerializedName("air")
	private double air_pollution;
	
	@SerializedName("water")
	private double water_pollution;
	
	@SerializedName("soil")
	private double soil_pollution;
	
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
	
	public PollutionData(double air, double water, double soil)
	{
		air_pollution = air;
		water_pollution = water;
		soil_pollution = soil;
	}
	
	//Getters/Setters

	/**
	 * @return the air_pollution
	 */
	public double getAirPollution() {
		return air_pollution;
	}

	/**
	 * @param air_pollution the air_pollution to set
	 */
	public void setAirPollution(double air_pollution) {
		this.air_pollution = air_pollution;
	}

	/**
	 * @return the water_pollution
	 */
	public double getWaterPollution() {
		return water_pollution;
	}

	/**
	 * @param water_pollution the water_pollution to set
	 */
	public void setWaterPollution(double water_pollution) {
		this.water_pollution = water_pollution;
	}

	/**
	 * @return the soil_pollution
	 */
	public double getSoilPollution() {
		return soil_pollution;
	}

	/**
	 * @param soil_pollution the soil_pollution to set
	 */
	public void setSoilPollution(double soil_pollution) {
		this.soil_pollution = soil_pollution;
	}
	
	public double get(PollutionType type)
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
	
	public PollutionData set(PollutionType type, double toset)
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
	
	public PollutionData add(PollutionType type, double amount)
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
	
	public PollutionData addAll(double amount)
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
		air_pollution /= d;
		water_pollution /= d;
		soil_pollution /= d;
		return this;
	}
	
	public PollutionData divide(PollutionType type, float d)
	{
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
		return new PollutionData(0.0D,0.0D,0.0D);
	}
	
	public PollutionData clone()
	{
		return new PollutionData(air_pollution, water_pollution, soil_pollution);
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
			bb.writeDouble(get(t));
	}
	
	public static PollutionData fromByteBuf(ByteBuf bb)
	{
		PollutionData ret = getEmpty();
		
		for(PollutionType t : PollutionType.values())
			ret.set(t, bb.readDouble());
		
		return ret;
	}
	
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
				setAirPollution(nbt.getDouble("air"));
			if(nbt.hasKey("water"))
				setWaterPollution(nbt.getDouble("water"));
			if(nbt.hasKey("soil"))
				setSoilPollution(nbt.getDouble("soil"));
		}
	}
}
