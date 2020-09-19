package me.zopac.freemanatee.module.modules.movement;

import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "SafeWalk",
        category = Module.Category.MOVEMENT
)
public class SafeWalk extends Module {

    private static SafeWalk INSTANCE;

    public SafeWalk() {
        INSTANCE = this;
    }

    public static boolean shouldSafewalk() {
        return INSTANCE.isEnabled();
    }

}
