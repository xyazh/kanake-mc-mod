package com.xyazh.kanake.gui;

import com.xyazh.kanake.gui.test.GuiHandlerTest;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuis {
    public static IGuiHandler GUI_TEST;

    public static void registerGuis(){
        GUI_TEST = new GuiHandlerTest();
    }
}
