package me.zeroeightsix.kami.module.modules.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Arc2D;

import me.zeroeightsix.kami.gui.font.CFontRenderer;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.ColourUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;


@Module.Info(name ="Pvp Info", category = Module.Category.RENDER)
public class CombatInfo extends Module {
    private Setting<Float> x = this.register(Settings.f("Info X", 0.0f));
    private Setting<Float> y = this.register(Settings.f("Info Y", 200.0f));
    private Setting<Boolean> rainbow = this.register(Settings.b("Rainbow", false));
    private Setting<Boolean> arial = this.register(Settings.b("Arial Font", false));
    private Setting<Integer> red = this.register(Settings.integerBuilder("Red").withRange(0, 255).withValue(255).build());
    private Setting<Integer> green = this.register(Settings.integerBuilder("Green").withRange(0, 255).withValue(255).build());
    private Setting<Integer> blue = this.register(Settings.integerBuilder("Blue").withRange(0, 255).withValue(255).build());

    CFontRenderer cFontRenderer = new CFontRenderer(new Font("Arial", 0, 18), true, false);

    @Override
    public void onRender() {
        int drgb;
        float yCount = this.y.getValue().floatValue();
        int ared = this.red.getValue();
        int bgreen = this.green.getValue();
        int cblue = this.blue.getValue();
        int color = drgb = ColourUtils.toRGBA(ared, bgreen, cblue, 255);

        if (this.rainbow.getValue().booleanValue()) {
            int argb;
            float[] hue = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0f};
            int rgb = Color.HSBtoRGB(hue[0], 1.0f, 1.0f);
            int red = rgb >> 16 & 255;
            int green = rgb >> 8 & 255;
            int blue = rgb & 255;
            color = argb = ColourUtils.toRGBA(red, green, blue, 255);
        }
        {
            if(this.arial.getValue() == false) {
                mc.fontRenderer.drawStringWithShadow("CA: " + this.getCaura(), x.getValue(), y.getValue(), color);
                mc.fontRenderer.drawStringWithShadow("32k Aura: " + this.get32K(), x.getValue(), y.getValue() -10f, color);
                mc.fontRenderer.drawStringWithShadow("KA: " + this.getKA(), x.getValue(), y.getValue() -20f, color);
            } else if(this.arial.getValue() == true) {
                this.cFontRenderer.drawStringWithShadow("CA: " + this.getCaura(), this.x.getValue().floatValue(), (yCount) - (float) this.cFontRenderer.getHeight(), color);
                this.cFontRenderer.drawStringWithShadow("32k Aura: " + this.get32K(), this.x.getValue().floatValue(), (yCount - 10.0f) - (float) this.cFontRenderer.getHeight(), color);
                this.cFontRenderer.drawStringWithShadow("KA: " + this.getKA(), this.x.getValue().floatValue(), (yCount - 20.0f) - (float) this.cFontRenderer.getHeight(), color);
            }

            return;
        }
    }

    private String getCaura() {
        String x = "OFF";
        Module aura = ModuleManager.getModuleByName("autocrystal");
        if (aura == null) return x;
        if(aura.isEnabled()) {
            x = "ON";
        } else {
            x = "OFF";
        }
        return x;
    }

    private String getKA() {
        String x = "OFF";
        Module aura = ModuleManager.getModuleByName("RegAura");
        if (aura == null) return x;
        if(aura.isEnabled()) {
            x = "ON";
        } else {
            x = "OFF";
        }
        return x;
    }

    private String get32K() {
        String x = "OFF";
        Module aura = ModuleManager.getModuleByName("ppaura");
        if (aura == null) return x;
        if(aura.isEnabled()) {
            x = "ON";
        } else {
            x = "OFF";
        }
        return x;
    }
}