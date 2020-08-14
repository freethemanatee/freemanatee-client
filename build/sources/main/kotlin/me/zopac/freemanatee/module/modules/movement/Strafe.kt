package me.zopac.freemanatee.module.modules.movement

import me.zopac.freemanatee.module.Module
import me.zopac.freemanatee.setting.Settings
import me.zopac.freemanatee.util.MovementUtils
import net.minecraft.client.settings.KeyBinding
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created july 14th 2020 by historian
 */
@Module.Info(
        name = "Strafe",
        category = Module.Category.MOVEMENT
)

class Strafe : Module() {

    private val airSpeedBoost = register(Settings.b("AirSpeedBoost", true))
    private val timerBoost = register(Settings.b("TimerBoost", false))
    private val autoJump = register(Settings.b("AutoJump", true))

    private var jumpTicks = 0

    override fun onUpdate() {
        if ((mc.player.moveForward != 0F || mc.player.moveStrafing != 0F) && !mc.player.isElytraFlying) {
            MovementUtils.setSpeed(MovementUtils.getSpeed())
            if (airSpeedBoost.value) mc.player.jumpMovementFactor = 0.029F
            if (timerBoost.value) mc.timer.tickLength = 50 / 1.09F
            if (autoJump.value && mc.player.onGround && jumpTicks == 0) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, false)
                mc.player.motionY = 0.41
                if (mc.player.isSprinting) {
                    mc.player.motionX -= sin(MovementUtils.getMoveYaw()) * 0.2
                    mc.player.motionZ += cos(MovementUtils.getMoveYaw()) * 0.2
                }
                mc.player.isAirBorne = true
                jumpTicks = 5
            }
            if (jumpTicks > 0) jumpTicks--
        }
    }

    override fun onDisable() {
        mc.player.jumpMovementFactor = 0.02F
        mc.timer.tickLength = 50F
        jumpTicks = 0
    }
}