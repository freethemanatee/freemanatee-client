package me.zopac.freemanatee.module.modules.player;

import me.zopac.freemanatee.module.Module;

@Module.Info(name = "AutoJump", category = Module.Category.PLAYER)
public class AutoJump extends Module {

    @Override
    public void onUpdate() {
        if (mc.player.isInWater() || mc.player.isInLava()) mc.player.motionY = 0.1;
        else if (mc.player.onGround) mc.player.jump();
    }

}
