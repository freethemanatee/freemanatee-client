package me.zopac.freemanatee;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class Discord {
    public static final String APP_ID = "742990575598567485";

    public static DiscordRichPresence presence;

    public static boolean connected;

    public static void start() {
        manatee.log.info("Starting Discord RPC");
        if (connected)
            return;
        connected = true;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("742990575598567485", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        setRpcFromSettings();
        (new Thread(Discord::setRpcFromSettingsNonInt, "Discord-RPC-Callback-Handler")).start();
        manatee.log.info("Discord RPC initialised successfully");
    }

    public static void end() {
        manatee.log.info("Shutting down Discord RPC...");
        connected = false;
        rpc.Discord_Shutdown();
    }

    public static String getIGN() {
        if ((Minecraft.getMinecraft()).player != null)
            return (Minecraft.getMinecraft()).player.getName();
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public static String getIP() {
        if (Minecraft.getMinecraft().getCurrentServerData() != null)
            return (Minecraft.getMinecraft().getCurrentServerData()).serverIP;
        if (Minecraft.getMinecraft().isIntegratedServerRunning())
            return "Singleplayer";
        return "Main Menu";
    }

    private static void setRpcFromSettingsNonInt() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                rpc.Discord_RunCallbacks();
                details = getIGN();
                state = getIP();
                presence.details = details;
                presence.state = state;
                rpc.Discord_UpdatePresence(presence);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                Thread.sleep(4000L);
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        }
    }

    private static void setRpcFromSettings() {
        details = getIGN();
        state = getIP();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.state = "playing block game";
        presence.details = "free him";
        presence.largeImageKey = "manatee_rpc";
        presence.largeImageText = "freemanatee utility mod";
        presence.smallImageKey = "rpc";
        presence.smallImageText = "free him or we gonna have some problems";
        presence.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        presence.partySize = 1;
        presence.partyMax = 69;

    }

    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;

    private static String details;

    private static String state;

    static {
        presence = new DiscordRichPresence();
        connected = false;
    }
}