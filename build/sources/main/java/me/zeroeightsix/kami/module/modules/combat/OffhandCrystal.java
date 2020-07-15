

package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiContainer;
import me.zeroeightsix.kami.module.Module;


@Module.Info(name = "Offhand CA", category = Module.Category.COMBAT)
public class OffhandCrystal extends Module
{
    int crystals;
    boolean moving;
    boolean returnI;

    private Setting<Boolean> totemdisable = register(Settings.b("AutototemOnDisable", true));

    @Override
    public void onEnable() {

        if (mc.world == null)
            return;

        if (this.totemdisable.getValue()) {
            ModuleManager.getModuleByName("autototem").disable();
        }


    }

    public OffhandCrystal() {
        this.moving = false;
        this.returnI = false;
    }

    @Override
    public void onUpdate() {
        if (OffhandCrystal.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.returnI) {
            int t = -1;
            for (int i = 0; i < 45; ++i) {
                if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).isEmpty) {
                    t = i;
                    break;
                }
            }
            if (t == -1) {
                return;
            }
            OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer) OffhandCrystal.mc.player);
            this.returnI = false;
        }
        this.crystals = OffhandCrystal.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            ++this.crystals;
        }
        else {
            if (OffhandCrystal.mc.player.getHealth() + OffhandCrystal.mc.player.getAbsorptionAmount() <= AutoTotem.health()) {
                return;
            }
            if (this.moving) {
                OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer) OffhandCrystal.mc.player);
                this.moving = false;
                if (!OffhandCrystal.mc.player.inventory.itemStack.isEmpty()) {
                    this.returnI = true;
                }
                return;
            }
            if (OffhandCrystal.mc.player.inventory.itemStack.isEmpty()) {
                if (this.crystals == 0) {
                    return;
                }
                int t = -1;
                for (int i = 0; i < 45; ++i) {
                    if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                        t = i;
                        break;
                    }
                }
                if (t == -1) {
                    return;
                }
                OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer) OffhandCrystal.mc.player);
                this.moving = true;
            }
            else {
                int t = -1;
                for (int i = 0; i < 45; ++i) {
                    if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).isEmpty) {
                        t = i;
                        break;
                    }
                }
                if (t == -1) {
                    return;
                }
                OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer) OffhandCrystal.mc.player);
            }
        }
    }

    public void onDisable() {

        if (totemdisable.getValue()) {
            ModuleManager.getModuleByName("autototem").enable();
        }

        if (OffhandCrystal.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        this.crystals = OffhandCrystal.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (OffhandCrystal.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
            if (this.crystals == 0) {
                return;
            }
            int t = -1;
            for (int i = 0; i < 45; ++i) {
                if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    t = i;
                    break;
                }
            }
            if (t == -1) {
                return;
            }
            OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer) OffhandCrystal.mc.player);
            OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer) OffhandCrystal.mc.player);
            OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer) OffhandCrystal.mc.player);
        }
    }
}