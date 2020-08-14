package me.zopac.freemanatee.module.modules;

import me.zopac.freemanatee.gui.kami.DisplayGuiScreen;
import me.zopac.freemanatee.module.Module;
import org.lwjgl.input.Keyboard;

@Module.Info(name = "ClickGUI", description = "Opens the Click GUI", category = Module.Category.RENDER)
public class ClickGUI extends Module {

    public ClickGUI() {
        getBind().setKey(Keyboard.KEY_Y);
    }

    @Override
    protected void onEnable() {
        if (!(mc.currentScreen instanceof DisplayGuiScreen)) {
            mc.displayGuiScreen(new DisplayGuiScreen(mc.currentScreen));
        }
        disable();
    }

}
