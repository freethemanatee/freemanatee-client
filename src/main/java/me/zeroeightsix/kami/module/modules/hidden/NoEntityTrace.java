package me.zeroeightsix.kami.module.modules.hidden;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Module.Info(
        name = "NoEntityTrace",
        category = Module.Category.HIDDEN,
        description = "Blocks entities from stopping you from mining"
)
public class NoEntityTrace extends Module {
    private Setting mode;
    private static NoEntityTrace INSTANCE;

    public NoEntityTrace() {
        this.mode = this.register(Settings.e("Mode", NoEntityTrace.TraceMode.DYNAMIC));
        INSTANCE = this;
    }

    public static boolean shouldBlock() {
        return INSTANCE.isEnabled() && (INSTANCE.mode.getValue() == NoEntityTrace.TraceMode.STATIC || mc.playerController.isHittingBlock);
    }

    private static enum TraceMode {
        STATIC,
        DYNAMIC;
    }
}
