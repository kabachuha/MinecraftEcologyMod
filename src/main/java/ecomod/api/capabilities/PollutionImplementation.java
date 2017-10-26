package ecomod.api.capabilities;

import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;

public class PollutionImplementation implements IPollution {

	PollutionData data;
	
	public PollutionImplementation()
	{
		data = new PollutionData();
	}
	
	public PollutionImplementation(PollutionData pollution)
	{
		data = pollution;
	}
	
	@Override
	public PollutionData getPollution() {
		return data;
	}

	@Override
	public void setPollution(PollutionData new_data) {
		for(PollutionType pt : PollutionType.values())
		{
			if(new_data.get(pt) < 0)
				new_data.set(pt, 0);
		}
		
		data = new_data;
	}

}
