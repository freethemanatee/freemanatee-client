package me.zopac.freemanatee.module.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "ReverseStep",
        category = Module.Category.MOVEMENT
)
public class ReverseStep extends Module {

    public void onUpdate() {
        if (ReverseStep.mc.player == null || ReverseStep.mc.world == null || ReverseStep.mc.player.isInWater() || ReverseStep.mc.player.isInLava()) {
            return;
        }
        if (ReverseStep.mc.player.onGround) {
            final EntityPlayerSP player = ReverseStep.mc.player;
            --player.motionY;
        }
    }
}