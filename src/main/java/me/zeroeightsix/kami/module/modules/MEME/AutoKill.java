package me.zeroeightsix.kami.module.modules.MEME;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;

@Module.Info(
        name = "AutoKill",
        category = Module.Category.MEMES
)

public class AutoKill extends Module {


    protected void onEnable() {
        Command.sendChatMessage("/kill");
        if (mc.player == null) {
            return;
        }
    }
}