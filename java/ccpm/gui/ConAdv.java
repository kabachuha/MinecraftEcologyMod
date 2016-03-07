package ccpm.gui;

import DummyCore.Utils.ContainerInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ConAdv extends ContainerInventory {

	
		public ConAdv(InventoryPlayer playerInv, TileEntity tileInv) {
			super(playerInv, tileInv);
		}

		@Override
		public void setupSlots() {
			this.addSlotToContainer(new Slot(inv, 0, pInvOffsetX+42, pInvOffsetZ+32));
			
			setupPlayerInventory();
		}

		
		
	
	
	
}
