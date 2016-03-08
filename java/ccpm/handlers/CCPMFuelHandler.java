package ccpm.handlers;

import ccpm.core.CCPM;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

public class CCPMFuelHandler implements IFuelHandler {

	public CCPMFuelHandler() {
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		if(fuel.getItem() == CCPM.pollutionBrick)
			return 14400;
		
		return 0;
	}

}
