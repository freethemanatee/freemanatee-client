package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.gui.font.CFontRenderer;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Wrapper;
import java.awt.Font;
import net.minecraft.item.ItemStack;

@Module.Info(name = "Armor Warning", category = Module.Category.RENDER)

public class ArmorWarning extends Module {
    private Setting threshold = this.register(Settings.integerBuilder("Warning Threshold").withMinimum(5).withValue((int)25).withMaximum(100).build());
    CFontRenderer ff = new CFontRenderer(new Font("Arial", 0, 18), true, false);

    public void onRender() {
        if (this.shouldMend(0) || this.shouldMend(1) || this.shouldMend(2) || this.shouldMend(3)) {
            String text = "Armor is below " + this.threshold.getValue() + "%!";
            int divider = getScale();
            this.ff.drawStringWithShadow(text, (double)((float)mc.displayWidth / (float)divider / 2.0F - (float)(this.ff.getStringWidth(text) / 2)), (double)((float)mc.displayHeight / (float)divider / 2.0F - 16.0F), 15748422);
        }
    }

    private boolean shouldMend(int i) {
        if (((ItemStack)mc.player.inventory.armorInventory.get(i)).getMaxDamage() == 0) {
            return false;
        } else {
            return 100 * ((ItemStack)mc.player.inventory.armorInventory.get(i)).getItemDamage() / ((ItemStack)mc.player.inventory.armorInventory.get(i)).getMaxDamage() > reverseNumber((Integer)this.threshold.getValue(), 1, 100);
        }
    }

    public static int reverseNumber(int num, int min, int max) {
        return max + min - num;
    }

    public static int getScale() {
        int scaleFactor = 0;
        int scale = Wrapper.getMinecraft().gameSettings.guiScale;
        if (scale == 0) {
            scale = 1000;
        }

        while(scaleFactor < scale && Wrapper.getMinecraft().displayWidth / (scaleFactor + 1) >= 320 && Wrapper.getMinecraft().displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        if (scaleFactor == 0) {
            scaleFactor = 1;
        }

        return scaleFactor;
    }
}