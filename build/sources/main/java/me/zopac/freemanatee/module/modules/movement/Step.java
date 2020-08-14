
package me.zopac.freemanatee.module.modules.movement;

import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;

import java.text.DecimalFormat;

@Module.Info(name = "Step", category = Module.Category.MOVEMENT)
public class Step extends Module {


    private Setting<Float> stepHeightSetting = register(Settings.floatBuilder("Step Height").withMinimum((float) 0).withMaximum((float) 6).withValue((float) 1).build());


    public void onDisable() {
        mc.player.stepHeight = 0.5F;
    }


    @Override

    public void onUpdate() {
        DecimalFormat df = new DecimalFormat("#");
        mc.player.stepHeight = Float.parseFloat(df.format(stepHeightSetting.getValue()));
    }
}