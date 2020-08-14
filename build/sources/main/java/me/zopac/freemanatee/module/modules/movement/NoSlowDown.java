package me.zopac.freemanatee.module.modules.movement;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.module.Module;
import net.minecraftforge.client.event.InputUpdateEvent;

@Module.Info(
        name = "NoSlow",
        category = Module.Category.MOVEMENT
)
public class NoSlowDown extends Module {

    @EventHandler
    private Listener<InputUpdateEvent> eventListener = new Listener<>(event -> {
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5;
            event.getMovementInput().moveForward *= 5;
        }
    });
}
