package me.zopac.freemanatee.module.modules.chat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.event.events.PacketEvent;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;

@Module.Info(name = "GreenText", category = Module.Category.CHAT)
public class GreenText extends Module {

    private Setting<Boolean> space = register(Settings.b("Space", true));

    @EventHandler
    private Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketChatMessage){
            if(((CPacketChatMessage) event.getPacket()).getMessage().startsWith("/") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith(Command.getCommandPrefix()) || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith(".") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith("!") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith("#")) return;
            String old = ((CPacketChatMessage) event.getPacket()).getMessage();
            String suffix = "";
            String s = "";
            if(space.getValue()){
                suffix = "> ";
            } else {
                suffix = ">";
            }
            s = suffix + old;
            int longs = s.length();
            int ok = 0;
            if(s.length() > 255)
                ok = longs - 255;
            s = s.substring(0, s.length()-ok);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });
}