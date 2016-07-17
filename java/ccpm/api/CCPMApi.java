package ccpm.api;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public abstract class CCPMApi {

	public static ToolMaterial pollMaterial = EnumHelper.addToolMaterial("ccpm.pollution", 3, 750, 2.5F, 7, 22);
	
	private CCPMApi(){};

	public static DamageSource damageSourcePollution = new DamageSource("pollution").setDamageBypassesArmor();
}
