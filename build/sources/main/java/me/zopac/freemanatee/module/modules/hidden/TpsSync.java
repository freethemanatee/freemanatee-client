package me.zopac.freemanatee.module.modules.hidden;

import me.zopac.freemanatee.module.Module;

@Module.Info(name = "TpsSync", description = "Synchronizes some actions with the server TPS", category = Module.Category.HIDDEN)
public class TpsSync extends Module {

    private static TpsSync INSTANCE;

    public TpsSync() {
        INSTANCE = this;
    }

    public static boolean isSync() {
        return INSTANCE.isEnabled();
    }

}
