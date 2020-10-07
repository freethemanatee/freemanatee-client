package me.zopac.freemanatee.module.modules.chat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.manatee;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.event.events.PacketEvent;
import me.zopac.freemanatee.event.events.TotemPopEvent;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashMap;

@Module.Info(name = "TotemPopCounter", category = Module.Category.CHAT)
public class TotemPopCounter extends Module {

    private HashMap<String, Integer> popList = new HashMap();
    private Setting<colour> mode = register(Settings.e("Color", colour.GREEN));
    private Setting<allah> pop = register(Settings.e("Announce Mode", allah.Client));

    @EventHandler
    public Listener<TotemPopEvent> totemPopEvent = new Listener<>(event -> {
        if(popList == null) {
            popList = new HashMap<>();
        }


        if(popList.get(event.getEntity().getName()) == null) {
            popList.put(event.getEntity().getName(), 1);
            this.pop.getValue().equals("Client");
            Command.sendChatMessage(colourchoice() + event.getEntity().getName() + " popped " + 1 + " totem");
            this.pop.getValue().equals("Public");
            Command.sendRawChatMessage(colourchoice() + event.getEntity().getName() + " popped " + 1 + " totem");
        } else if(!(popList.get(event.getEntity().getName()) == null)) {
            int popCounter = popList.get(event.getEntity().getName());
            int newPopCounter = popCounter += 1;
            popList.put(event.getEntity().getName(), newPopCounter);
            this.pop.getValue().equals("Client");
            Command.sendChatMessage(colourchoice() + event.getEntity().getName() + " popped " + newPopCounter + " totems");
            this.pop.getValue().equals("Public");
            Command.sendRawChatMessage(colourchoice() + event.getEntity().getName() + " popped " + newPopCounter + " totems");
        }

    });

    @Override
    public void onUpdate() {
        for(EntityPlayer player : mc.world.playerEntities) {
            if(player.getHealth() <= 0) {
                if(popList.containsKey(player.getName())) {
                    this.pop.getValue().equals("Client");
                    Command.sendChatMessage(colourchoice() + player.getName() + " died after popping " + popList.get(player.getName()) + " totems");
                    this.pop.getValue().equals("Public");
                    Command.sendRawChatMessage(colourchoice() + player.getName() + " died after popping " + popList.get(player.getName()) + " totems");
                    popList.remove(player.getName(), popList.get(player.getName()));
                }
            }
        }
    }

    @EventHandler
    public Listener<PacketEvent.Receive> totemPopListener = new Listener<>(event -> {

        if (mc.world == null || mc.player == null) {
            return;
        }

        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(mc.world);
                manatee.EVENT_BUS.post(new TotemPopEvent(entity));
            }
        }

    });

private enum allah {
    Client, Public
}

    private String colourchoice(){
        switch (mode.getValue()){
            case BLACK: return "&0";
            case RED: return "&c";
            case AQUA: return "&b";
            case BLUE: return "&9";
            case GOLD: return "&6";
            case GRAY: return "&7";
            case WHITE: return "&f";
            case GREEN: return "&a";
            case YELLOW: return "&e";
            case DARK_RED: return "&4";
            case DARK_AQUA: return "&3";
            case DARK_BLUE: return "&1";
            case DARK_GRAY: return "&8";
            case DARK_GREEN: return "&2";
            case DARK_PURPLE: return "&5";
            case LIGHT_PURPLE: return "&d";
            default: return "";
        }


    }

    private enum colour{
        BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE
    }

}