package ecomod.api.capabilities;

import ecomod.api.EcomodStuff;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class PollutionProvider implements ICapabilitySerializable<NBTTagCompound> {

	@CapabilityInject(IPollution.class)
	private static final Capability<IPollution> POLLUTION_CAP = null;
	
	private IPollution instance = POLLUTION_CAP.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == POLLUTION_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == POLLUTION_CAP ? POLLUTION_CAP.<T> cast(this.instance) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) POLLUTION_CAP.getStorage().writeNBT(POLLUTION_CAP, instance, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		POLLUTION_CAP.getStorage().readNBT(POLLUTION_CAP, instance, null, nbt);
	}

}
