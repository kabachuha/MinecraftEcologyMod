package ccpm.integration.thaumcraft;

import ccpm.utils.PollutionUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.wands.IWand;
import thaumcraft.api.wands.IWandRodOnUpdate;

public class WandRodPollutedOnUpdate implements IWandRodOnUpdate {

	AspectList al = new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.ENTROPY, 1);
	
	public WandRodPollutedOnUpdate() {
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
					PollutionUtils.increasePollution(10, player.worldObj.getChunkFromBlockCoords(player.getPosition()));
					if(player.worldObj.rand.nextInt(10)==1)
					AuraHelper.pollute(player.getEntityWorld(), player.getPosition().up(), 1, true);
					((IWand)itemstack.getItem()).addVis(itemstack, a, 1, true);
				}
			}
		}
	}

}
