package ecomod.api.pollution;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IRespirator
{
	public boolean isRespirating(EntityLivingBase entity, ItemStack stack, boolean check_affects);
}
