package me.zopac.freemanatee.module.modules.chat;

import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;

@Module.Info(name = "AntiKick", category = Module.Category.CHAT)
public class AntiKick extends Module {

    private final Timer timer = new Timer();
    private Setting<Boolean> info = register(Settings.b("Info Messages", true));

    private int delay = 60-360;

    @Override
    public void onUpdate() {

        if (!shouldSendMessage(mc.player)) {
            return;
        }

        if (info.getValue()) {
            Command.sendChatMessage("[AntiKick] Sending message: /stats");
        }

        mc.player.sendChatMessage("/stats");

        timer.reset();

    }

    private boolean shouldSendMessage(EntityPlayer player) {

        if (!timer.passed(delay * 1000)) {
            return false;
        }

        return player.getPosition().equals(new Vec3i(0, 240, 0));

    }

    public static final class Timer {

        private long time;

        Timer() {
            time = -1;
        }

        boolean passed(double ms) {
            return System.currentTimeMillis() - time >= ms;
        }

        public void reset() {
            time = System.currentTimeMillis();
        }

    }

}
