package me.zopac.freemanatee.module.modules.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "VisualRange", category = Module.Category.CHAT)
public class VisualRange extends Module {

    private Setting<Boolean> publicChat = register(Settings.b("Public Chat", false));
    private Setting<Boolean> leaving = register(Settings.b("Leaving", false));

    private List<String> knownPlayers;

    @Override
    public void onUpdate() {

        if (mc.player == null) {
            return;
        }

        List<String> tickPlayerList = new ArrayList<>();

        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer) {
                tickPlayerList.add(entity.getName());
            }
        }

        if (tickPlayerList.size() > 0) {
            for (String playerName : tickPlayerList) {
                if (playerName.equals(mc.player.getName())) {
                    continue;
                }
                if (!knownPlayers.contains(playerName)) {
                    knownPlayers.add(playerName);
                    if (publicChat.getValue()) {
                        mc.player.connection.sendPacket(new CPacketChatMessage(playerName + " entered visual range "));

                    } else {
                        if (Friends.isFriend(playerName)) {
                            sendNotification("[VisualRange] " + ChatFormatting.AQUA.toString() + playerName + ChatFormatting.RESET.toString() + " entered visual range ");
                        } else {
                            sendNotification("[VisualRange] " + ChatFormatting.RED.toString() + playerName + ChatFormatting.RESET.toString() + " entered visual range ");
                        }
                    }
                    return;
                }
            }
        }

        if (knownPlayers.size() > 0) {
            for (String playerName : knownPlayers) {
                if (!tickPlayerList.contains(playerName)) {
                    knownPlayers.remove(playerName);
                    if (leaving.getValue()) {
                        if (publicChat.getValue()) {
                            mc.player.connection.sendPacket(new CPacketChatMessage(playerName + " just left visual range "));
                        } else {
                            if (Friends.isFriend(playerName)) {
                                sendNotification("[VisualRange] " + ChatFormatting.AQUA.toString() + playerName + ChatFormatting.RESET.toString() + " just left visual range ");
                            } else {
                                sendNotification("[VisualRange] " + ChatFormatting.RED.toString() + playerName + ChatFormatting.RESET.toString() + " just left visual range ");
                            }
                        }
                    }
                    return;
                }
            }
        }

    }

    private void sendNotification(String s) {
        Command.sendChatMessage(s);
    }

    @Override
    public void onEnable() {
        this.knownPlayers = new ArrayList<>();
    }

}
