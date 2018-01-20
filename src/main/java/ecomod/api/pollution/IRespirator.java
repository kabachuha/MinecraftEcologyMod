package ecomod.api.pollution;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * An interface for smog-respirator items.
 */
public interface IRespirator
{
	/**
	 * Is this item respirating the polluted air?
	 * 
	 * By default, checked in the following situations: when the player is in a smog zone; when the player is in the placed concentrated pollution block; when the player sleeps within the polluted area.
	 * 
	 * @param entity the respirator wearer. *It can be not only player
	 * @param stack the respirator item.
	 * @param check_affects should the check affect the respirator(true) and/or its filters, or it's only being simulated(false)?
	 * @return whether the respirator filters the air.
	 */
    boolean isRespirating(EntityLivingBase entity, ItemStack stack, boolean check_affects);
}
