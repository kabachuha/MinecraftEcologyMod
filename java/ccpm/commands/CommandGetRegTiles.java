package ccpm.commands;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import DummyCore.Utils.ReflectionProvider;
import ccpm.core.CCPM;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Deprecated
public class CommandGetRegTiles extends CommandBase {

	public CommandGetRegTiles() {

	}

	@Override
	public String getCommandName() {
		return "getRegTiles";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/getRegTiles <path>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		
		if(args == null || args.length == 0)
		{
			sender.addChatMessage(new ChatComponentText("Argument can't be null!"));
			return;
		}
		
		if(sender == null || sender.getEntityWorld() == null || sender.getEntityWorld().isRemote)
			return;
		

		
		Map<String, Class <? extends TileEntity>> map = ReflectionHelper.getPrivateValue(TileEntity.class, null, 1);

		
		
		if(map == null)
		{
			CCPM.log.warning("Map is null!");
			return;
		}
		
		Iterator<String> iter = map.keySet().iterator();
		
		if(iter == null)
		{
			CCPM.log.warning("Iterator is null!");
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		
		while(iter.hasNext())
		{
			sb.append(iter.next()+"\n");
		}
		
		String path = args[0];
		
		File pathFile = new File(path);
		
		if(!pathFile.exists())
		{
			pathFile.mkdirs();
		}
		String path2 = pathFile.getAbsolutePath() + "//TilesNames.txt";
		
		File file = new File(path2);
		
		if(file.isDirectory())
		{
			CCPM.log.warning("File TilesNames.txt is a directory! Please, delete it!");
			return;
		}
		
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				CCPM.log.warning("Unable to create file TilesNames.txt");
				e.printStackTrace();
				return;
			}
		}
		
		if(file.canWrite())
		{
			CCPM.log.info("Writing registred tiles names to : "+file.getAbsolutePath());
			try {
				FileUtils.writeStringToFile(file, sb.toString());
			} catch (IOException e) {
				CCPM.log.warning("Unable to create file TilesNames.txt");
				e.printStackTrace();
				return;
			}
		}
	}

}
