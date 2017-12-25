package ecomod.common.blocks;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.stuff.EMAchievements;
import ecomod.core.stuff.EMConfig;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidPollution extends BlockFluidFinite {
	
	@SideOnly(Side.CLIENT)
    protected IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    protected IIcon flowingIcon;

	public BlockFluidPollution()
	{
		super(EcomodStuff.concentrated_pollution, Material.water);
		
		setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		setResistance(0.5F);
	}

	@Override
	public int tryToFlowVerticallyInto(World world, int x, int y, int z, int amtToInput) {
        if (y + densityDir < 0 || y + densityDir >= world.getHeight())
        { 
            PollutionData adv_filter_redution = PollutionSourcesConfig.getSource("advanced_filter_redution");
            if(adv_filter_redution != null && adv_filter_redution.compareTo(PollutionData.getEmpty()) != 0)
            {
            	int amount = this.getQuantaValue(world, x, y, z) * 1000 / this.quantaPerBlock;
            	
            	if(amount > 0)
            		EcomodAPI.emitPollution(world, Pair.of(x, z), new PollutionData(-adv_filter_redution.getAirPollution() * amount / 2, -adv_filter_redution.getWaterPollution() * amount / 4, 0), true);
            }
            
            world.setBlockToAir(x, y, z);
            
            return 0;
        }
		return super.tryToFlowVerticallyInto(world, x, y, z, amtToInput);
	}


	@Override
	public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosionIn) {
		if(worldIn.isRemote)
			return;
		
		EcomodAPI.emitPollution(worldIn, Pair.of(x, z), PollutionSourcesConfig.getSource("concentrated_pollution_explosion_pollution"), true);
		
		if(EMConfig.isConcentratedPollutionExplosive)
			worldIn.newExplosion(explosionIn.getExplosivePlacedBy(), x, y, z, 3F, true, true);
	}


	@Override
	public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
		super.onEntityCollidedWithBlock(worldIn, x, y, z, entityIn);
		
		if(worldIn.isRemote || entityIn == null)
			return;
		
		if(entityIn instanceof EntityLivingBase)
		{
			if(entityIn.ticksExisted % 20 == 0)
			if(!PollutionUtils.isEntityRespirating((EntityLivingBase) entityIn, true))
			{
				((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.hunger.getId(), 220, 2));
				((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.confusion.getId(), 220, 2));
				((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.poison.getId(), 180, 1));
			}
		}
	}

	@Override
	public String getUnlocalizedName() {
		return EMConsts.modid+".block."+EcomodStuff.concentrated_pollution.getName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister reg)
	{
		stillIcon = reg.registerIcon(EMConsts.modid+":pollution_still");
		flowingIcon = reg.registerIcon(EMConsts.modid+":pollution_flow");
		
		EcomodStuff.concentrated_pollution.setIcons(stillIcon, flowingIcon);
	}

	@Override
	public MapColor getMapColor(int meta) {
		return MapColor.greenColor;
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return side != 0 && side != 1 && EMConfig.enable_concentrated_pollution_flow_texture ? flowingIcon : stillIcon;
	}
}
