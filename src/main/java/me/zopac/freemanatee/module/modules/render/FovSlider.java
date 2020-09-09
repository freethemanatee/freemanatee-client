package me.zopac.freemanatee.module.modules.render;

import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;

@Module.Info(name = "CustomFOV", category=Module.Category.RENDER)
public class FovSlider extends Module {

    private Setting<Integer> custom_fov = register(Settings.integerBuilder("FOV").withMinimum(0).withValue(110).withMaximum(180));

    public static float old_fov = mc.gameSettings.fovSetting;

    @Override
    public void onDisable() {
        mc.gameSettings.fovSetting = old_fov;
    }

    @Override
    public void onUpdate() {
        mc.gameSettings.fovSetting = custom_fov.getValue();
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(custom_fov.getValue());
    }
}