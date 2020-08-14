package me.zopac.freemanatee.module.modules.misc;

import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "AntiWeather",
        category = Module.Category.MISC
)
public class AntiWeather extends Module {

    @Override
    public void onUpdate() {
        if (isDisabled()) return;
        if (mc.world.isRaining())
            mc.world.setRainStrength(0);
    }
}