package ecomod.common.world;

import ecomod.common.utils.EMUtils;
import ecomod.core.stuff.EMConfig;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;

public class FluidPollution extends Fluid
{
	public FluidPollution()
	{
		super("concentrated_pollution", EMUtils.resloc("blocks/pollution_still"), EMConfig.enable_concentrated_pollution_flow_texture ? EMUtils.resloc("blocks/pollution_flow") : EMUtils.resloc("blocks/pollution_still"));
		
		this.setDensity(-1500);
		this.setGaseous(true);
		this.setLuminosity(8);
		this.setTemperature(355);
		this.setViscosity(800);
		
		this.setRarity(EnumRarity.RARE);
	}
}
