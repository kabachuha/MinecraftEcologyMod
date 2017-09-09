package ecomod.common.commands;

import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

@Deprecated
public class CommandUpdateCache extends CommandBase
{
	@Override
	public String getName()
	{
		return "updatePollutionCache";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/updatePollutionCache";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(sender != null)
		if(sender instanceof EntityPlayerMP)
		{
			try
			{
				EMPacketString to_send = EcologyMod.ph.formCachedPollutionToSend((EntityPlayer)sender, EMConfig.cached_pollution_radius);
				
				if(to_send == null)
				{
					EcologyMod.log.error("Unable to make EMPacketString with mark 'P'!!! Unable to form cached pollution for player "+((EntityPlayer)sender).getName()+"("+((EntityPlayer)sender).getUniqueID().toString()+")");
				}
				else
				{
					EMPacketHandler.WRAPPER.sendTo(to_send, (EntityPlayerMP)sender);
				}
			}
			catch (Exception e)
			{
				EcologyMod.log.error("Error while sending EMPacketString with mark 'P' to the client!");
				EcologyMod.log.error(e.toString());
			}
		}
	}

	public int getRequiredPermissionLevel()
    {
        return 1;
    }
}
