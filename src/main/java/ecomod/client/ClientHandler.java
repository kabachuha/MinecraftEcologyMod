package ecomod.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.PollutionData;
import ecomod.common.blocks.BlockFrame;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.PollutionEffectsConfig.Effects;
import ecomod.common.pollution.PollutionManager.WorldPollution;
import ecomod.common.utils.EMUtils;
import ecomod.common.utils.Percentage;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import ecomod.network.EMPacketString;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//Client side handler!
public class ClientHandler
{
	public ChunkPollution[] cached_pollution;
	
	public Gson gson = new GsonBuilder().create();
	
	public Map<String, IAnalyzerPollutionEffect> pollution_effects = new HashMap<String, IAnalyzerPollutionEffect>();
	
	public Percentage smog_intensity = Percentage.ZERO;
	
	public Percentage required_smog_intensity = Percentage.ZERO;
	
	public boolean acid_rain = false;
	
	public boolean requestForNearbyPollution()
	{
		return false;
	}
	
	private List<ChunkPollution> polls = new ArrayList<ChunkPollution>();
	
	public PollutionData getLocalPollutionAtChunk(Pair<Integer, Integer> chunk_pos)
	{/*
		if(cached_pollution == null)
		{
			requestForNearbyPollution();
			return null;
		}
		
		for(ChunkPollution cp : cached_pollution)
		{
			if(cp.getLeft() == chunk_pos)
				return cp.getValue();
		}*/
		
		return null;
	}
	
	public void setSmog(String str)
	{
		if(str.length() < 1)
			return;
		
		if(str.equals("-0"))
		{
			smog_intensity = Percentage.ZERO;
			required_smog_intensity = Percentage.ZERO;
			return;
		}
		
		if(str.equals("-1"))
		{
			smog_intensity = Percentage.FULL;
			required_smog_intensity = Percentage.FULL;
			return;
		}
		
		try
		{
			boolean force = false;
			if(str.charAt(0) == '-')
			{
				force = true;
				str = str.substring(1);
			}
			
			int i = Integer.parseUnsignedInt(str);
			
			required_smog_intensity = new Percentage(i);
			
			if(force)
			{
				smog_intensity = new Percentage(i);
			}
		}
		catch (Exception ex)
		{
		}
	}
	
	public void setAcidRain(String str)
	{
		if(str.length() != 1)
			return;
		
		try
		{
			boolean b = Integer.parseInt(str) != 0;
		
			acid_rain = b;
		}
		catch (Exception ex)
		{
			acid_rain = false;
		}
	}
	
	public boolean setFromJson(String json)
	{/*
		polls.clear();
		
		if(json.length() < 3)
			return false;
		
		json = "OQdataQk["+json+"]C";
		
		json = json.replaceAll("pn", "QpollutionQkOQairQk0.0,QwaterQk0.0,QsoilQk0.0C");
		
		json = json.replaceAll("N", "chunkX");
		
		json = json.replaceAll("M", "chunkZ");
		
		json = json.replace('Q', '\"');
		
		json = json.replace('k', ':');
		
		json = json.replace('O', '{');
		
		json = json.replace('C', '}');
		
		try
		{
			for(ChunkPollution p : gson.fromJson(json, WorldPollution.class).getData())
				polls.add(p);
		}
		catch (JsonSyntaxException e)
		{
			EcologyMod.log.warn(e.toString());
			EcologyMod.log.warn(json);
		}
		
		if(!polls.isEmpty())
		{
			if(EMConfig.check_client_pollution)
			{
				if(EMUtils.isSquareChunkPollution(polls))
				{
					cached_pollution = polls.toArray(new ChunkPollution[polls.size()]);
				}
				else
				{
					EcologyMod.log.error("Pollution data has been received by client but it is invalid! Retrying...");
					requestForNearbyPollution();
				}
			}
			else
			{
				cached_pollution = polls.toArray(new ChunkPollution[polls.size()]);
			}
			polls.clear();
		}*/
		return false;
	}
	
	public void setEffects(String str)
	{
		pollution_effects.clear();
		Effects t = null;
		EcologyMod.log.info("Receiving Pollution Effects Config from the server...");
		try
		{
			t = gson.fromJson(str, Effects.class);
		}
		catch(JsonSyntaxException e)
		{
			EcologyMod.log.error("Unable to parse Pollution Effects cache received from the server!!!");
			return;
		}
		
		if(t != null)
		{
			for(IAnalyzerPollutionEffect iape : t.getEffects())
			{
				pollution_effects.put(iape.getId(), iape);
			}
			EcologyMod.log.info("Pollution Effects Config has been received.");
		}
	}
	
	public void setBiome(String str)
	{
		if(str.isEmpty())
			return;
		
		if(str.indexOf(";") == -1)
			return;
		
		String args[] = str.split(";");
		
		if(args.length != 3)
			return;
		
		int x = Integer.parseInt(args[0]);
		int z = Integer.parseInt(args[1]);
		
		int id = Integer.parseInt(args[2]);
		
		World w = Minecraft.getMinecraft().world;
		
		Chunk chunk = w.getChunkFromBlockCoords(new BlockPos(x,w.getActualHeight(),z));
		
		byte[] biome = chunk.getBiomeArray();
		int cbiome = biome[(z & 0xf) << 4 | x & 0xf];
		cbiome = id & 0xff;
		biome[(z & 0xf) << 4 | x & 0xf] = (byte) cbiome;
		
		chunk.setBiomeArray(biome);
		w.markBlocksDirtyVertical(x, z, 16, 16);
	}
	
	/**
	 * Client side EMPacketString handler
	 * 
	 * <br>
	 * By the first char:<br>
	 * P - Update cached_pollution<br>
	 * > - Set smog<br>
	 * * - Set biome<br>
	 * E - Update Effects Cache<br>
	 * ...<br>
	 * TODO add more cases<br>
	 * 
	 * 0 or '\0' - Do not handle<br>
	 * Other characters - Do not handle<br>
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void onStrEventReceived(EMPacketString.EventReceived event)
	{
		String str = event.getContent();
		//EcologyMod.log.info(str);
		char TYPE = str.charAt(0);
		
		if(str.length() >= 1)
			str = str.substring(1);
		
		switch(TYPE)
		{
			case 'P':
				setFromJson(str);
				break;
			case '>':
				setSmog(str);
				break;
			case 'R':
				setAcidRain(str);
				break;
			case '*':
				setBiome(str);
				break;
			case 'E':
				setEffects(str);
				break;
			case '#':
				GuiScreen.setClipboardString(str);
				break;
				
			case '0':
			case '\0'://So if the string is empty
			default:
				return;
		}
	}
	
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event)
	{
		
	}
	
	//Rendering handlers:
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogColor(EntityViewRenderEvent.FogColors event)
	{
		if(smog_intensity.compareTo(0) > 0 && !Minecraft.getMinecraft().player.isPotionActive(Potion.getPotionFromResourceLocation(new ResourceLocation("blindness").toString())))
		{
			Vec3d color = EMUtils.lerp(new Vec3d(event.getRed(), event.getGreen(), event.getBlue()), new Vec3d(66F/255, 80F/255, 67F/255), Math.min(smog_intensity.floatValue(), Minecraft.getMinecraft().world.isDaytime() ? 0.9F : 0.55F));
			
			event.setRed((float) color.x);
			event.setGreen((float) color.y);
			event.setBlue((float) color.z);
		}
	}
	
	private int lasttick = -1;
	
	private static final float s0 = 0.3F;
	private static final float e0 = 0.2F;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogRender(EntityViewRenderEvent.RenderFogEvent event)
	{
		if(smog_intensity.compareTo(0) > 0 && event.getFogMode() != GlStateManager.FogMode.EXP.capabilityId)
		{
			if(!Minecraft.getMinecraft().player.isPotionActive(Potion.getPotionFromResourceLocation(new ResourceLocation("blindness").toString())))
			{
				float f = event.getFarPlaneDistance();
				GlStateManager.setFogStart((float) (s0 + (f * 0.75 - s0) * (1 - smog_intensity.floatValue())));
				GlStateManager.setFogEnd((float) (e0 * f + (f - e0 * f) * (1 - Math.pow(smog_intensity.floatValue(), EMConfig.smog_rendering_distance_intensity_exponent))));
				GlStateManager.setFogDensity(smog_intensity.floatValue());
				event.setResult(Result.ALLOW);
			}
		}
		
		if((event.getEntity() != null && event.getEntity().ticksExisted % 2 == 0) && !Minecraft.getMinecraft().isGamePaused())
		{
			if(event.getEntity().ticksExisted != lasttick)
			{
				if(smog_intensity.compareTo(required_smog_intensity) < 0)
					smog_intensity = smog_intensity.add(2);
		
				if(smog_intensity.compareTo(required_smog_intensity) > 0)
					smog_intensity = smog_intensity.add(-2);
				
				lasttick = event.getEntity().ticksExisted;
			}
		}
	}
	
	public boolean client_isEffectActive(String id, PollutionData chunk_pd)
	{
		if(pollution_effects.containsKey(id))
		{
			IAnalyzerPollutionEffect iape = pollution_effects.get(id);
			
			if(iape != null)
			{
				if(iape.getTriggeringType() == TriggeringType.AND)
				{
					return chunk_pd.compareTo(iape.getTriggerringPollution()) >= 0;
				}
				else if(iape.getTriggeringType() == TriggeringType.OR)
				{
					return chunk_pd.compareOR(iape.getTriggerringPollution()) >= 0;
				}
			}
		}
		return false;
	}
	
	@SubscribeEvent
	public void itemTooltip(ItemTooltipEvent event)
	{
		if(event.getItemStack() != null)
		{
			if(EMConfig.is_oc_analyzer_interface_crafted_by_right_click)
			if(BlockFrame.oc_adapter != null)
				if(event.getItemStack().getItem() == BlockFrame.oc_adapter.getItem())
					if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
						event.getToolTip().add(I18n.format("tooltip.ecomod.oc.adapter", new Object[0]));
		}
	}
}
