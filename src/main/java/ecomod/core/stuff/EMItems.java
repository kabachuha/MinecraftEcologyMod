package ecomod.core.stuff;

import ecomod.api.EcomodItems;
import ecomod.common.items.ItemCore;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EMItems 
{
	public static void doPreInit()
	{
		if(!EcomodItems.inited)
		{
			EcomodItems.inited = true;
			
			EcomodItems.CORE = new ItemCore().setUnlocalizedName(EMConsts.modid+".core");
			
			regItem(EcomodItems.CORE, "core", false, EMUtils.resloc("filter_core"), EMUtils.resloc("advanced_filter_core"), EMUtils.resloc("analyzer_core"));
		}
	}
	
	public static void doInit()
	{
		EcologyMod.proxy.registerItemModel(EcomodItems.CORE, 0, "filter_core");
		EcologyMod.proxy.registerItemModel(EcomodItems.CORE, 1, "advanced_filter_core");
		EcologyMod.proxy.registerItemModel(EcomodItems.CORE, 2, "analyzer_core");
	}
	
	public static void regItem(Item item, String name, boolean model, ResourceLocation... variants)
	{
		ResourceLocation resloc = EMUtils.resloc(name);
		
		item.setRegistryName(resloc);
		
		GameRegistry.register(item);
		
		if(variants != null && variants.length > 0)
			ModelBakery.registerItemVariants(item, variants);
		else
			ModelBakery.registerItemVariants(item, EMUtils.resloc(name));
		
		if(model)
		{
			EcologyMod.proxy.putItemToBeRegistred(item);
		}
	}
}
