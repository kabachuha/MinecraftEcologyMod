package ccpm.items;

import java.util.List;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Utils.IOldItem;
import DummyCore.Utils.MiscUtils;
import ccpm.api.CCPMApi;
import ccpm.api.IRespirator;
import ccpm.utils.PollutionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import thaumcraft.api.items.IRepairable;
import thaumcraft.api.items.IWarpingGear;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Optional.Interface;


@Optional.InterfaceList({
@Optional.Interface(iface = "thaumcraft.api.items.IRepairable", modid = "Thaumcraft"),
@Optional.Interface(iface = "thaumcraft.api.items.IWarpingGear", modid = "Thaumcraft")
})
public class PollutedSword extends ItemSword implements IOldItem, IRepairable, IWarpingGear {

	public PollutedSword() {
		super(CCPMApi.pollMaterial);
		this.setUnlocalizedName("ccpm.sword");
	}

	Icon icon;
	
	@Override
	public int getWarp(ItemStack itemstack, EntityPlayer player) {
		return 1;
	}

	@Override
	public Icon getIconFromDamage(int meta) {
		return icon;
	}

	@Override
	public Icon getIconFromItemStack(ItemStack stk) {
		return icon;
	}

	@Override
	public void registerIcons(IconRegister reg) {
		icon=reg.registerItemIcon("ccpm:ccpb_sword");
	}

	@Override
	public int getRenderPasses(ItemStack stk) {
		return 0;
	}

	@Override
	public Icon getIconFromItemStackAndRenderPass(ItemStack stk, int pass) {
		return icon;
	}

	@Override
	public boolean recreateIcon(ItemStack stk) {
		return false;
	}

	@Override
	public boolean render3D(ItemStack stk) {
		return true;
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		if(player == null || stack == null)
			return;
		
		if(player.worldObj == null) return;
		
		if(player.worldObj.isRemote)
			return;
		
		if(count >= 20)
			player.getEntityWorld().playSoundAtEntity(player, "random.fizz", 0.3F, 2.6F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.8F);
		
		if(count >=50)
		{
			stack.damageItem(1, player);
			PollutionUtils.increasePollution(6, player.worldObj.getChunkFromBlockCoords(player.getPosition()));
		}
	}

	
	
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 200;
    }
	
	
	public ItemStack onItemUseFinish(ItemStack item, World world, EntityPlayer player)
	{
		if(item==null || player == null)
			return item;
		
		if(world.isRemote)
			return item;
		
		
		List<Entity> elb = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, AxisAlignedBB.fromBounds(player.posX-8, player.posY-8, player.posZ-8, player.posX+8, player.posY+8, player.posZ+8));
		
		if(elb == null || elb.size() <= 0)
			return item;
		
		BlockPos pos = player.getPosition();
		
		
		
		for(Entity e : elb)
		{
			if(e instanceof EntityLivingBase)
			{
				EntityLivingBase el = (EntityLivingBase)e;
				
				int multiplier = (int) MiscUtils.applyPotionDamageCalculations(el, CCPMApi.damageSourcePollution, this.getDamageVsEntity()+world.rand.nextInt(3));
				
				if(e instanceof EntityPlayer)
				{
				if(el.getEquipmentInSlot(4) == null||!(el.getEquipmentInSlot(4).getItem() instanceof IRespirator)||!((IRespirator)el.getEquipmentInSlot(4).getItem()).isFiltering((EntityPlayer)el, el.getEquipmentInSlot(4)))
				{
				el.attackEntityFrom(CCPMApi.damageSourcePollution, multiplier);
				
				el.addPotionEffect(new PotionEffect(Potion.poison.id,10*multiplier, 2));
				}
				}
				else
				{
					el.attackEntityFrom(CCPMApi.damageSourcePollution, multiplier);
					
					el.addPotionEffect(new PotionEffect(Potion.poison.id,10*multiplier, 3));
				}
				
				player.getEntityWorld().playSoundAtEntity(player, "random.fizz", 32F, 2.6F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.8F);
				
				for(int i = -2; i <= 2; i++)
					for(int j = -2; j <=2; j++)
						for(int k = -2; k <=2; k++)
						{
							player.getEntityWorld().spawnParticle(player.getEntityWorld().rand.nextBoolean() ? EnumParticleTypes.CLOUD : EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + player.getEntityWorld().rand.nextFloat()) - 0.5D, (double)((float)k - player.getEntityWorld().rand.nextFloat() - 1.0F), (double)((float)j + player.getEntityWorld().rand.nextFloat()) - 0.5D, new int[0]);
							player.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + player.getEntityWorld().rand.nextFloat()) - 0.5D, (double)((float)k - player.getEntityWorld().rand.nextFloat() - 1.0F), (double)((float)j + player.getEntityWorld().rand.nextFloat()) - 0.5D, new int[0]);
							player.getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + player.getEntityWorld().rand.nextFloat()) - 0.5D, (double)((float)k - player.getEntityWorld().rand.nextFloat() - 1.0F), (double)((float)j + player.getEntityWorld().rand.nextFloat()) - 0.5D, new int[0]);
						}
				
				PollutionUtils.increasePollution(100, player.getEntityWorld().getChunkFromBlockCoords(pos));
				item.damageItem(100, player);
			}
		}
		
		return item;
		
	}
	
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.RARE;
	}
}
