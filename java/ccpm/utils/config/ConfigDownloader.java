package ccpm.utils.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ccpm.core.CCPM;
import ccpm.utils.config.PollutionConfig.PollutionProp;
import net.minecraftforge.fml.common.FMLCommonHandler;


public class ConfigDownloader {

	public ConfigDownloader() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public static void download(File file, String URL)
	{
		
		CCPM.log.info("Starting downloading: "+file.getName()+" from:" + URL);
		 String s = downloadString(URL);
		
		 if(s != null)
		 {
			 CCPM.log.info("Data successfully recieved!");
			 CCPM.log.info("Trying to write data in file "+file.getName());
			 
			 try
			 {
				 file.createNewFile();
				 FileUtils.writeStringToFile(file, s);
			 }
			 catch(IOException e)
			 {
				 FMLCommonHandler.instance().raiseException(e, "Unable to create or write data to "+file.getName()+"!", true);
				 e.printStackTrace();
				 return;
			 }
			 
			 
			 CCPM.log.info("File: "+file.getName()+" from: "+URL+" downloaded, created and writed successfully!");
		 }
		 else
		 {
			 FMLCommonHandler.instance().raiseException(new IOException("The data hasn't been received!"), "The data hasn't been received!", true);
			 return;
		 }
		
	}
	
	public static PollutionProp convertToPP(String json)
	{
		if(json != null && json.length() > 0)
		{
			try
			{
			return new Gson().fromJson(json, PollutionProp.class);
			}
			catch(JsonSyntaxException jse)
			{
				CCPM.log.warn("Unable to parse PollutionProp from JSON! JSON is probably invalid!");
				return null;
			}
		}
		
		return null;
	}

	public static String downloadString(String URL)
	{
		StringBuffer buffer = null;
		 try 
		 {
		      URL url = new URL(URL);
		      CCPM.log.info("Connecting to: "+URL);
		      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		      String inputLine;
		      buffer = new StringBuffer();
		      
		      
		      while ((inputLine = in.readLine()) != null) 
		      {
		        buffer.append(inputLine);
		        buffer.append('\n');
		      }
		      buffer.deleteCharAt(buffer.length()-1);
		      in.close();
		  }
		  catch (Exception e)
		  {
			  return null;
		  }
		 return buffer.toString(); 
		 
	}
	
	public static boolean updateConfig(String URL,PollutionProp pp)
	{
		CCPM.log.info("Starting checking version of config");
		String s = downloadString(URL);
		
		if(s == null)
		{
			CCPM.log.warn("Unable to check version of configuration! Check your internet connection!");
			return false;
		}
		
		PollutionProp newPP = convertToPP(s);
		
		if(newPP == null)
			return false;
		
		if(pp.getVersion() != newPP.getVersion())
		{
			pp = newPP;
			return true;
		}
		
		return false;
	}
}
