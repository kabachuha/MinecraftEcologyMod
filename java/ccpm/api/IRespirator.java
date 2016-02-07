/**
 * 
 */
package ccpm.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author artem226
 *
 */
public interface IRespirator {
       
	public boolean isFiltering(EntityPlayer player, ItemStack respStack);
	
	public boolean renderHud();
}
