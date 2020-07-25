package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.util.math.MathHelper;

@Module.Info(
        name = "BoostElytraFly",
        category = Module.Category.MOVEMENT
)
public class BoostFlight extends Module {

    @Override
    public void onUpdate() {
                if (mc.gameSettings.keyBindJump.isKeyDown())
                    mc.player.motionY += 0.08;
                else if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.player.motionY -= 0.04;

                if (mc.gameSettings.keyBindForward.isKeyDown()) {
                    float yaw = (float) Math
                            .toRadians(mc.player.rotationYaw);
                    mc.player.motionX -= MathHelper.sin(yaw) * 0.05F;
                    mc.player.motionZ += MathHelper.cos(yaw) * 0.05F;
                } else if (mc.gameSettings.keyBindBack.isKeyDown()) {
                    float yaw = (float) Math
                            .toRadians(mc.player.rotationYaw);
                    mc.player.motionX += MathHelper.sin(yaw) * 0.05F;
                    mc.player.motionZ -= MathHelper.cos(yaw) * 0.05F;
                }

        }


    @Override
    protected void onDisable() {
        if (mc.player.capabilities.isCreativeMode) return;
        mc.player.capabilities.isFlying = false;

    }
}