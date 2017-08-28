package ecomod.common.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import buildcraft.api.tiles.IHasWork;
import buildcraft.api.tiles.TilesAPI;
import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileAdvancedFilter extends TileEnergy implements ITickable, IHasWork
{
	public FluidTank tank;
	
	public int ticks = 0;
	
	public TileAdvancedFilter()
	{
		super(EMConfig.adv_filter_energy);
		
		tank = new AdvFilterTank(EMConfig.adv_filter_capacity);
		tank.setTileEntity(this);
	}

	@Override
	public boolean hasWork()
	{
		return isWorking();
	}
	
	private boolean was_working = false;
	private int i1 = 0;

	@Override
	public void update()
	{
		if(ticks > (20 * EMConfig.adv_filter_delay_secs) && (ticks - i1) > 340)
		{
			ticks = 0;
			i1 = 0;
		}
		
		if(ticks % (20 * EMConfig.adv_filter_delay_secs) == 0)
		{	
			if(world.isRemote)
			{
				
			}
			else
			{
				if(isWorking())
				{
					if(!was_working)
					{
						was_working = true;
						i1 = ticks;
					}
					
					if(energy.extractEnergy(EMConfig.advanced_filter_energy_per_second * EMConfig.adv_filter_delay_secs, false) == EMConfig.advanced_filter_energy_per_second * EMConfig.adv_filter_delay_secs)
					{
						EcomodAPI.emitPollution(getWorld(), getChunkCoords(), PollutionSourcesConfig.getSource("advanced_filter_redution"), false);
					
						tank.fillInternal(getProduction(), true);
					
						EMUtils.pushFluidAround(getWorld(), getPos(), tank);
					}
				}
				else
				{
					if(was_working)
					{
						was_working = false;
						i1 = 0;
					}
				}
			}
		}
		
		if(!world.isRemote)
				if(was_working)
				{
					if((ticks - i1) % (340) == 0)//17*20
						world.playSound(null, getPos(), EcomodStuff.advanced_filter_working, SoundCategory.BLOCKS, 2F, 1F);
				}
		
		++ticks;
	}
	
	public boolean isWorking()
	{
		boolean ret = true;
		
		if(!PollutionUtils.hasSurfaceAccess(getWorld(), getPos()))
		{
			return false;
		}
		
		ret &= world.isBlockPowered(getPos());
		
		if(ret)
		ret &= this.getEnergyStored() >= (EMConfig.advanced_filter_energy_per_second * EMConfig.adv_filter_delay_secs);
		
		if(ret)
		ret &= getProduction() != null;
		
		if(ret)
		ret &= tank.getCapacity() >= getProduction().amount + tank.getFluidAmount();
		
		return ret;
	}
	
	private FluidStack getProduction()
	{
		PollutionData pd = EcomodAPI.getPollution(getWorld(), this.getChunkCoords().getLeft(), this.getChunkCoords().getRight());
		
		if(pd == null || pd == PollutionData.getEmpty())
			return null;
		
		FluidStack ret = new FluidStack(EcomodStuff.concentrated_pollution, 0);
		
		PollutionData adv_filter_redution = PollutionSourcesConfig.getSource("advanced_filter_redution");
		
		for(PollutionType type : PollutionType.values())
		{
			if(pd.get(type) >= adv_filter_redution.get(type))
			{
				switch(type)
				{
					case AIR:
						ret.amount += adv_filter_redution.get(type);
						break;
						
					case WATER:
						ret.amount += adv_filter_redution.get(type) * 2;
						break;
						
					case SOIL:
						ret.amount += adv_filter_redution.get(type) * 4;
						break;
				}
			}
		}
		
		ret.amount = -ret.amount;
		
		if(ret.amount <= 0)
			return null;
		
		return ret;
	}
	
	
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
    }
	
	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag = super.writeToNBT(tag);
        tank.writeToNBT(tag);
        return tag;
    }
	
	@Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == TilesAPI.CAP_HAS_WORK || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) tank;
        if (capability == TilesAPI.CAP_HAS_WORK)
        	return (T) this;
        
        return super.getCapability(capability, facing);
    }
    
    private static class AdvFilterTank extends FluidTank
    {
    	public AdvFilterTank(int capacity)
        {
            super(capacity);
        }
    	
    	public boolean canFillFluidType(FluidStack fluid)
        {
            return canFill() && fluid.getFluid() == EcomodStuff.concentrated_pollution;
        }
    }
}
