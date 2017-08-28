package ecomod.core;

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
	public static final String version = "1.11.2-1.0.3.0-beta1";
	
	public static final String deps = "required-after:FML";
	
	public static final String json = "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.11/versions.json";
	
	//
	
	public static final String githubURL = "https://github.com/Artem226/MinecraftEcologyMod";
	
	public static final String issues = "https://github.com/Artem226/MinecraftEcologyMod/issues";
	
	public static final String projectURL = "https://minecraft.curseforge.com/projects/ecology-mod";
	
	//Proxies
	
	public static final String common_proxy = "ecomod.common.proxy.ComProxy";
	public static final String client_proxy = "ecomod.client.proxy.CliProxy";
	
	// Consts
	
	/**
	 * Max cached pollution radius.
	 * Set to prevent abuse when requesting from server
	 */
	public static final int max_cached_pollution_radius = 16;
	
	
	public static final int analyzer_gui_id = 0;
	
	public static boolean asm_transformer_inited = false;
}
