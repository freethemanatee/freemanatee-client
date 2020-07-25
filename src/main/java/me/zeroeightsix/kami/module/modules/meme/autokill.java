package me.zeroeightsix.kami.module.modules.meme;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;

@Module.Info(
        name = "/kill",
        category = Module.Category.MEME
)

public class autokill extends Module {

    protected void onEnable() {
        Command.sendChatMessage("stop trying to kill yourself, get some help");
            if (mc.player == null) {
                return;
            }
            this.disable();
        }
    }
