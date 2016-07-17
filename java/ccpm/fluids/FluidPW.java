package ccpm.fluids;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class FluidPW extends BlockFluidClassic {

	public FluidPW() {
		super(CCPMFluids.pollutedWater, Material.WATER);
		this.setHardness(0);
		this.setResistance(0);
		this.setRegistryName("liquid_ccpm_pw");
		this.setUnlocalizedName("liquid_ccpm_pw");
	}
}
