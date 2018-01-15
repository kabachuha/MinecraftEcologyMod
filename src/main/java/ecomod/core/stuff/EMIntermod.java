package ecomod.core.stuff;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.tiles.IHasWork;
import com.google.common.collect.ImmutableList;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.blocks.compat.BlockAnalyzerAdapter;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.pollution.TEPollutionConfig.TEPollution;
import ecomod.common.pollution.handlers.IC2Handler;
import ecomod.common.tiles.compat.TileAnalyzerAdapter;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;
import java.util.function.Function;

public class EMIntermod
{
	@net.minecraftforge.common.capabilities.CapabilityInject(IHasWork.class)
	public static final Capability<IHasWork> CAP_HAS_WORK = null;
	
	public static final org.apache.logging.log4j.Logger log = LogManager.getLogger("Ecomod|Intermod");
	
	public static void registerBCFuels()
	{
		if(ModAPIManager.INSTANCE.hasAPI("BuildCraftAPI|fuels"))
		{
			log.info("Adding to BuildCraft fuels");
			if(BuildcraftFuelRegistry.fuel != null)
			{
				BuildcraftFuelRegistry.fuel.addFuel(EcomodStuff.concentrated_pollution, EMConfig.fuel_concentrated_pollution_burn_energy, EMConfig.fuel_concentrated_pollution_burn_time);
			}
		}
	}
	
	public static void OCpreInit()
	{
		log.info("Setting OpenComputers support");
		EcomodBlocks.OC_ANALYZER_ADAPTER = new BlockAnalyzerAdapter().setUnlocalizedName(EMConsts.modid + ".oc_analyzer_adapter");
		EMBlocks.regBlock(EcomodBlocks.OC_ANALYZER_ADAPTER, "analyzer_adapter");
		GameRegistry.registerTileEntity(TileAnalyzerAdapter.class, EMUtils.resloc("analyzer_adapter").toString());
	}
	
	public static final String key_add_tepc = "add_to_tepc";
	public static final String key_remove_tepc = "remove_from_tepc";
	public static final String blacklist_dropped_item = "blacklist_dropped_item";
	public static final String key_add_te_pollution_determinant = "add_te_pollution_determinant";
	
	public static void processIMC(ImmutableList<IMCMessage> messages)
	{
		log.info("Processing Intermod Commits");
		TEPollutionConfig tepc = EcologyMod.instance.tepc;
		
		for(IMCMessage m : messages)
		{
			log.info("Processing "+m.getSender() + '#' +m.key+"->"+(m.isStringMessage() ? m.getStringValue() : m.isNBTMessage() ? m.getNBTValue().toString() : m.isItemStackMessage() ? m.getItemStackValue().toString() : m.isResourceLocationMessage() ? m.getResourceLocationValue().toString() : "....."));
			String lowerKey = m.key.toLowerCase();
			if(lowerKey.contentEquals(key_add_tepc))
			{
				TEPollution tep = null;
				
				String id = "";
				double air = 0.0D;
				double water = 0.0D;
				double soil = 0.0D;
				
				if(m.isNBTMessage())
				{
					NBTTagCompound nbt = m.getNBTValue();
					
					if(nbt.hasKey("id"))
						id = nbt.getString("id");
					
					if(nbt.hasKey("air"))
						air = nbt.getDouble("air");
					
					if(nbt.hasKey("water"))
						water = nbt.getDouble("water");
					
					if(nbt.hasKey("soil"))
						soil = nbt.getDouble("soil");
				}
				else if (m.isStringMessage())
				{
					String val[];
					
					if(m.getStringValue().lastIndexOf(';') == -1 || (val = m.getStringValue().split(";")).length != 4)
					{
						log.info("Unable to add "+m.getStringValue()+" because of invalid format. The message value has to be splitted by semicolons into 4 parts ('id', 'air', 'water', 'soil')");
						continue;
					}
					
					id = val[0];
					
					air = Double.parseDouble(val[1]);
					water = Double.parseDouble(val[2]);
					soil = Double.parseDouble(val[3]);
				}
				
				if(id.length() > 0 && !(air == 0 && water == 0 && soil == 0))
				{
					tep = new TEPollution(id, new PollutionData((float)air, (float)water, (float)soil));
				}
				
				if(tep != null)
				{
					if(tepc.hasTile(new ResourceLocation(tep.getId())))
					{
						log.warn(tep.getId()+" is already in TEPC. Thus replacing the previous("+tepc.getTEP(tep.getId()).toString()+").");
						tepc.data.remove(tepc.getTEP(tep.getId()));
					}
					
					log.info("Added to TEPC: "+tep.toString());
					tepc.data.add(tep);
				}
			}
			
			if(lowerKey.contentEquals(key_remove_tepc) && m.isStringMessage())
			{
				if(tepc.hasTile(new ResourceLocation(m.getStringValue())))
				{
					log.info("Removing "+tepc.getTEP(m.getStringValue()).toString()+" from TEPC.");
					tepc.data.remove(tepc.getTEP(m.getStringValue()));
				}
			}
			
			if(lowerKey.contentEquals(blacklist_dropped_item))
			{
				String item_string = "";
				
				if(m.isItemStackMessage())
				{
					String str = m.getItemStackValue().getItem().getRegistryName().toString();
					if(m.getItemStackValue().getMetadata() != OreDictionary.WILDCARD_VALUE && m.getItemStackValue().getMetadata() != 0)
					{
						str += ":"+m.getItemStackValue().getMetadata();
					}
					
					item_string = str;
				}
				else if(m.isNBTMessage())
				{
					String str = "";
					
					if(m.getNBTValue().hasKey("id"))
					{
						str = m.getNBTValue().getString("id");
						
						if(m.getNBTValue().hasKey("meta"))
						{
							str += "@"+m.getNBTValue().getInteger("meta");
						}
					}
					
					item_string = str;
				}
				else if(m.isStringMessage())
				{
					item_string = m.getStringValue();
				}
				
				if(item_string.equals(""))
				{
					log.error("Unable to Blacklist Polluting On Expire Item.");
				}
				else
				{
					log.info("Blacklisting Polluting On Expire Item: "+item_string);
					EcomodStuff.blacklisted_items.add(item_string);
				}
			}
			
			if(lowerKey.contentEquals(key_add_te_pollution_determinant))
			{
				Optional<Function<TileEntity, Object[]>> op_func = m.getFunctionValue(TileEntity.class, Object[].class);
					
				if(!op_func.isPresent())
				{
					log.error("The TE pollution determining function sent by "+m.getSender()+" is invalid!");
				}
				else
				{
					log.info("Adding custom TileEntity pollution determining function!");
					EcomodStuff.custom_te_pollution_determinants.add(op_func.get());
				}
			}
		}
	}
	
	public static void thermal_expansion_imc()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("fluidName", EcomodStuff.concentrated_pollution.getName());
		nbt.setInteger("energy", EMConfig.fuel_concentrated_pollution_burn_energy);
		FMLInterModComms.sendMessage("thermalexpansion", "addcompressionfuel", nbt);
	}
	
	public static void setup_ic2_support()
	{
		log.info("Setuping IC2 support");
		if(EMConfig.isConcentratedPollutionIC2Fuel)
		{
			ic2.api.recipe.Recipes.fluidHeatGenerator.addFluid(EcomodStuff.concentrated_pollution.getName(), 40, EMConfig.fuel_concentrated_pollution_burn_energy / 15);
			ic2.api.recipe.Recipes.fluidHeatGenerator.getAcceptedFluids().add(EcomodStuff.concentrated_pollution);
			ic2.api.recipe.Recipes.semiFluidGenerator.addFluid(EcomodStuff.concentrated_pollution.getName(), 40, EMConfig.fuel_concentrated_pollution_burn_energy / 30);
			ic2.api.recipe.Recipes.semiFluidGenerator.getAcceptedFluids().add(EcomodStuff.concentrated_pollution);
		}
	}
	
	public static void init_ic2_support()
	{
		MinecraftForge.EVENT_BUS.register(new IC2Handler());
	}
	
	@net.minecraftforge.common.capabilities.CapabilityInject(IHasWork.class)
	private static void onIHasWorkCapLoaded(Capability cap)
	{
		if(cap != null)
			log.info("Capability<IHasWork> had been loaded!");
	}
}
