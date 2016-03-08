package ccpm.gui;

import DummyCore.Utils.GuiContainerLibrary;

public class CCPMGuis {

	public static int guiAdvID = -1;
	public static int guiCompressorID = -1;
	
	public static void init()
	{
		guiAdvID = GuiContainerLibrary.registerGuiContainer(GuiAdvFilter.class.getName(), ConAdv.class.getName());
		guiCompressorID = GuiContainerLibrary.registerGuiContainer(GuiCompressor.class.getName(), ContainerCompressor.class.getName());
	}

}
