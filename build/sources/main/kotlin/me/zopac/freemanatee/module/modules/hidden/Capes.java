package me.zopac.freemanatee.module.modules.hidden;

import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "Capes",
        category = Module.Category.RENDER
)

public class Capes extends Module {

    private static Capes INSTANCE;

    public Capes() {
        INSTANCE = this;
    }

    public static boolean isActive() {
        return INSTANCE.isEnabled();
    }

}