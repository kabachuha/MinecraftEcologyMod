package ccpm.api;

import net.minecraft.tileentity.TileEntity;

public interface ICCPMEnergySource {

	public int getEnergy();
	
	public int getMaxEnergy();
	
	public void setEnergy(int amount);
	
	public boolean useEnergy(int amount, TileEntity user);
}
