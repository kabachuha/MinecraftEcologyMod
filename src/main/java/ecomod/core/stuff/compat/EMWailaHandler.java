package ecomod.core.stuff.compat;

import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.config.TEPollutionConfig;
import ecomod.common.tiles.TileAdvancedFilter;
import ecomod.common.tiles.TileEnergy;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EMWailaHandler implements IWailaDataProvider
{
	@Override
	@Nonnull
	public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileEntity tile = accessor.getTileEntity();

		if(tile != null)
		{
			boolean isCrouching = accessor.getPlayer().isSneaking();
			PollutionData data = null;
			
			if(EMConfig.waila_shows_pollution_info && EcologyMod.proxy.getClientHandler().waila_shows_pollution_info)
			{
				TEPollutionConfig.TEPollution pollution = EcologyMod.proxy.getClientHandler().client_tiles_pollution.getTEP(tile);
				if (pollution != null)
					data = pollution.getEmission();
			}
			
			if(tile instanceof TileEnergy)
				currentTip.add(I18n.format("gui.ecomod.text.energy") + ' ' + TextFormatting.YELLOW + ((TileEnergy)tile).getEnergyStored() + " / " + ((TileEnergy)tile).getMaxEnergyStored() + " RF");
			if(tile instanceof TileAdvancedFilter)
			{
				TileAdvancedFilter filter = (TileAdvancedFilter)tile;
				currentTip.add(I18n.format(EcomodStuff.concentrated_pollution.getUnlocalizedName()) + ": " + filter.tank.getFluidAmount() + " / " + filter.tank.getCapacity() + " mb");
				currentTip.add(filter.was_working ? TextFormatting.GREEN + I18n.format("tooltip.ecomod.waila.working") : TextFormatting.RED + I18n.format("tooltip.ecomod.waila.not_working"));
				if (filter.was_working) //If it is not working do not show it is reducing pollution
					data = filter.getSource();
			}
			if (data != null) {
				ArrayList<String> pollutionText = new ArrayList<>();
				float air = data.getAirPollution(), soil = data.getSoilPollution(), water = data.getWaterPollution();
				if(air <= -0.1D)
					pollutionText.add(TextFormatting.YELLOW + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.air") + TextFormatting.GREEN + ' ' + Float.toString(air));
				else if(air >= 0.1D)
					pollutionText.add(TextFormatting.YELLOW + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.air") + TextFormatting.RED + " +" + Float.toString(air));

				if(water <= -0.1D)
					pollutionText.add(TextFormatting.AQUA + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.water") + TextFormatting.GREEN + ' ' + Float.toString(water));
				else if(water >= 0.1D)
					pollutionText.add(TextFormatting.AQUA + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.water") + TextFormatting.RED + " +" + Float.toString(water));

				if(soil <= -0.1D)
					pollutionText.add(TextFormatting.GOLD + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.soil") + TextFormatting.GREEN + ' ' + Float.toString(soil));
				else if(soil >= 0.1D)
					pollutionText.add(TextFormatting.GOLD + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.soil") + TextFormatting.RED + " +" + Float.toString(soil));
				if (!pollutionText.isEmpty()) {
					if (isCrouching) {
						currentTip.add(I18n.format("gui.jei.ingredient.ecomod.pollution"));
						currentTip.addAll(pollutionText);
					} else
						currentTip.add(I18n.format("tooltip.ecomod.waila.pollution"));
				}
			}
		}

		return currentTip;
	}

	public static void callbackRegister(IWailaRegistrar registrar)
	{
		registrar.registerBodyProvider(new EMWailaHandler(), TileEntity.class);
	}
}
