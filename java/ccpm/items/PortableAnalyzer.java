package ccpm.items;

import java.util.List;

import DummyCore.Utils.MiscUtils;
import DummyCore.Utils.ScheduledServerAction;
import ccpm.utils.PollutionUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
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
		
		world.playSoundAtEntity(player, "mob.wither.idle", 6, 0.8F + world.rand.nextFloat() * 0.3F);
		
		player.addChatMessage(new ChatComponentText("The amount of pollution in this chunk is "+PollutionUtils.getChunkPollution(player)));
		
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
    public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player)
    {
		world.playSoundAtEntity(player, "tile.piston.in", 6, 0.8F + world.rand.nextFloat() * 0.3F);
		player.addChatMessage(new ChatComponentText("Starting analyzing the air in this chunk."));
		player.addChatMessage(new ChatComponentText("Please, stand by."));
		player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),getMaxItemUseDuration(item),6));
		player.setItemInUse(item, getMaxItemUseDuration(item));
		return item;
    }
}
