package ccpm.utils.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import ccpm.core.CCPM;
import net.minecraftforge.fml.common.FMLCommonHandler;


public class ConfigDownloader {

	public ConfigDownloader() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public static void download(File file, String URL)
	{
		
		StringBuffer buffer = null;
		CCPM.log.info("Starting downloading: "+file.getName()+" from:" + URL);
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
		      }
		      in.close();
		  }
		  catch (IOException e)
		  {
			  FMLCommonHandler.instance().raiseException(e, "Unable to connect to server: "+URL+" and download json configuration file! Check your internet connection!", true);
			  e.printStackTrace();
			  return;
		  }
		
		 if(buffer != null)
		 {
			 CCPM.log.info("[CCPM]Data successfully recieved!");
			 CCPM.log.info("[CCPM]Trying to write data in file "+file.getName());
			 
			 try
			 {
				 file.createNewFile();
				 FileUtils.writeStringToFile(file, buffer.toString());
			 }
			 catch(IOException e)
			 {
				 FMLCommonHandler.instance().raiseException(e, "Unable to create or write data to "+file.getName()+"!", true);
				 e.printStackTrace();
				 return;
			 }
			 
			 
			 CCPM.log.info("[CCPM]File: "+file.getName()+" from: "+URL+" downloaded, created and writed successfully!");
		 }
		 
		
	}

}
