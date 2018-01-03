package ecomod.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;


@IFMLLoadingPlugin.MCVersion("1.7.10")
public class EcomodLoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"ecomod.asm.EcomodClassTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
