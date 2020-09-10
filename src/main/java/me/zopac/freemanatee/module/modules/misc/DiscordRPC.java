package me.zopac.freemanatee.module.modules.misc;

import me.zopac.freemanatee.Discord;
import me.zopac.freemanatee.module.Module;

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
