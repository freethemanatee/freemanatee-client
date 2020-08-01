package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Module.Info(name = "Custom FOV", category = Module.Category.RENDER)
public class FovSlider extends Module {
    private Setting<Integer> fov = register(Settings.integerBuilder("Fov").withMinimum(0).withValue(135).withMaximum(180).build());

    public float old_fov;
    public float new_fov;

    @Override
    public void onDisable() {
        mc.gameSettings.fovSetting = old_fov;
    }

    @Override
    public void onUpdate(){
        new_fov = (float) fov.getValue();

        mc.gameSettings.fovSetting = new_fov;
    }
}