package ecomod.api.pollution;

public class PollutionData
{
	private double air_pollution;
	
	private double water_pollution;
	
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
		return "{\"air_pollution\" : "+air_pollution+", \"water_pollution\" : "+water_pollution+", \"soil_pollution\" : "+soil_pollution+"}";
	}
	
	public static PollutionData getEmpty()
	{
		return new PollutionData(0,0,0);
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
}
