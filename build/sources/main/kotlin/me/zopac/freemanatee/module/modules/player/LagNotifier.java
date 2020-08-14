package me.zopac.freemanatee.module.modules.player;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.event.events.PacketEvent;
import me.zopac.freemanatee.gui.rgui.render.font.FontRenderer;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.Wrapper;

import static me.zopac.freemanatee.gui.kami.DisplayGuiScreen.getScale;
import static me.zopac.freemanatee.util.InfoCalculator.round;

@Module.Info(name = "LagNotifier", description = "Displays a warning when the server is lagging", category = Module.Category.PLAYER)
public class LagNotifier extends Module {
    private Setting<Double> timeout = register(Settings.doubleBuilder().withName("Timeout").withValue(1.0).withMinimum(0.0).withMaximum(10.0).build());
    private long serverLastUpdated;

    @Override
    public void onRender() {
        if (!(timeout.getValue() * 1000L <= System.currentTimeMillis() - serverLastUpdated)) return;
        String text = "Server Not Responding! " + timeDifference() + "s";
        FontRenderer renderer = Wrapper.getFontRenderer();

        int divider = getScale();
        /* 217 is the offset to make it go high, bigger = higher, with 0 being center */
        renderer.drawStringWithShadow(mc.displayWidth / divider / 2 - renderer.getStringWidth(text) / 2, mc.displayHeight / divider / 2 - 217, 255, 85, 85, text);
    }

    @EventHandler
    private Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> serverLastUpdated = System.currentTimeMillis());

    private double timeDifference() {
        return round((System.currentTimeMillis() - serverLastUpdated) / 1000d, 1);
    }
}
