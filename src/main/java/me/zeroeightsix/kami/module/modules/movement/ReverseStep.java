package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.module.Module;

@Module.Info(
        name = "ReverseStep",
        category = Module.Category.MOVEMENT
)
public class ReverseStep extends Module {


    public void onUpdate() {
        if (mc.player.onGround)
            --mc.player.motionY;
    }
}
