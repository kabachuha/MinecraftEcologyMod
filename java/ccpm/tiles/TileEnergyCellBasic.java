package ccpm.tiles;

import DummyCore.Utils.Coord3D;
import DummyCore.Utils.Lightning;
import DummyCore.Utils.MathUtils;
import ccpm.api.ICCPMEnergySource;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.*;

@Optional.InterfaceList({
    @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers"),
    @Optional.Interface(iface = "li.cil.oc.api.network.ManagedPeripheral", modid = "OpenComputers")
})
public class TileEnergyCellBasic extends TileEntity implements ICCPMEnergySource, SimpleComponent, ManagedPeripheral, ITickable {

	int energy = 0;
	public String name;
	static int maxEnergy = 1000000;
	
	public Lightning light1;
	public Lightning light2;
	public Lightning light3;
	public Lightning light4;
	
	public TileEnergyCellBasic(String name, int maxEnergy) {
		super();
		this.name=name;
		this.maxEnergy=maxEnergy;
	}

	@Override
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

	@Override
	@Optional.Method(modid = "OpenComputers")
	public String[] methods() {
		return new String[]{"getEnergy"};
	}

	
	@Override
	@Optional.Method(modid = "OpenComputers")
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		if(method.equals("getEnergy"))
		{
			return new Object[]{this.getEnergy()};
		}
		
		
		return null;
	}


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
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("energy", this.energy);

    }
    int ticks;
	@Override
	public void update() {
		ticks++;
		
		if((ticks % 20) == 0)
		{
			light1 = new Lightning(getWorld().rand, new Coord3D(0,0,0), new Coord3D(MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50), 0.3F, 1,0,0);
			light2 = new Lightning(getWorld().rand, new Coord3D(0,0,0), new Coord3D(MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50), 0.3F, 1,1,1);
			light3 = new Lightning(getWorld().rand, new Coord3D(0,0,0), new Coord3D(MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50), 0.3F, 0,1,0);
			light4 = new Lightning(getWorld().rand, new Coord3D(0,0,0), new Coord3D(MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50,MathUtils.randomDouble(getWorld().rand)/50), 0.3F, 0,0,1);
		}
		
	}

	
}
