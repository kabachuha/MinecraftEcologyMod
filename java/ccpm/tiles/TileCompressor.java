package ccpm.tiles;

import java.util.Random;

import DummyCore.Utils.Coord3D;
import DummyCore.Utils.DummyData;
import DummyCore.Utils.ITEHasGameData;
import DummyCore.Utils.MiscUtils;
import DummyCore.Utils.TileStatTracker;
import ccpm.api.ICCPMEnergySource;
import ccpm.api.IHasProgress;
import ccpm.core.CCPM;
import ccpm.fluids.CCPMFluids;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.crafting.IInfusionStabiliser;

public class TileCompressor extends TileEntity implements IFluidHandler, IInventory/*, ITEHasGameData*/, IHasProgress,
		IInfusionStabiliser, ITickable, ISidedInventory {

	public EnumFacing pipeConDir = EnumFacing.UP;
	
	public ItemStack output;
	
	public int progress = 0;
	
	public static int maxProgress = 100;
	
	private TileStatTracker track;
	
	public TileCompressor() {
		track = new TileStatTracker(this);
	}

	@Override
	public String getName() {
		return "inventory.ccpm.compressor";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		return StatCollector.canTranslate(getName()) ? new ChatComponentText(StatCollector.translateToLocal(getName())) : new ChatComponentText("Compressor");
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? null : side == pipeConDir ? null : new int[]{0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 0 ? direction == EnumFacing.DOWN ? false : direction == pipeConDir ? false : true : false;
	}

	int sticks = -1;
	
	public boolean isWorking = false;
	
	@Override
	public void update() 
	{
		if(output != null)
			if(output.stackSize >= 64)
			{
				isWorking = false;
				return;
			}
		
		if(!isWorking)
		{
			progress = 0;
		}
		else
		{
			World w = getWorld();
			Random rand = w.rand;
			for(int i = -2; i <= 2; i++)
				for(int j = -2; j <=2; j++)
					for(int k = -2; k <=2; k++)
					{
						if(rand.nextBoolean())
						w.spawnParticle(rand.nextBoolean() ? EnumParticleTypes.CLOUD : EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D, new int[0]);
						if(rand.nextBoolean())
						w.spawnParticle(EnumParticleTypes.REDSTONE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D, new int[0]);
						if(rand.nextBoolean())
						w.spawnParticle(EnumParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D, new int[0]);
						if(rand.nextBoolean())
						if(rand.nextBoolean())
						w.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D, new int[0]);
					}
		}
		
		if(isPowered())
		{
			if(!getWorld().isRemote)
			{
				World w = getWorld();
				Random rand = w.rand;
				
				
				if(!isWorking)
				if(!CCPMConfig.needStructure || isStructureBuilt())
				if(hasEnoughPollution(1000))
				isWorking = true;
				
				
				if(isWorking)
				{
				
				
				
				if(useEnergy())
				{
					tank.drain(10, true);
					
					++progress;
				}
				
				if(progress >= maxProgress)
				{
					isWorking = false;
					progress = 0;
					if(output == null)
					{
						ItemStack stack = new ItemStack(CCPM.miscIngredient,1,1);
						output = stack;
					}
					else
					{
						++output.stackSize;
						
						if(output.stackSize > 64)
							w.newExplosion(null, getPos().getX(), getPos().getY(), getPos().getZ(), 32, true, true);
					}
					
				}
				
				}
			}
		}
	}

	@Override
	public boolean canStabaliseInfusion(World world, BlockPos pos) {
		return CCPMConfig.fstab;
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
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		tank.writeToNBT(nbt);
		nbt.setBoolean("isWorking", isWorking);
		nbt.setInteger("progress", progress);
		MiscUtils.saveInventory(this, nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		tank = tank.readFromNBT(nbt);
		isWorking = nbt.getBoolean("isWorking");
		progress = nbt.getInteger("progress");
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
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index == 0 ? output : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(index == 0)
		if(output!=null)
		{
			ItemStack ret = output.splitStack(count);
			
			return ret;
		}
		
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index == 0)
		{
		     ItemStack ret = output.copy();
		     output = null;
		     return ret;
		}
		
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(index==0)
			output = stack;

	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
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

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		
		return index == 0 ? stack.getItem() == CCPM.pollutionBrick : false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
	
	
	public FluidTank tank = new FluidTank(CCPMFluids.concentratedPollution, 0, 10000);

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(from == pipeConDir && resource.getFluid() == CCPMFluids.concentratedPollution)return tank.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return from == pipeConDir && fluid == CCPMFluids.concentratedPollution;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[]{tank.getInfo()};
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
	
	private boolean hasEnoughPollution(int amount)
	{
		return tank.getFluid() == null ? false : tank.getFluid().getFluid() == CCPMFluids.concentratedPollution ? tank.getFluidAmount() >=amount ? true : false : false; 
	}
	
	private boolean isStructureBuilt()
	{
		boolean ret = true;
		
		ret = ret && checkIron(getPos().add(1, 1, 1));//+
		ret = ret && checkIron(getPos().add(1, -1, 1));//+
		ret = ret && checkIron(getPos().add(-1, 1, -1));//+
		ret = ret && checkIron(getPos().add(-1, -1, -1));//+
		ret = ret && checkIron(getPos().add(1, -1, -1));//+
		ret = ret && checkIron(getPos().add(1, 1, -1));//+
		ret = ret && checkIron(getPos().add(-1, 1, 1));//+
		ret = ret && checkIron(getPos().add(-1, -1, 1));//+
		
		return ret;
	}
	
	private boolean checkIron(BlockPos pos)
	{
		return getWorld().getBlockState(pos) == Blocks.iron_block.getDefaultState();
	}
}
