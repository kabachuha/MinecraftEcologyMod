package ecomod.network;

import java.nio.charset.Charset;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.MinecraftForge;


public class EMPacketString implements IMessage
{
	String string;
	
	public EMPacketString()
	{
		string = "";
	}
	
	/**
	 * <strong>!WARNING!</strong> The string MUST be marked by its first character! Otherwise it will not be handled!
	 * 
	 */
	public EMPacketString(String data)
	{
		string = data;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		string = "";
		while(buf.isReadable())
		{
			string+=buf.readChar();
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		for (int i = 0; i < string.length(); i++) {
			buf.writeChar(string.charAt(i));
		}
	}

	/**EMPacketString.EventReceived*/
	public static class EventReceived extends Event
	{
		private final String content;
		
		public EventReceived(String str)
		{
			content = str;
		}
		
		public String getContent()
		{
			return content;
		}
	}
	
	public static class Handler implements IMessageHandler<EMPacketString, IMessage>
	{
		
		@Override
		public IMessage onMessage(EMPacketString message, MessageContext ctx)
		{
			//Firing an event to be handled on the required side
			
			if(ctx.side == Side.SERVER)
			{
				MinecraftForge.EVENT_BUS.post(new EventReceived(message.string));
			}
			else
			{
				MinecraftForge.EVENT_BUS.post(new EventReceived(message.string));
			}
			
			return null;
		}
	}
}
