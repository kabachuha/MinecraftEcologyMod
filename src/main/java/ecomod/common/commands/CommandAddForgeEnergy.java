package ecomod.common.commands;

import ecomod.common.utils.EMUtils;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class CommandAddForgeEnergy extends CommandBase {

	@Override
	public String getCommandName() {
		return "ecomod_addForgeEnergy";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/ecomod_addForgeEnergy [amount] [x] [y] [z]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World w = sender.getEntityWorld();
		
		if(w.isRemote)
			return;
		
		if(args.length < 1)
		{
			throw new WrongUsageException(getCommandUsage(sender));
		}
		
		int amount = this.parseInt(args[0]);
		
		BlockPos blockpos;
		if (args.length < 4)
        {
			blockpos = sender.getPosition().down();
        }
		else
		{
			blockpos = parseBlockPos(sender, args, 1, false);
		}
		
		TileEntity tile = w.getTileEntity(blockpos);
		
		if(tile != null)
		{
			EnumFacing face = null;
			int i = 0;
			do
			{
				if(tile.hasCapability(CapabilityEnergy.ENERGY, face))
				{
					IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, face);
				
					if(amount >= 0)
					{
						if(storage.canReceive())
						{
							sender.addChatMessage(new TextComponentString("TileEntity at "+ blockpos.toString() + "received " + storage.receiveEnergy(amount, false) + " units of ForgeEnergy(RF)"));
							break;
						}
						else
						{
							sender.addChatMessage(new TextComponentString("TileEntity at " + blockpos.toString() + " is unable to accept energy!"));
						}
					}
				
					if(amount < 0)
					{
						if(storage.canExtract())
						{
							sender.addChatMessage(new TextComponentString(amount + " units of ForgeEnergy(RF) were extracted from TileEntity at "+blockpos.toString()));
							break;
						}
						else
						{
							sender.addChatMessage(new TextComponentString("The energy can't be extracted from TileEntity at" + blockpos.toString()));
						}
					}
				}
				
				face = EnumFacing.VALUES[i];
				i++;
			}
			while(i < 6);
			
			if(i >= 6)
			{
				sender.addChatMessage(new TextComponentString("The TileEntity at " + blockpos.toString() + " doesn't have an accessible energy storage!"));
			}
		}
		else
		{
			throw new CommandException("commands.ecomod.get_tile_id.tile_not_found", blockpos.toString());
		}
	}

}
