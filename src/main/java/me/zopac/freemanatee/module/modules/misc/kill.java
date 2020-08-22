package me.zopac.freemanatee.module.modules.misc;

import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "/kill",
        category = Module.Category.MISC
)

public class kill extends Module {

    protected void onEnable() {
        mc.player.sendChatMessage("/kill");
        this.disable();
        }
    }