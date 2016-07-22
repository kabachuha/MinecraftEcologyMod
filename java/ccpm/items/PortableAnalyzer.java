package ccpm.items;

import java.util.List;

import ccpm.utils.PollutionUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PortableAnalyzer extends Item {

	public PortableAnalyzer() {
		super();
		this.setUnlocalizedName("portable.analyzer");
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
		
		world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_AMBIENT, SoundCategory.BLOCKS, 6, 0.8F + world.rand.nextFloat() * 0.3F);
		
		player.addChatMessage(new TextComponentString("The amount of pollution in this chunk is "+PollutionUtils.getChunkPollution(player.worldObj, player.getPosition())));
		
		return item;
	}
	
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.UNCOMMON;
	}
	
	public EnumAction getItemUseAction(ItemStack is)
	{
		return EnumAction.BLOCK;
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack item, World world, EntityPlayer player, EnumHand hand)
    {
		world.playSound(null, player.getPosition(), SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.PLAYERS, 6, 0.8F + world.rand.nextFloat() * 0.3F);
		player.addChatMessage(new TextComponentString("Starting analyzing the air in this chunk."));
		player.addChatMessage(new TextComponentString("Please, stand by."));
		player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS ,getMaxItemUseDuration(item),6));
		player.setActiveHand(hand);
        return new ActionResult(EnumActionResult.SUCCESS, item);
    }
}
