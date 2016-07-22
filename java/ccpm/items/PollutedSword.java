package ccpm.items;

import java.util.List;

import ccpm.api.CCPMApi;
import ccpm.api.IRespirator;
import ccpm.utils.PollutionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Optional.Interface;


public class PollutedSword extends ItemSword {

	public PollutedSword() {
		super(CCPMApi.pollMaterial);
		this.setUnlocalizedName("ccpm.sword");
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
	{
		if(player == null || stack == null)
			return;
		
		if(player.worldObj == null) return;
		
		if(player.worldObj.isRemote)
			return;
		
		if(count >= 20)
			player.getEntityWorld().playSound(player instanceof EntityPlayer ? (EntityPlayer) player : null, player.getPosition(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 0.3F, 2.6F + (player.getEntityWorld().rand.nextFloat() - player.getEntityWorld().rand.nextFloat()) * 0.8F);
		
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
	
	public ItemStack onItemUseFinish(ItemStack item, World world, EntityLivingBase player)
	{
		if(item==null || player == null)
			return item;
		
		if(world.isRemote)
			return item;
		
		
		List<Entity> elb = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(player.posX-8, player.posY-8, player.posZ-8, player.posX+8, player.posY+8, player.posZ+8));
		
		if(elb == null || elb.size() <= 0)
			return item;
		
		BlockPos pos = player.getPosition();
		
		
		
		for(Entity e : elb)
		{
			if(e instanceof EntityLivingBase)
			{
				EntityLivingBase el = (EntityLivingBase)e;
				
				int multiplier = (int) (this.getDamageVsEntity()+world.rand.nextInt(3));
				
				if(e instanceof EntityPlayer)
				{
				if(el.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null||!((el.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof IRespirator)&&!((IRespirator)el.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem()).isFiltering((EntityPlayer)el, el.getItemStackFromSlot(EntityEquipmentSlot.HEAD))))
				{
				el.attackEntityFrom(CCPMApi.damageSourcePollution, multiplier);
				
				el.addPotionEffect(new PotionEffect(MobEffects.POISON,10*multiplier, 2));
				}
				}
				else
				{
					el.attackEntityFrom(CCPMApi.damageSourcePollution, multiplier);
					
					el.addPotionEffect(new PotionEffect(MobEffects.POISON,10*multiplier, 3));
				}
				
				player.getEntityWorld().playSound(player instanceof EntityPlayer ? (EntityPlayer)player : null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS, 32, 2.6F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.8F);
				
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
	
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        playerIn.setActiveHand(hand);
        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
    }
	
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BLOCK;
    }
}
