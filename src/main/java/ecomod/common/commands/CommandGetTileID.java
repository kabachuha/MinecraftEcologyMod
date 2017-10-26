package ecomod.common.commands;

import ecomod.common.utils.EMUtils;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World w = sender.getEntityWorld();
		
		if(w.isRemote)
			return;
		
		BlockPos blockpos;
		if (args.length < 3)
        {
			blockpos = sender.getPosition().down();
        }
		else
		{
			blockpos = parseBlockPos(sender, args, 0, false);
		}
		
		TileEntity tile = w.getTileEntity(blockpos);
		
		if(tile != null)
		{
			String s = EMUtils.getTileEntityId(tile.getClass()).toString();
			sender.addChatMessage(new TextComponentString(s));
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
