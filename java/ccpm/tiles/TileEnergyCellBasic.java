package ccpm.tiles;

import ccpm.api.ICCPMEnergySource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.*;

@Optional.InterfaceList({
    @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers"),
    @Optional.Interface(iface = "li.cil.oc.api.network.ManagedPeripheral", modid = "OpenComputers")
})
public class TileEnergyCellBasic extends TileEntity implements ICCPMEnergySource/*, SimpleComponent, ManagedPeripheral*/, ITickable {

	int energy = 0;
	public String name;
	static int maxEnergy = 1000000;
	
	public TileEnergyCellBasic(String name, int maxEnergy) {
		super();
		this.name=name;
		this.maxEnergy=maxEnergy;
	}

//	@Override
	public String getComponentName() {
		return name;
	}

	@Override
	public int getEnergy() {
		
		return this.energy;
	}
	
	@Override
	public int getMaxEnergy() {
		
		return this.maxEnergy;
	}

	

	@Override
	public void setEnergy(int amount) {
		this.energy = amount;
	}

	//@Override
	@Optional.Method(modid = "OpenComputers")
	public String[] methods() {
		return new String[]{"getEnergy"};
	}

	/*
	@Override
	@Optional.Method(modid = "OpenComputers")
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		if(method.equals("getEnergy"))
		{
			return new Object[]{this.getEnergy()};
		}
		
		
		return null;
	}
*/

	@Override
	public boolean useEnergy(int amount, TileEntity user) {
		return false;
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbt)
	{
        super.readFromNBT(nbt);
        energy=nbt.getInteger("energy");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("energy", this.energy);
		return nbt;

    }
   // int sticks = -1;
	/*@Override
	public void update() {
		
		if(sticks <= 0)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			writeToNBT(nbt);
			MiscUtils.syncTileEntity(nbt, 0);
		}
		else
			--sticks;
		
	}
*/

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
}
