package me.zopac.freemanatee.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.event.events.GuiScreenEvent;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.client.gui.GuiGameOver;

@Module.Info(
        name = "AutoRespawn",
        category = Module.Category.MISC
)
public class AutoRespawn extends Module {

    private Setting<Boolean> autoRespawn = register(Settings.b("Auto Respawn", true));
    private Setting<Boolean> deathCoords = register(Settings.b("Death Coords", false));
    private Setting<Boolean> antiBug = register(Settings.b("Anti Bug", true));

    @EventHandler
    public Listener<GuiScreenEvent.Displayed> listener = new Listener<>(event -> {

        if (!(event.getScreen() instanceof GuiGameOver)) {
            return;
        }

        if (deathCoords.getValue() && mc.player.getHealth() <= 0) {
            Command.sendChatMessage(String.format("You died at x %d y %d z %d", (int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ));
        }

        if (autoRespawn.getValue() || (antiBug.getValue() && mc.player.getHealth() > 0)) {
            mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }

    });

}
