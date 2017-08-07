package ecomod.common.tiles;

import org.apache.commons.lang3.tuple.Pair;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import ecomod.common.utils.EMUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.*;



public class TileEnergy extends TileEntity implements IEnergyReceiver, IEnergyStorage
{
	EnergyStorage energy;
	
	public TileEnergy(int max_energy)
	{
		super();
		energy = new EnergyStorage(max_energy);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
			return true;
		
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
			return (T) energy;
		
		return super.getCapability(capability, facing);
	}

	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) 
	{
		return energy.receiveEnergy(maxReceive, simulate);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		energy = readEnergyFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		writeEnergyToNBT(nbt);
		return nbt;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return energy.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) 
	{
		return 0;
	}

	@Override
	public int getEnergyStored() 
	{
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() 
	{
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive()
	{
		return true;
	}
	
	public Pair<Integer, Integer> getChunkCoords()
	{
		return EMUtils.blockPosToPair(getPos());
	}
	
	public EnergyStorage readEnergyFromNBT(NBTTagCompound nbt) {

		this.energy.setEnergyStored(nbt.getInteger("Energy"));

		if (energy.getEnergyStored() > energy.getMaxEnergyStored()) {
			energy.setEnergyStored(energy.getMaxEnergyStored());
		}
		return energy;
	}

	public NBTTagCompound writeEnergyToNBT(NBTTagCompound nbt) {

		if (energy.getEnergyStored() < 0) {
			energy.setEnergyStored(0);
		}
		nbt.setInteger("Energy", energy.getEnergyStored());
		return nbt;
	}
}
