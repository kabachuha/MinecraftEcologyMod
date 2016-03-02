package ccpm.tiles;

import DummyCore.Utils.MiscUtils;
import DummyCore.Utils.TileStatTracker;
import ccpm.api.ICCPMEnergySource;
import ccpm.fluids.CCPMFluids;
import ccpm.utils.PollutionUtils;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.crafting.IInfusionStabiliser;

public class AdvancedAirFilter extends TileEntity implements /*IInventory, */IInfusionStabiliser, IFluidHandler, ITickable {

	FluidTank tank = new FluidTank(CCPMFluids.concentratedPollution, 0, 1000);
	
	private TileStatTracker tracker;
	
	public AdvancedAirFilter() {
		super();
		tracker = new TileStatTracker(this);
		maxProgress = 60;
	}

	

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		
		return fluid == tank.getFluid().getFluid() && tank.getFluidAmount() > 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public boolean canStabaliseInfusion(World world, BlockPos pos) {
		
		return CCPMConfig.fstab;
	}

	public int progress = 0;
	
	public static int maxProgress = 1200;
	
	private int sticks = 0;
	
	int ticks = 0;
	
	@Override
	public void update()
	{
		++ticks;
		if(sticks == 0)
		{
			if(tracker!=null)
			if(!getWorld().isRemote && tracker.tileNeedsSyncing())
			{
				MiscUtils.sendPacketToAllAround(getWorld(), getDescriptionPacket(), getPos().getX(), getPos().getY(), getPos().getZ(), getWorld().provider.getDimensionId(), 32);
			}
			sticks=60;
		}
		else
			--sticks;
		
		
		if(ticks>=20)
			ticks = 0;
		
		if(isPowered())
		{
			if(!getWorld().isRemote)
			if(getWorld().provider.getDimensionId() == 0)
			{
				if(useEnergy())
				{
					PollutionUtils.increasePollution(-0.05F, getWorld().getChunkFromBlockCoords(getPos()));
					++progress;
					
					if(progress == maxProgress)
					{
						progress = 0;
						PollutionUtils.increasePollution(-60, getWorld().getChunkFromBlockCoords(getPos()));
						
						tank.fill(new FluidStack(CCPMFluids.concentratedPollution, 100), true);
					}
				}
			}
			else
			{
				getWorld().newExplosion(null, getPos().getX(), getPos().getY(), getPos().getZ(), 10, true, true);
			}
		}
	}

	private boolean isPowered()
	{
		return getWorld().isBlockIndirectlyGettingPowered(getPos()) > 0;
	}
	
	private boolean useEnergy()
	{
		BlockPos p = getPos().down();
		
		TileEntity te = getWorld().getTileEntity(p);
		
		if(te == null || te.isInvalid() || !te.hasWorldObj())
			return false;
		
		if(te instanceof ICCPMEnergySource){}else return false;
		
		return ((ICCPMEnergySource)te).useEnergy(100, this);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		tank.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		tank = tank.readFromNBT(nbt);
	}
	
	@Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.getPos(), -10, nbt);
    }
	
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
		if(net.getNetHandler() instanceof INetHandlerPlayClient)
			if(pkt.getTileEntityType() == -10)
				this.readFromNBT(pkt.getNbtCompound());
    }
}
