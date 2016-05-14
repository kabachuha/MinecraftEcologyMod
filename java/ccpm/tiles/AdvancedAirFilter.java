package ccpm.tiles;

import DummyCore.Utils.MiscUtils;
import DummyCore.Utils.TileStatTracker;
import buildcraft.api.tiles.IControllable;
import ccpm.api.ICCPMEnergySource;
import ccpm.api.IHasProgress;
import ccpm.fluids.CCPMFluids;
import ccpm.utils.PollutionUtils;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.FishingHooks.FishableCategory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.crafting.IInfusionStabiliser;

public class AdvancedAirFilter extends TileEntity implements IInventory, ISidedInventory, IInfusionStabiliser, IFluidHandler, ITickable, IHasProgress, IControllable{

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
	
	public static int maxProgress = 200;
	
	private int sticks = 0;
	
	int ticks = 0;
	
	Mode curMode = Mode.Unknown;
	
	@Override
	public void update()
	{
		++ticks;
		/*
		if(sticks == 0)
		{
			if(tracker!=null)
			if(!getWorld().isRemote && tracker.tileNeedsSyncing())
			{
				MiscUtils.syncTileEntity(getTileData(), -10);
			}
			sticks=60;
		}
		else
			--sticks;
		*/
		
		if(ticks>=20)
			ticks = 0;
		
		if((isPowered() && curMode == Mode.Unknown) || curMode == Mode.On || curMode == Mode.Loop)
		{
			if(!getWorld().isRemote)
			if(getWorld().provider.getDimensionId() == 0)
			{
				if(useEnergy())
				{
					worldObj.playSoundEffect(getPos().getX(), getPos().getY(), getPos().getZ(), "mob.enderdragon.wings", 5.0F, 0.8F + worldObj.rand.nextFloat() * 0.3F);
					PollutionUtils.increasePollution(-0.05F, getWorld().getChunkFromBlockCoords(getPos()));
					++progress;
					
					if(progress == maxProgress)
					{
						progress = 0;
						PollutionUtils.increasePollution(-60, getWorld().getChunkFromBlockCoords(getPos()));
						
						tank.fill(new FluidStack(CCPMFluids.concentratedPollution, 100), true);
						
						if(curMode == Mode.On)
							curMode = Mode.Off;
						
						if(getWorld().rand.nextInt(10)==1)
						{
							if(rubbish == null || rubbish.stackSize==0)
							{
								if(getWorld().rand.nextInt(10) == 1)
								{
									rubbish = new ItemStack(Blocks.web,1);
								}
								else
									rubbish = new ItemStack(Items.string,1);
							}
						}
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
		MiscUtils.saveInventory(this, nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		tank = tank.readFromNBT(nbt);
		MiscUtils.loadInventory(this, nbt);
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



	@Override
	public int getProgress() {
		return progress;
	}



	@Override
	public int getMaxProgress() {
		return maxProgress;
	}



	@Override
	public String getName() {
		return "container.adv.air";
	}

	ItemStack rubbish;

	@Override
	public boolean hasCustomName() {
		return false;
	}



	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(StatCollector.translateToLocal(getName()));
	}



	@Override
	public int getSizeInventory() {
		return 1;
	}



	@Override
	public ItemStack getStackInSlot(int index) {
		return index == 0 ? rubbish : null;
	}



	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(index == 0 && count > 0)
		{
		rubbish.splitStack(count);
		return rubbish;
		}
		return null;
	}



	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index == 0)
		{
		ItemStack ret = rubbish.copy();
		rubbish = null;
		return ret;
		}
		return null;
	}



	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(index == 0)
		rubbish = stack;
		
	}



	@Override
	public int getInventoryStackLimit() {
		return 1;
	}



	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}



	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		
		return false;
	}



	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void clear() {
		rubbish = null;
		
	}



	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{0};
	}



	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}



	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return !CCPMConfig.hardcoreFilter;
	}



	@Override
	public Mode getControlMode() {
		return curMode;
	}



	@Override
	public void setControlMode(Mode mode) {
		curMode=mode;
	}



	@Override
	public boolean acceptsControlMode(Mode mode) {
		return true;
	}
}
