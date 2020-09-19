package me.zopac.freemanatee.module.modules.misc;

import me.zopac.freemanatee.module.Module;
import net.minecraft.world.GameType;

@Module.Info(
        name = "FakeCreative",
        category = Module.Category.MISC
)
public class FakeCreative extends Module {
    @Override
    public void onEnable() {
        if (mc.player == null) {
            this.disable();
            return;
        }
        mc.playerController.setGameType(GameType.CREATIVE);
    }
    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        mc.playerController.setGameType(GameType.SURVIVAL);
    }

}
