package ccpm.fluids;

import ccpm.api.CCPMApi;
import ccpm.api.IRespirator;
import ccpm.core.CCPM;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class FluidPollution extends BlockFluidClassic {

	public FluidPollution() {
		super(CCPMFluids.concentratedPollution, Material.WATER);
		this.setHardness(0);
		this.setResistance(0);
		this.setUnlocalizedName("liquid_ccpm_pollution");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state ,Entity entity)
	{
		super.onEntityCollidedWithBlock(world, pos, state, entity);
		
		if(!world.isRemote)
		{
			if(entity != null && entity instanceof EntityLivingBase)
			{
				EntityLivingBase elb = (EntityLivingBase)entity;
				
				if(elb.getItemStackFromSlot(EntityEquipmentSlot.HEAD) !=null)
				if(elb.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof IRespirator)
				{
					if(entity instanceof EntityPlayer)
					{
						if(((IRespirator)elb.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem()).isFiltering((EntityPlayer)entity, elb.getItemStackFromSlot(EntityEquipmentSlot.HEAD)))
						{
							return;
						}
					}
				}
				elb.attackEntityFrom(CCPMApi.damageSourcePollution, 1);
				elb.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, world.rand.nextBoolean() ? 1 : 2));
				elb.addPotionEffect(new PotionEffect(MobEffects.WITHER, 10, 1));
				elb.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200, 1));
				elb.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 600, 3));
				elb.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 2));
				elb.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 1000, 2));
				elb.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 2));
			}
		}
	}
}
