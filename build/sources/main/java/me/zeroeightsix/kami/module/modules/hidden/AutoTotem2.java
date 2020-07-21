package me.zeroeightsix.kami.module.modules.hidden;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;


@Module.Info(
        name = "AutoTotemDev",
        category = Module.Category.HIDDEN
)
public class AutoTotem2 extends Module {

    int totems;
    boolean moving = false;
    boolean returnI = false;
    private Setting<Boolean> soft = register(Settings.b("Soft"));
    private static AutoTotem2 INSTANCE = new AutoTotem2();
    private Setting<Double> health = this.register(Settings.d("Health", 11.0));

    public AutoTotem2() {
        INSTANCE = this;
    }
    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (returnI) {
            int t = -1;
            for (int i = 0; i < 45; i++) {
                if (mc.player.inventory.getStackInSlot(i).isEmpty) {
                    t = i;
                    break;
                }
            }
            if (t == -1) {
                return;
            }
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            returnI = false;
        }
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems++;
        } else {
            if (this.soft.getValue().booleanValue() && !AutoTotem2.mc.player.getHeldItemOffhand().isEmpty && (double)(AutoTotem2.mc.player.getHealth() + AutoTotem2.mc.player.getAbsorptionAmount()) >= this.health.getValue()) {
                return;
            }
        }
        if (moving) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            moving = false;
            if (!mc.player.inventory.itemStack.isEmpty()) {
                returnI = true;
            }
            return;
        }
        if (mc.player.inventory.itemStack.isEmpty()) {
            if (totems == 0) {
                return;
            }
            int t = -1;
            for (int i = 0; i < 45; i++) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    t = i;
                    break;
                }
            }
            if (t == -1) {
                return; // Should never happen!
            }
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            moving = true;
        } else if (!soft.getValue()) {
            int t = -1;
            for (int i = 0; i < 45; i++) {
                if (mc.player.inventory.getStackInSlot(i).isEmpty) {
                    t = i;
                    break;
                }
            }
            if (t == -1) {
                return;
            }
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
        }
    }


    public void disableSoft() {
        soft.setValue(false);
    }
    public static double health() {
        return AutoTotem2.INSTANCE.health.getValue();

    }
    @Override
    public String getHudInfo() {
        return String.valueOf(totems);
    }

}