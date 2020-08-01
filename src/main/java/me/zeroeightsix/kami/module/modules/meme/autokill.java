package me.zeroeightsix.kami.module.modules.meme;

import me.zeroeightsix.kami.module.Module;

@Module.Info(
        name = "/kill",
        category = Module.Category.MEME
)

public class autokill extends Module {

    protected void onEnable() {
        mc.player.sendChatMessage("/kill");
        this.disable();
        }
    }