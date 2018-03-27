package ecomod.common.commands;

import ecomod.common.utils.EMUtils;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandGetTileID extends CommandBase {

	@Override
	public String getCommandName() {
		return "getTileEntityID";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.ecomod.get_tile_id.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		World w = sender.getEntityWorld();
		
		if(w.isRemote)
			return;
		
		EMBlockPos blockpos;
		if (args.length < 3)
        {
			blockpos = new EMBlockPos(sender.getPlayerCoordinates()).down();
        }
		else
		{
			blockpos = new EMBlockPos(func_110666_a(sender, sender.getPlayerCoordinates().posX, args[0]), func_110666_a(sender, sender.getPlayerCoordinates().posY, args[1]), func_110666_a(sender, sender.getPlayerCoordinates().posZ, args[2]));
		}
		
		TileEntity tile = EMUtils.getTile(w, blockpos);
		
		if(tile != null)
		{
			String s = EMUtils.getTileEntityId(tile.getClass()).toLowerCase();
			sender.addChatMessage(new ChatComponentText(s));
			if(sender instanceof EntityPlayerMP)
				EMPacketHandler.WRAPPER.sendTo(new EMPacketString("#"+s), getCommandSenderAsPlayer(sender));
		}
		else
		{
			throw new CommandException("commands.ecomod.get_tile_id.tile_not_found", blockpos.toString());
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
