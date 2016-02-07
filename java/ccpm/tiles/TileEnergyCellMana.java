package ccpm.tiles;

import net.minecraft.tileentity.TileEntity;
import vazkii.botania.api.mana.IManaReceiver;

public class TileEnergyCellMana extends TileEnergyCellBasic implements IManaReceiver {

	public TileEnergyCellMana(String name, int maxEnergy) {
		super(name, maxEnergy);
	}

	@Override
	public int getCurrentMana() {
		return this.getEnergy();
	}

	@Override
	public boolean useEnergy(int amount, TileEntity user) {
		if(amount > this.getEnergy())
		{
			return false;
		}
		
		this.setEnergy(this.getEnergy()-amount);
		return true;
	}

	@Override
	public boolean isFull() {
		return this.energy < this.maxEnergy;
	}

	@Override
	public void recieveMana(int mana) {
		energy = Math.min(maxEnergy, energy+mana);
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return !isFull();
	}

}
