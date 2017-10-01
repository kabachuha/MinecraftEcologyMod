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
			}
		}
	}
	
	@Override
	public int tryToFlowVerticallyInto(World world, BlockPos pos, int amtToInput)
    {
		BlockPos other = pos.add(0, densityDir, 0);
        if (other.getY() < 0 || other.getY() >= world.getHeight())
        { 
            PollutionData adv_filter_redution = PollutionSourcesConfig.getSource("advanced_filter_redution");
            if(adv_filter_redution != null && adv_filter_redution.compareTo(PollutionData.getEmpty()) != 0)
            {
            	int amount = this.getQuantaValue(world, pos) * 1000 / this.quantaPerBlock;
            	
            	if(amount > 0)
            		EcomodAPI.emitPollution(world, EMUtils.blockPosToPair(pos), new PollutionData(-adv_filter_redution.getAirPollution() * amount / 2, -adv_filter_redution.getWaterPollution() * amount / 4, 0), true);
            }
            
            world.setBlockToAir(pos);
            
            return 0;
        }
        
		return super.tryToFlowVerticallyInto(world, pos, amtToInput);
    }
}
