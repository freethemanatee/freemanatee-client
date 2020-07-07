package me.zeroeightsix.kami.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 26 October 2019 by hub
 * Updated 23 November 2019 by hub
 */
@Module.Info(name = "VisualRange", description = "Reports Players in VisualRange", category = Module.Category.MISC)
public class VisualRange extends Module {

    private Setting<Boolean> publicChat = register(Settings.b("PublicChat", false));
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
                        mc.player.connection.sendPacket(new CPacketChatMessage("Oh hey, there is " + playerName + " in my range! This announcement was presented by: " + KamiMod.NAME_UNICODE));
                    } else {
                        if (Friends.isFriend(playerName)) {
                            sendNotification("[VisualRange] " + ChatFormatting.GREEN.toString() + playerName + ChatFormatting.RESET.toString() + " entered the Battlefield!");
                        } else {
                            sendNotification("[VisualRange] " + ChatFormatting.RED.toString() + playerName + ChatFormatting.RESET.toString() + " entered the Battlefield!");
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
                            mc.player.connection.sendPacket(new CPacketChatMessage("I cant see " + playerName + " anymore! This announcement was presented by: " + KamiMod.NAME_UNICODE));
                        } else {
                            if (Friends.isFriend(playerName)) {
                                sendNotification("[VisualRange] " + ChatFormatting.GREEN.toString() + playerName + ChatFormatting.RESET.toString() + " left the Battlefield!");
                            } else {
                                sendNotification("[VisualRange] " + ChatFormatting.RED.toString() + playerName + ChatFormatting.RESET.toString() + " left the Battlefield!");
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
