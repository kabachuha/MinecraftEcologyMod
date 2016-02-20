package ccpm.integration.thaumcraft;

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLLog;
import thaumcraft.api.aspects.Aspect;

public class TCUtils {
	static Method drainEssentia;
	/*
	Seems, thaumcraft api is outdated, let's use this function until thaum api update
	//TODO Remove that class then api update
	 * 
	 */
	public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, int ext) {
	    try {
	        if(drainEssentia == null) {
	            Class fake = Class.forName("thaumcraft.common.lib.events.EssentiaHandler");
	            drainEssentia = fake.getMethod("drainEssentia", TileEntity.class, Aspect.class, EnumFacing.class, int.class, int.class);
	        }
	        return (Boolean) drainEssentia.invoke(null, tile, aspect, direction, range, ext);
	    } catch(Exception ex) { 
	    	FMLLog.warning("[Thaumcraft API] Could not invoke thaumcraft.common.lib.events.EssentiaHandler method drainEssentia");
	    }
		return false;
	}

}
