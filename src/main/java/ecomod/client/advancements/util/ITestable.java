package ecomod.client.advancements.util;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ITestable {
	boolean test(EntityPlayerMP player, Object[] args);
}
