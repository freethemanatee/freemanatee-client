package me.zopac.freemanatee.module.modules.player;

import me.zopac.freemanatee.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.event.events.PacketEvent;
import net.minecraft.network.play.client.CPacketCloseWindow;

@Module.Info(
        name = "XCarry",
        category = Module.Category.PLAYER
)
public class XCarry extends Module {

    @EventHandler
    private Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketCloseWindow){
            event.cancel();
        }

    });
}