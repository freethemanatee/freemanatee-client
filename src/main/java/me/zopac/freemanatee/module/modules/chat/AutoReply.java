package me.zopac.freemanatee.module.modules.chat;

import java.util.function.Predicate;
import me.zopac.freemanatee.util.Wrapper;
import net.minecraft.network.play.server.SPacketChat;
import me.zero.alpine.listener.EventHandler;
import me.zopac.freemanatee.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.module.Module;

@Module.Info(name = "AutoReply", category = Module.Category.CHAT)
public class AutoReply extends Module
{
    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener;

    public AutoReply() {
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketChat && ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText().contains("whispers:")) {
                Wrapper.getPlayer().sendChatMessage("/r stop messaging me i'm doing something");
            }
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
    }
}
