package me.zeroeightsix.kami.util;

import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import scala.reflect.api.Mirrors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageSendHelper {
    public static void sendChatMessage(String message) {
        sendRawChatMessage("&7[&9" + KamiMod.NAME_UNICODE + "&7] &r" + message);
    }

    public static void sendWarningMessage(String message) {
        sendRawChatMessage("&7[&6" + KamiMod.NAME_UNICODE + "&7] &r" + message);
    }

    public static void sendErrorMessage(String message) {
        sendRawChatMessage("&7[&4" + KamiMod.NAME_UNICODE + "&7] &r" + message);
    }

    public static void sendDisableMessage(String message) {
        sendRawChatMessage("&7[&4" + KamiMod.NAME_UNICODE + "&7] &r" + message);
    }

    public static void sendCustomMessage(String message, String colour) {
        sendRawChatMessage("&7[" + colour + KamiMod.NAME_UNICODE + "&7] &r" + message);
    }

    public static void sendStringChatMessage(String[] messages) {
        sendChatMessage("");
        for (String s : messages) sendRawChatMessage(s);
    }

    public static void sendRawChatMessage(String message) {
        if (Minecraft.getMinecraft().player != null) {
            Wrapper.getPlayer().sendMessage(new ChatMessage(message));
        } else {
            LogWrapper.info(message);
        }
    }

    public static void sendServerMessage(String message) {
        if (Minecraft.getMinecraft().player != null) {
            Wrapper.getPlayer().connection.sendPacket(new CPacketChatMessage(message));
        } else {
            LogWrapper.warning("Could not send server message: \"" + message + "\"");
        }
    }

    public static class ChatMessage extends TextComponentBase {

        String text;

        ChatMessage(String text) {

            Pattern p = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher m = p.matcher(text);
            StringBuffer sb = new StringBuffer();

            while (m.find()) {
                String replacement = "\u00A7" + m.group().substring(1);
                m.appendReplacement(sb, replacement);
            }

            m.appendTail(sb);

            this.text = sb.toString();
        }

        public String getUnformattedComponentText() {
            return text;
        }

        @Override
        public ITextComponent createCopy() {
            return new ChatMessage(text);
        }

    }
}