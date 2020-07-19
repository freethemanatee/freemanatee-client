package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "HoleTP", description = "slaps you into holes", category = Module.Category.COMBAT)
public class HoleTP extends Module {


    public void onUpdate() {
        if (mc.player.onGround) // gonna try and add a slider here to change how far you go down
            --mc.player.motionY;
    }
}
