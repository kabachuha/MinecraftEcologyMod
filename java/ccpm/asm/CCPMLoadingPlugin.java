package ccpm.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.8.9")
public class CCPMLoadingPlugin implements IFMLLoadingPlugin {

	public CCPMLoadingPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] getASMTransformerClass() {
		System.out.println("CCPMLoadingPlugin---->getASMTransformerClass");
		return new String[]{"ccpm.asm.CCPMClassTransformer"};
	}

	@Override
	public String getModContainerClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
