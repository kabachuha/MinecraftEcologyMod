package ccpm.items;

import ccpm.core.CCPM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class PWBucket extends ItemBucket{

	public PWBucket() {
		super(CCPM.pw);
		this.setUnlocalizedName("ccpm.buck.pw");
	}
}
