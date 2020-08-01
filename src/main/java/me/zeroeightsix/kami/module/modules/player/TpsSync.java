package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "TpsSync", category = Module.Category.PLAYER)
public class TpsSync extends Module {

    private static TpsSync INSTANCE;

    public TpsSync() {
        INSTANCE = this;
    }

    public static boolean isSync() {
        return INSTANCE.isEnabled();
    }

}
