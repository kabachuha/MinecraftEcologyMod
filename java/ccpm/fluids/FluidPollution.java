package ccpm.fluids;

import ccpm.api.CCPMApi;
import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class FluidPollution extends BlockFluidClassic {

	public FluidPollution() {
		super(CCPMFluids.concentratedPollution, Material.water);
		this.setHardness(0);
		this.setResistance(0);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity)
	{
		super.onEntityCollidedWithBlock(world, pos, entity);
		
		if(!world.isRemote)
		{
			if(entity != null && entity instanceof EntityLivingBase)
			{
				EntityLivingBase elb = (EntityLivingBase)entity;
				
				if(elb.getEquipmentInSlot(4) !=null)
				if(elb.getEquipmentInSlot(4).getItem() instanceof IRespirator)
				{
					if(entity instanceof EntityPlayer)
					{
						if(((IRespirator)elb.getEquipmentInSlot(4).getItem()).isFiltering((EntityPlayer)entity, elb.getEquipmentInSlot(4)))
						{
							return;
						}
					}
				}
				elb.attackEntityFrom(CCPMApi.damageSourcePollution, 1);
				elb.addPotionEffect(new PotionEffect(Potion.poison.getId(), 100, world.rand.nextBoolean() ? 1 : 2));
				elb.addPotionEffect(new PotionEffect(Potion.wither.getId(), 10, 1));
				elb.addPotionEffect(new PotionEffect(Potion.blindness.getId(), 200, 1));
				elb.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 600, 3));
				elb.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 200, 2));
				elb.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 1000, 2));
				elb.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 200, 2));
			}
		}
	}
}
