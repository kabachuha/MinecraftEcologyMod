package ccpm.gui;

import DummyCore.Utils.GuiContainerLibrary;

public class CCPMGuis {

	public static int guiAdvID = -1;
	public static void init()
	{
		guiAdvID = GuiContainerLibrary.registerGuiContainer(GuiAdvFilter.class.getName(), ConAdv.class.getName());
	}

}
