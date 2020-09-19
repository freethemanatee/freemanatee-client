package me.zopac.freemanatee.module.modules.render;

import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.ColourUtils;
import net.minecraft.util.math.MathHelper;

@Module.Info(name="Compass", category=Module.Category.RENDER)

public class Compass
        extends Module {

    private Setting<Integer> scale = this.register(Settings.integerBuilder("Scale").withMinimum(0).withValue(3).withMaximum(3).build());
    private Setting<Integer> optionX = register(Settings.integerBuilder("X").withMinimum(0).withValue(400).withMaximum(2000).build());
    private Setting<Integer> optionY = register(Settings.integerBuilder("Y").withMinimum(0).withValue(400).withMaximum(2000).build());
    private static final double HALF_PI = 1.5707963267948966;

    @Override
    public void onRender() {
        for (Direction dir : Direction.values()) {
            double rad = Compass.getPosOnCompass(dir);
            Compass.mc.fontRenderer.drawStringWithShadow(dir.name(), (float)((double)this.optionX.getValue().intValue() + this.getX(rad)), (float)((double)this.optionY.getValue().intValue() + this.getY(rad)), dir == Direction.N ? ColourUtils.Colors.RED : ColourUtils.Colors.WHITE);
        }
    }

    private double getX(double rad) {
        return Math.sin(rad) * (double)(this.scale.getValue() * 10);
    }

    private double getY(double rad) {
        double epicPitch = MathHelper.clamp((float)(Compass.mc.player.rotationPitch + 30.0f), (float)-90.0f, (float)90.0f);
        double pitchRadians = Math.toRadians(epicPitch);
        return Math.cos(rad) * Math.sin(pitchRadians) * (double)(this.scale.getValue() * 10);
    }

    private static double getPosOnCompass(Direction dir) {
        double yaw = Math.toRadians(MathHelper.wrapDegrees((float)Compass.mc.player.rotationYaw));
        int index = dir.ordinal();
        return yaw + (double)index * 1.5707963267948966;
    }

    private static enum Direction {
        N,
        W,
        S,
        E;

    }
}

