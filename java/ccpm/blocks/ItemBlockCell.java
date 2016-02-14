package ccpm.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class ItemBlockCell extends ItemBlock {

	public ItemBlockCell(Block b) {
		super(b);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("ccpm.energycell");
	}

	@Override
	public int getMetadata (int meta) {
		return meta;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if(stack.getItemDamage() == 0)
		{
			return "ccpm.energycell.rf";
		}
		if(stack.getItemDamage() == 1)
		{
			return "ccpm.energycell.th";
		}
		if(stack.getItemDamage() == 2)
		{
			return "ccpm.energycell.ma";
		}
		return "ccpm.energycell";
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
		int meta = stack.getItemDamage();
		
		list.add("Thus is an energy cell from ecology mod."); 
		list.add("Place thus under the machine from ecology mod");
		list.add("to use thus as energy source.");
				
		if(meta == 0)
			list.add("This cell uses rf to work");
		
		if(meta == 1)
			list.add("This cell uses potentia essentia from jars(like infusion) to work");
		
		if(meta == 2)
			list.add("This cell uses mana to work");
		
		if(Loader.isModLoaded("OpenComputers"))
		{
			list.add("You may attach thus to your computer from OC");
		}
		
	}
}
