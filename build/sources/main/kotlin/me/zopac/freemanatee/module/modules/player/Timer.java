package me.zopac.freemanatee.module.modules.player;

import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;

@Module.Info(name = "Timer", category = Module.Category.PLAYER)
public class Timer extends Module{
    private Setting<Float> speed = register(Settings.f("Speed", 4.2f));

    @Override
    protected void onDisable() {
        super.onDisable();
        mc.timer.tickLength = 50;
    }

    @Override
    public void onUpdate() {
        mc.timer.tickLength = 50.0f / speed.getValue();
    }
}
