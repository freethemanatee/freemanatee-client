/*
 * Decompiled with CFR <Could not determine version>.
 *
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 */
package me.zeroeightsix.kami.module.modules.render;

import java.awt.Color;
import java.awt.Font;
 import net.minecraft.client.Minecraft;
import       net.minecraft.init.Items;
import        net.minecraft.item.ItemStack;
import me.zeroeightsix.kami.gui.font.CFontRenderer;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.ColourUtils;

@Module.Info(name="PvPInfo", category=Module.Category.RENDER)
public class PvPInfo extends Module {

    private Setting<Float> x = this.register(Settings.f("InfoX", 0.0f));
    private Setting<Float> y = this.register(Settings.f("InfoY", 200.0f));
    private Setting<Boolean> rainbow = this.register(Settings.b("Rainbow", false));
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
        int totems = PvPInfo.mc.player.inventory.mainInventory.stream().filter(itemStack -> {
            if (itemStack.getItem() != Items.TOTEM_OF_UNDYING) return false;
            return true;
        }).mapToInt(ItemStack::getCount).sum();
        if (PvPInfo.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            ++totems;
        }
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
            this.cFontRenderer.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), this.x.getValue().floatValue(), yCount - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            this.cFontRenderer.drawStringWithShadow("PING: " + (mc.getCurrentServerData() != null ? Long.valueOf(PvPInfo.mc.getCurrentServerData().pingToServer) : "0"), this.x.getValue().floatValue(), (yCount += 10.0f) - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            this.cFontRenderer.drawStringWithShadow("AT: " + this.getAutoTrap(), this.x.getValue().floatValue(), (yCount += 10.0f) - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            this.cFontRenderer.drawStringWithShadow("HF: " + this.getHoleFiller(), this.x.getValue().floatValue(), (yCount += 10.0f) - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            this.cFontRenderer.drawStringWithShadow("SU: " + this.getSurround(), this.x.getValue().floatValue(), (yCount += 10.0f) - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            this.cFontRenderer.drawStringWithShadow("CA: " + this.getCA(), this.x.getValue().floatValue(), (yCount += 10.0f) - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            this.cFontRenderer.drawStringWithShadow("CA2: " + this.getCA2(), this.x.getValue().floatValue(), (yCount += 10.0f) - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            this.cFontRenderer.drawStringWithShadow("KA: " + this.getKA(), this.x.getValue().floatValue(), (yCount += 10.0f) - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            this.cFontRenderer.drawStringWithShadow("HP: " + this.getHP(), this.x.getValue().floatValue(), (yCount += 10.0f) - (float) this.cFontRenderer.getHeight() - 1.0f, color);
            return;
        }
    }

    private String getAutoTrap() {
        String x = "OFF";
        if (ModuleManager.getModuleByName("AutoTrap") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("AutoTrap").isEnabled()).toUpperCase();
    }

    private String getSurround() {
        String x = "OFF";
        if (ModuleManager.getModuleByName("Surround") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("Surround").isEnabled()).toUpperCase();
    }

    private String getCA() {
        String x = "OFF";
        if (ModuleManager.getModuleByName("AutoCrystal") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("AutoCrystal").isEnabled()).toUpperCase();
    }

    private String getCA2() {
        String x = "OFF";
        if (ModuleManager.getModuleByName("AutoCrystal2") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("AutoCrystal2").isEnabled()).toUpperCase();
    }

    private String getKA() {
        String x = "OFF";
        if (ModuleManager.getModuleByName("Aura") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("Aura").isEnabled()).toUpperCase();
    }

    private String getHP() {
        String x = "OFF";
        if (ModuleManager.getModuleByName("HopperNuker") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("HopperNuker").isEnabled()).toUpperCase();
    }

    private String getHoleFiller() {
        String x = "OFF";
        if (ModuleManager.getModuleByName("HoleFiller") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("HoleFiller").isEnabled()).toUpperCase();
    }
}

