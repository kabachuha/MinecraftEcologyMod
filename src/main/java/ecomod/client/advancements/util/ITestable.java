package ecomod.client.advancements.util;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ITestable {
	public boolean test(EntityPlayerMP player, Object[] args);
}
