package me.zopac.freemanatee.module.modules.misc;

import java.util.Random;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.entity.player.EnumPlayerModelParts;

@Module.Info(
        name = "SkinFlicker",
        category = Module.Category.MISC
)
public class SkinFlicker extends Module {
    private Setting mode;
    private Setting slowness;
    private static final EnumPlayerModelParts[] PARTS_HORIZONTAL;
    private static final EnumPlayerModelParts[] PARTS_VERTICAL;
    private Random r;
    private int len;
    public SkinFlicker() {
        this.mode = this.register(Settings.e("Mode", SkinFlicker.FlickerMode.HORIZONTAL));
        this.slowness = this.register(Settings.integerBuilder().withName("Slowness").withValue((int)2).withMinimum(1).build());
        this.r = new Random();
        this.len = EnumPlayerModelParts.values().length;
    }
    public void onUpdate() {
        switch((SkinFlicker.FlickerMode)this.mode.getValue()) {
            case RANDOM:
                if (mc.player.ticksExisted % (Integer)this.slowness.getValue() != 0) {
                    return;
                }
                mc.gameSettings.switchModelPartEnabled(EnumPlayerModelParts.values()[this.r.nextInt(this.len)]);
                break;
            case VERTICAL:
            case HORIZONTAL:
                int i = mc.player.ticksExisted / (Integer)this.slowness.getValue() % (PARTS_HORIZONTAL.length * 2);
                boolean on = false;
                if (i >= PARTS_HORIZONTAL.length) {
                    on = true;
                    i -= PARTS_HORIZONTAL.length;
                }
                mc.gameSettings.setModelPartEnabled(this.mode.getValue() == SkinFlicker.FlickerMode.VERTICAL ? PARTS_VERTICAL[i] : PARTS_HORIZONTAL[i], on);
        }
    }
    static {
        PARTS_HORIZONTAL = new EnumPlayerModelParts[]{EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.JACKET, EnumPlayerModelParts.HAT, EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG, EnumPlayerModelParts.RIGHT_SLEEVE};
        PARTS_VERTICAL = new EnumPlayerModelParts[]{EnumPlayerModelParts.HAT, EnumPlayerModelParts.JACKET, EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.RIGHT_SLEEVE, EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG};
    }
    public static enum FlickerMode {
        HORIZONTAL,
        VERTICAL,
        RANDOM
    }
}
