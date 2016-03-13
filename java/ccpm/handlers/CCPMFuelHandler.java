package ccpm.handlers;

import ccpm.core.CCPM;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

public class CCPMFuelHandler implements IFuelHandler {

	public CCPMFuelHandler() {
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		if(fuel.getItem() == CCPM.pollutionBrick)
			return 14400;
		
		if(fuel.getItem() == Item.getItemFromBlock(CCPM.pollutionBricks))
			return 14400 * 9;
		
		return 0;
	}

}
