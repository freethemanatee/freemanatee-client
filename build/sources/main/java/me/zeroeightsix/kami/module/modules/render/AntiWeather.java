package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "AntiWeather", description = "Removes rain from your world", category = Module.Category.RENDER)
public class AntiWeather extends Module {

    @Override
    public void onUpdate() {
        if (isDisabled()) return;
        if (mc.world.isRaining())
            mc.world.setRainStrength(0);
    }
}
