package me.zopac.freemanatee.module.modules.combat;

import me.zopac.freemanatee.mixin.client.ICPacketPlayer;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketPlayer;
import me.zopac.freemanatee.event.events.PacketEvent;
import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "PacketXP",
        category = Module.Category.COMBAT
)

public class PacketXP extends Module {
    public void onUpdate(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && PacketXP.mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle) {
            final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            ((ICPacketPlayer) packet).setPitch(90.0f);
        }
    }
}