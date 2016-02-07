package ccpm.tiles;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEnergyCellRf extends TileEnergyCellBasic implements IEnergyReceiver {

	public TileEnergyCellRf(String name, int maxEnergy) {
		super(name, maxEnergy);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public boolean useEnergy(int amount, TileEntity user) {
		if(amount > this.getEnergy())
		{
			return false;
		}
		
		this.setEnergy(this.getEnergy()-amount);
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		
		energy = Math.min(maxEnergy, energy+maxReceive);
		
		int free = this.maxEnergy-this.energy;
		if(free<=0)return 0;
		int free1 = free-maxReceive;
		int ret = free - free1;
		return ret;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return this.energy;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return this.maxEnergy;
	}

}
