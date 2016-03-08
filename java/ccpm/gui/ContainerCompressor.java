package ccpm.gui;

import DummyCore.Utils.ContainerInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerCompressor extends ContainerInventory {

	public ContainerCompressor(InventoryPlayer playerInv, TileEntity tileInv) {
		super(playerInv, tileInv);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setupSlots() {
		
		this.addSlotToContainer(new Slot(inv, 0, pInvOffsetX+64, pInvOffsetZ+32));
		
		this.setupPlayerInventory();
	}

}
