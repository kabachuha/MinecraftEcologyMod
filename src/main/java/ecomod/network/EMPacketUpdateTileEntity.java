package ecomod.network;

import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import ecomod.common.tiles.TileEnergy;
import ecomod.core.EcologyMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class EMPacketUpdateTileEntity implements IMessage
{
	NBTTagCompound tile_tag;
	
	public EMPacketUpdateTileEntity()
	{
		tile_tag = new NBTTagCompound();
	}
	
	public EMPacketUpdateTileEntity(NBTTagCompound tag)
	{
		tile_tag = tag;
	}
	
	public EMPacketUpdateTileEntity(TileEntity tile)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tile.writeToNBT(tag);
		tile_tag = tag;
	}
	
	public NBTTagCompound getData()
	{
		return tile_tag;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		tile_tag = new NBTTagCompound();
		
		byte[] in = new byte[buf.readableBytes()];
		int i = 0;
		
		while(buf.isReadable())
		{
			in[i] = buf.readByte();
			i++;
		}
		
		ByteArrayDataInput datain = ByteStreams.newDataInput(in);
		
		try {
			tile_tag = CompressedStreamTools.func_152456_a(datain, NBTSizeTracker.field_152451_a);
		} catch (IOException e) {
			EcologyMod.log.error("Unable to read a TileEntity update packet!!!");
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteArrayDataOutput dataout = ByteStreams.newDataOutput();

		try {
			CompressedStreamTools.write(tile_tag, dataout);
		} catch (IOException e) {
			EcologyMod.log.error("Unable to form a TileEntity update packet!!!");
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
			return;
		}
		
		buf.writeBytes(dataout.toByteArray());
	}


	
	public static class Handler implements IMessageHandler<EMPacketUpdateTileEntity, IMessage>
	{
		
		@Override
		public IMessage onMessage(EMPacketUpdateTileEntity message, MessageContext ctx)
		{
			if(ctx.side == Side.SERVER)
			{
			}
			else
			{
				EcologyMod.proxy.packetUpdateTE_do_stuff(message);
			}
			
			return null;
		}
	}
}

