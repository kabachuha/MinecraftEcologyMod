package ecomod.common.blocks;

import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.stuff.EMAchievements;
import ecomod.core.stuff.EMConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidPollution extends BlockFluidFinite {

	public BlockFluidPollution()
	{
		super(EcomodStuff.concentrated_pollution, Material.WATER);
		
		setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		setUnlocalizedName(EMConsts.modid+".block."+EcomodStuff.concentrated_pollution.getName());
		setResistance(0.5F);
	}
	
	
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
	{
		if(worldIn.isRemote)
			return;
		
		EcomodAPI.emitPollution(worldIn, EMUtils.blockPosToPair(pos), PollutionSourcesConfig.getSource("concentrated_pollution_explosion_pollution"), true);
		
		if(EMConfig.isConcentratedPollutionExplosive)
			worldIn.newExplosion(explosionIn.getExplosivePlacedBy(), pos.getX(), pos.getY(), pos.getZ(), 3F, true, true);
	}
	
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		if(worldIn.isRemote || entityIn == null)
			return;
		
		if(entityIn instanceof EntityLivingBase)
		{
			if(entityIn.ticksExisted % 20 == 0)
			if(!PollutionUtils.isEntityRespirating((EntityLivingBase) entityIn, true))
			{
				((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("hunger"), 220, 2));
				((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("nausea"), 220, 2));
				((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("poison"), 180, 1));
				
				if(entityIn instanceof EntityPlayer)
				{
					//Achievement ach = EMAchievements.ACHS.get("concentrated_pollution");
				
					//if(ach != null)
					//if(!((EntityPlayer)entityIn).hasAchievement(ach))
					//{
						//((EntityPlayer)entityIn).addStat(ach);
					//}
				}
			}
		}
	}
}
