package ecomod.common.world;

import java.awt.Color;

import ecomod.common.utils.EMUtils;
import ecomod.core.stuff.EMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.common.Optional;

public class FluidPollution extends Fluid implements buildcraft.api.fuels.IFuel
{
	public FluidPollution()
	{
		super("concentrated_pollution");
		
		//this.setFlowingIcon(EMConfig.enable_concentrated_pollution_flow_texture ? EMUtils.resloc("blocks/pollution_flow") : EMUtils.resloc("blocks/pollution_still"));
		
		this.setDensity(-1500);
		this.setGaseous(true);
		this.setLuminosity(8);
		this.setTemperature(355);
		this.setViscosity(800);
		
		this.setRarity(EnumRarity.rare);
	}

	@Override
	public int getColor()
	{
		return new Color(56, 83, 56).getRGB();
	}

	@Override
	public Fluid getFluid() {
		return this;
	}

	@Override
	public int getTotalBurningTime()
	{
		return EMConfig.fuel_concentrated_pollution_burn_time;
	}

	@Override
	public int getPowerPerCycle() {
		return EMConfig.fuel_concentrated_pollution_burn_energy;
	}
}
