package ccpm.tiles;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEnergyCellRf extends TileEnergyCellBasic implements IEnergyReceiver {

	public TileEnergyCellRf(String name, int maxEnergy) {
		super(name, maxEnergy);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
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
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		
		energy = Math.min(maxEnergy, energy+maxReceive);
		
		int free = this.maxEnergy-this.energy;
		if(free<=0)return 0;
		int free1 = free-maxReceive;
		int ret = free - free1;
		return ret;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return this.energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return this.maxEnergy;
	}

}
