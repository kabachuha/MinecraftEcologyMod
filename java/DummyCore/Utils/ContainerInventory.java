package DummyCore.Utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * A nifty wrapper around the Container class which allows you to easily create containers for your inventories
 * <br>This comes with an integrated merging code(shift-click) so you do not have to handle that on your own
 * @author modbder, pahimar
 *
 */
public abstract class ContainerInventory extends Container{

	public final IInventory inv;
	public final InventoryPlayer pInv;
	public final TileEntity tile;
	public final EntityPlayer player;
	
	public int pInvOffsetX;
	public int pInvOffsetZ;
	
	/**
	 * Constructs a new ContainerInventory object from the player's slots and the TileEntity's slots
	 * @param playerInv - the player inventory
	 * @param tileInv - the TileEntity. Must implement IInventory
	 */
	public ContainerInventory(InventoryPlayer playerInv, TileEntity tileInv)
	{
		super();
		inv = (IInventory) tileInv;
		pInv = playerInv;
		tile = tileInv;
		player = playerInv.player;
		
		inv.openInventory(playerInv.player);
		setupSlots();
	}
	
	/**
	 * Override this in your classes. This allows you to setup your slots and positions of them.
	 * <br>At the <b>END</b> of your setup you can call the {@linkplain #setupPlayerInventory()} to automatically create slots for the player's inventory
	 */
	public abstract void setupSlots();
	
	/**
	 * Automatically creates the slots for the player's inventory
	 */
	public void setupPlayerInventory()
	{
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
            	this.addSlotToContainer(new Slot(pInv, j + i * 9 + 9, 8 + j * 18 + pInvOffsetX, 84 + i * 18 + pInvOffsetZ));

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(pInv, i, 8 + i * 18 + pInvOffsetX, 142 + pInvOffsetZ));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inv.isUseableByPlayer(player);
	}
    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        inv.closeInventory(player);
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
    {
        ItemStack newItemStack = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemStack = slot.getStack();
            newItemStack = itemStack.copy();

            if (slotIndex < inv.getSizeInventory())
            {
                if (!this.mergeItemStack(itemStack, inv.getSizeInventory(), inventorySlots.size(), false))
                    return null;
            }
            else if (!this.mergeItemStack(itemStack, 0, inv.getSizeInventory(), false))
                return null;

            if (itemStack.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }

        return newItemStack;
    }    
    
    //Credits to Pahimar for his awesome ItemStack merging code!
	@Override
	public boolean mergeItemStack(ItemStack itemStack, int slotMin, int slotMax, boolean ascending)
	{
	    boolean slotFound = false;
	    int currentSlotIndex = ascending ? slotMax - 1 : slotMin;
        Slot slot;
        ItemStack stackInSlot;
	    if (itemStack.isStackable())
        {
	    	while (itemStack.stackSize > 0 && (!ascending && currentSlotIndex < slotMax || ascending && currentSlotIndex >= slotMin))
	    	{
               slot = (Slot) this.inventorySlots.get(currentSlotIndex);
                stackInSlot = slot.getStack();
                if (slot.isItemValid(itemStack) && equalsIgnoreStackSize(itemStack, stackInSlot))
                {
                    int combinedStackSize = stackInSlot.stackSize + itemStack.stackSize;
                    int slotStackSizeLimit = Math.min(stackInSlot.getMaxStackSize(), slot.getSlotStackLimit());
                    if (combinedStackSize <= slotStackSizeLimit)
                    {
                        itemStack.stackSize = 0;
                        stackInSlot.stackSize = combinedStackSize;
                        slot.onSlotChanged();
                        slotFound = true;
                    }
                    else if (stackInSlot.stackSize < slotStackSizeLimit)
                    {
                        itemStack.stackSize -= slotStackSizeLimit - stackInSlot.stackSize;
                        stackInSlot.stackSize = slotStackSizeLimit;
                        slot.onSlotChanged();
                        slotFound = true;
                    }
               }
               currentSlotIndex += ascending ? -1 : 1;
            }
        }

        if (itemStack.stackSize > 0)
        {
            currentSlotIndex = ascending ? slotMax - 1 : slotMin;
            while (!ascending && currentSlotIndex < slotMax || ascending && currentSlotIndex >= slotMin)
            {
                slot = (Slot) this.inventorySlots.get(currentSlotIndex);
                stackInSlot = slot.getStack();
                if (slot.isItemValid(itemStack) && stackInSlot == null)
                {
                    slot.putStack(cloneItemStack(itemStack, Math.min(itemStack.stackSize, slot.getSlotStackLimit())));
                    slot.onSlotChanged();
                    if (slot.getStack() != null)
                    {
                        itemStack.stackSize -= slot.getStack().stackSize;
                        slotFound = true;
                    }
                    break;
                }
	            currentSlotIndex += ascending ? -1 : 1;
            }
        }
	    return slotFound;
    }
    
    public static ItemStack cloneItemStack(ItemStack itemStack, int stackSize)
    {
        ItemStack clonedItemStack = itemStack.copy();
        clonedItemStack.stackSize = stackSize;
        return clonedItemStack;
    }
	
    public static boolean equalsIgnoreStackSize(ItemStack itemStack1, ItemStack itemStack2)
    {
        if (itemStack1 != null && itemStack2 != null)
            if (itemStack1.getItem().equals(itemStack2.getItem()))
                if (itemStack1.getItemDamage() == itemStack2.getItemDamage())
                    if (itemStack1.hasTagCompound() && itemStack2.hasTagCompound())
                    {
                        if (ItemStack.areItemStackTagsEqual(itemStack1, itemStack2))
                            return true;
                    }
                    else if (!itemStack1.hasTagCompound() && !itemStack2.hasTagCompound())
                        return true;
        
        return false;
    }
}
