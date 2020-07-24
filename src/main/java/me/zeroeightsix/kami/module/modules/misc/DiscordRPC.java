package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.Discord;
import me.zeroeightsix.kami.module.Module;

@Module.Info(
        name = "DiscordRPC",
        category = Module.Category.MISC
)
public class DiscordRPC extends Module{
    @Override
    protected void onEnable() {
        Discord.start();
    }

    @Override
    protected void onDisable() {
        Discord.end();
    }
}
