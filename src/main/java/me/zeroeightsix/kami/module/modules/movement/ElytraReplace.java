package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

@Module.Info(name = "ElytraReplace", category = Module.Category.MOVEMENT)
public class ElytraReplace extends Module {
    private Setting<InventoryMode> inventoryMode = register(Settings.e("Inventory", InventoryMode.ON));

    private boolean currentlyMovingElytra = false;
    private boolean currentlyMovingChestplate = false;

    private int elytraCount;
    private int chestplateCount;

    private enum InventoryMode { ON, OFF }

    @Override
    public void onUpdate() {

        if (inventoryMode.getValue().equals(InventoryMode.OFF) && mc.currentScreen instanceof GuiContainer) {
            return;
        }

        elytraCount = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.ELYTRA).mapToInt(ItemStack::getCount).sum();
        chestplateCount = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.ELYTRA).mapToInt(ItemStack::getCount).sum();

        if (currentlyMovingElytra) {
            mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
            currentlyMovingElytra = false;

            return;
        }

        if (currentlyMovingChestplate) {
            mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
            currentlyMovingChestplate = false;

            return;
        }

        if (onGround()) {
            int slot = -420;

            if (chestplateCount == 0) {
                return;
            }

            if (mc.player.inventory.armorInventory.get(2).isEmpty()) {
                for (int i = 0; i < 45; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                        slot = i;

                        break;
                    }
                }

                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
                currentlyMovingElytra = true;

                return;
            }

            if (!(mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA)) {
                for (int i = 0; i < 45; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                        slot = i;

                        break;
                    }
                }

                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);

                return;
            }
        }


            int slot = -420;

            if (elytraCount == 0) {
                return;
            }

            if (mc.player.inventory.armorInventory.get(2).isEmpty()) {
                for (int i = 0; i < 45; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                        slot = i;

                        break;
                    }
                }

                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
                currentlyMovingElytra = true;

                return;
            }

            if (!(mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA)) {
                for (int i = 0; i < 45; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                        slot = i;

                        break;
                    }
                }

                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
            }
        }


    @Override
    public String getHudInfo() {
            return "\u00A77[\u00A7f" + elytraCount + "\u00A77]";
        }


    private boolean onGround() {
        return mc.player.onGround;
    }

    }
