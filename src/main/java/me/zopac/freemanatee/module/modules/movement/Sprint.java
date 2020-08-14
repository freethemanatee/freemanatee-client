package me.zopac.freemanatee.module.modules.movement;

import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "Sprint",
        category = Module.Category.MOVEMENT
)
public class Sprint extends Module {

    @Override
    public void onUpdate() {
        try {
            if (!mc.player.collidedHorizontally && mc.player.moveForward > 0)
                mc.player.setSprinting(true);
            else
                mc.player.setSprinting(false);
        } catch (Exception ignored) {}
    }

}
