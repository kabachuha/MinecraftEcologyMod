package ecomod.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ecomod.api.EcomodStuff;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMAchievements;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemPollutionEffectsBook extends ItemWritableBook
{
	public ItemPollutionEffectsBook()
	{
		super();
		setTextureName("ecomod:pollution_effects_book");
		setCreativeTab(EcomodStuff.ecomod_creative_tabs);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player)
	{
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Coming soon..."));
		//FIXME EcologyMod.proxy.openGUIEffectsBook(player);
		//player.addStat(EMAchievements.ACHS.get("effects_book"), 1);
		return item;
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List tooltip, boolean advanced)
	{
		tooltip.add(EnumChatFormatting.BOLD+" "+EnumChatFormatting.DARK_RED+"WORK IN PROGRESS");
		tooltip.add(EnumChatFormatting.ITALIC + I18n.format("book.byAuthor", EMConsts.name));
	}
}
