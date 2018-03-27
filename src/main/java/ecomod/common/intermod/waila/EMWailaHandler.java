package ecomod.common.intermod.waila;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;

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
			
			if(tile instanceof TileAdvancedFilter)
			{
				TileAdvancedFilter filter = (TileAdvancedFilter)tile;
				currentTip.add(I18n.format(EcomodStuff.concentrated_pollution.getUnlocalizedName()) + ": " + filter.tank.getFluidAmount() + " / " + filter.tank.getCapacity() + " mb");
				currentTip.add(filter.was_working ? EnumChatFormatting.GREEN + I18n.format("tooltip.ecomod.waila.working") : EnumChatFormatting.RED + I18n.format("tooltip.ecomod.waila.not_working"));
				if (filter.was_working) //If it is not working do not show it is reducing pollution
					data = filter.getSource();
			}
			if (data != null) {
				ArrayList<String> pollutionText = new ArrayList<String>();
				float air = data.getAirPollution(), soil = data.getSoilPollution(), water = data.getWaterPollution();
				if(air <= -0.1D)
					pollutionText.add(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.air") + EnumChatFormatting.GREEN + ' ' + Float.toString(air));
				else if(air >= 0.1D)
					pollutionText.add(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.air") + EnumChatFormatting.RED + " +" + Float.toString(air));

				if(water <= -0.1D)
					pollutionText.add(EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.water") + EnumChatFormatting.GREEN + ' ' + Float.toString(water));
				else if(water >= 0.1D)
					pollutionText.add(EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.water") + EnumChatFormatting.RED + " +" + Float.toString(water));

				if(soil <= -0.1D)
					pollutionText.add(EnumChatFormatting.GOLD + "" + EnumChatFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.soil") + EnumChatFormatting.GREEN + ' ' + Float.toString(soil));
				else if(soil >= 0.1D)
					pollutionText.add(EnumChatFormatting.GOLD + "" + EnumChatFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.soil") + EnumChatFormatting.RED + " +" + Float.toString(soil));
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

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP arg0, TileEntity arg1, NBTTagCompound arg2, World arg3, int arg4,
			int arg5, int arg6) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack arg0, List<String> arg1, IWailaDataAccessor arg2,
			IWailaConfigHandler arg3) {
		return null;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor arg0, IWailaConfigHandler arg1) {
		return null;
	}

	@Override
	public List<String> getWailaTail(ItemStack arg0, List<String> arg1, IWailaDataAccessor arg2,
			IWailaConfigHandler arg3) {
		return null;
	}
}