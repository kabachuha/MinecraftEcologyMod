package ecomod.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.PollutionEffectsConfig.Effects;
import ecomod.common.pollution.PollutionManager.WorldPollution;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import ecomod.network.EMPacketString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//Client side handler!
public class ClientHandler
{
	public ChunkPollution[] cached_pollution;
	
	public Gson gson = new GsonBuilder().create();
	
	public Map<String, IAnalyzerPollutionEffect> pollution_effects = new HashMap<String, IAnalyzerPollutionEffect>();
	
	public boolean smog = false;
	
	public boolean acid_rain = false;
	
	public boolean requestForNearbyPollution()
	{
		return false;
	}
	
	private List<ChunkPollution> polls = new ArrayList<ChunkPollution>();
	
	public PollutionData getLocalPollutionAtChunk(Pair<Integer, Integer> chunk_pos)
	{
		if(cached_pollution == null)
		{
			requestForNearbyPollution();
			return null;
		}
		
		for(ChunkPollution cp : cached_pollution)
		{
			if(cp.getLeft() == chunk_pos)
				return cp.getValue();
		}
		
		return null;
	}
	
	public void setSmog(String str)
	{
		if(str.length() != 1)
			return;
		
		try
		{
			boolean b = Integer.parseInt(str) != 0;
		
			smog = b;
		}
		catch (Exception ex)
		{
			smog = false;
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
	{
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
		}
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
		if(smog)
		{
			event.setRed(66F/255);
			event.setGreen(80F/255);
			event.setBlue(67F/255);
		}
	}
	
	private boolean b = false;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void fogRender(EntityViewRenderEvent.RenderFogEvent event)
	{
		if(smog)
		{
			GlStateManager.setFogDensity(1F);
			GlStateManager.setFogStart(0.3F); // 0.7
			GlStateManager.setFogEnd(event.getFarPlaneDistance()*0.2F);
			if(!b)
				b = true;
		}
		else
		{
			if(b)
			{
				GlStateManager.setFogDensity(1);
				b = false;
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
}
