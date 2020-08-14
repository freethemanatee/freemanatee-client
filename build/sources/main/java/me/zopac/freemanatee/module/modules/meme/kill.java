package me.zopac.freemanatee.module.modules.meme;

import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "/kill",
        category = Module.Category.MEME
)

public class kill extends Module {

    protected void onEnable() {
        mc.player.sendChatMessage("/kill");
        this.disable();
        }
    }