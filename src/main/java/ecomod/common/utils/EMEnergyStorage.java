package ecomod.common.utils;

import cofh.api.energy.IEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class EMEnergyStorage extends EnergyStorage implements IEnergyStorage {

	public EMEnergyStorage(int capacity, boolean in)
	{
		super(capacity, in ? capacity : 0,  in ? 0 : capacity);
	}

	public EMEnergyStorage setIn(boolean in)
	{
		this.maxReceive = in ? capacity : 0;
		this.maxExtract = in ? 0 : capacity;
		
		return this;
	}
	
	public void setEnergyStored(int energy) {

		this.energy = energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}
	
	public void modifyEnergyStored(int energy) {

		this.energy += energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		if (energy < 0) {
			energy = 0;
		}
		nbt.setInteger("Energy", energy);
		return nbt;
	}
	
	public EMEnergyStorage readFromNBT(NBTTagCompound nbt) {

		this.energy = nbt.getInteger("Energy");

		if (energy > capacity) {
			energy = capacity;
		}
		return this;
	}
	
	public int extractEnergyNotOfficially(int maxExtract, boolean simulate)
    {
        int energyExtracted = Math.min(energy, maxExtract);
        if (!simulate)
            energy -= energyExtracted;
        return energyExtracted;
    }
}
