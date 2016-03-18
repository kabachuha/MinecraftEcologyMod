package ccpm.integration.thaumcraft;

import ccpm.utils.PollutionUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.wands.IWand;
import thaumcraft.api.wands.IWandRodOnUpdate;

public class WandRodPollutedInvertedOnUpdate implements IWandRodOnUpdate {

	AspectList al = new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.ORDER, 1);
	
	public WandRodPollutedInvertedOnUpdate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onUpdate(ItemStack itemstack, EntityPlayer player) {
		if(itemstack == null || player == null) return;
		
		if(player.ticksExisted % 5 == 0)
		{
			for(Aspect a : al.getAspects())
			{
				if(((IWand)itemstack.getItem()).getVis(itemstack, a) < ((IWand)itemstack.getItem()).getMaxVis(itemstack)/2)
				{
					if(PollutionUtils.getChunkPollution(player) >= 10)
					{
					PollutionUtils.increasePollution(-10, player.worldObj.getChunkFromBlockCoords(player.getPosition()));
					((IWand)itemstack.getItem()).addVis(itemstack, a, 1, true);
					}
				}
			}
		}

	}

}
