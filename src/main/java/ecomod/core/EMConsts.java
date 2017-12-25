package ecomod.core;

import java.util.List;

public class EMConsts
{
	//Mod data
	public static final String modid = "ecomod";
	
	public static final String name = "Ecology Mod";
	
	/**	
	 * MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH <br> 
	 * 
	 * 	https://mcforge.readthedocs.io/en/latest/conventions/versioning/
	 * 
	 */
	public static final String mcversion = "1.7.10";
	public static final String version = mcversion + "-1.2.4.0-beta";
	
	public static final String deps = "required-after:FML";
	
	//
	
	public static final String githubURL = "https://github.com/Artem226/MinecraftEcologyMod";
	
	public static final String issues = "https://github.com/Artem226/MinecraftEcologyMod/issues";
	
	public static final String projectURL = "https://minecraft.curseforge.com/projects/ecology-mod";
	
	public static final String contributors = "Artem226(author/maintainer), xhz313123(Chinese translation) and all feedbackers.";
	
	public static final List<String> authors = java.util.Arrays.asList(new String[]{"Artem226"});
	
	//Proxies
	
	public static final String common_proxy = "ecomod.common.proxy.ComProxy";
	public static final String client_proxy = "ecomod.client.proxy.CliProxy";
	
	// Consts and global variables
	
	public static final int analyzer_gui_id = 0;
	
	public static boolean asm_transformer_inited = false;
}
