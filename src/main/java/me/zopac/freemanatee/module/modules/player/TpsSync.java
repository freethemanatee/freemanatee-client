package me.zopac.freemanatee.module.modules.player;

import me.zopac.freemanatee.module.Module;

@Module.Info(name = "TpsSy nc", category = Module.Category.PLAYER)
public class TpsSync extends Module {

    private static TpsSync INSTANCE;

    public TpsSync() {
        INSTANCE = this;
    }

    public static boolean isSync() {
        return INSTANCE.isEnabled();
    }

}
