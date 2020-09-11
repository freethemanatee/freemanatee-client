package me.zopac.freemanatee.module.modules.render;

import me.zopac.freemanatee.module.*;
import me.zopac.freemanatee.util.*;

@Module.Info(name = "FixGUI", category=Module.Category.RENDER)
public class FixGui extends Module  {

    @Override
    public void onUpdate() {
        GuiFrameUtil.fixFrames(FixGui.mc);
    }
}
