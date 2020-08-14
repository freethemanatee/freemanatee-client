package me.zopac.freemanatee.module.modules.misc;

import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;


@Module.Info(
        name = "HotbarReplenish",
        category = Module.Category.COMBAT
)

public class HotbarReplenish extends Module {

    private Setting<Integer> threshold = register(Settings.integerBuilder("Threshold").withMinimum(1).withValue(32).withMaximum(63).build());
    private Setting<Integer> tickDelay = register(Settings.integerBuilder("TickDelay").withMinimum(1).withValue(2).withMaximum(10).build());

    private int delayStep = 0;

    private static Map<Integer, ItemStack> getInventory() {
        return getInventorySlots(9, 35);
    }

    private static Map<Integer, ItemStack> getHotbar() {
        return getInventorySlots(36, 44);
    }

    private static Map<Integer, ItemStack> getInventorySlots(int current, int last) {

        Map<Integer, ItemStack> fullInventorySlots = new HashMap<>();

        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            current++;
        }

        return fullInventorySlots;

    }

    @Override
    public void onUpdate() {

        if (mc.player == null) {
            return;
        }

        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }

        if (delayStep < tickDelay.getValue()) {
            delayStep++;
            return;
        } else {
            delayStep = 0;
        }

        Pair<Integer, Integer> slots = findReplenishableHotbarSlot();

        if (slots == null) {
            return;
        }

        int inventorySlot = slots.getKey();
        int hotbarSlot = slots.getValue();

        // pick up inventory slot
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);

        // click on hotbar slot
        mc.playerController.windowClick(0, hotbarSlot, 0, ClickType.PICKUP, mc.player);

        // put back inventory slot
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);

    }

    private Pair<Integer, Integer> findReplenishableHotbarSlot() {

        Pair<Integer, Integer> returnPair = null;

        for (Map.Entry<Integer, ItemStack> hotbarSlot : getHotbar().entrySet()) {

            ItemStack stack = hotbarSlot.getValue();

            if (stack.isEmpty || stack.getItem() == Items.AIR) {
                continue;
            }

            if (!stack.isStackable()) {
                continue;
            }

            if (stack.stackSize >= stack.getMaxStackSize()) {
                continue;
            }

            if (stack.stackSize > threshold.getValue()) {
                continue;
            }

            int inventorySlot = findCompatibleInventorySlot(stack);

            if (inventorySlot == -1) {
                continue;
            }

            returnPair = new Pair<>(inventorySlot, hotbarSlot.getKey());

        }

        return returnPair;

    }

    /**
     * Find a compatible inventory slot, holding a stack mergable with the hotbarStack.
     *
     * @param hotbarStack the hotbar slot id that a inventory slot with compatible stack is searched for
     * @return inventory slot id holding compatible stack, otherwise -1
     */
    private int findCompatibleInventorySlot(ItemStack hotbarStack) {

        int inventorySlot = -1;
        int smallestStackSize = 999;

        for (Map.Entry<Integer, ItemStack> entry : getInventory().entrySet()) {

            ItemStack inventoryStack = entry.getValue();

            if (inventoryStack.isEmpty || inventoryStack.getItem() == Items.AIR) {
                continue;
            }

            if (!isCompatibleStacks(hotbarStack, inventoryStack)) {
                continue;
            }

            int currentStackSize = mc.player.inventoryContainer.getInventory().get(entry.getKey()).stackSize;

            if (smallestStackSize > currentStackSize) {
                smallestStackSize = currentStackSize;
                inventorySlot = entry.getKey();
            }

        }

        return inventorySlot;

    }

    private boolean isCompatibleStacks(ItemStack stack1, ItemStack stack2) {

        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }

        if ((stack1.getItem() instanceof ItemBlock) && (stack2.getItem() instanceof ItemBlock)) {
            Block block1 = ((ItemBlock) stack1.getItem()).getBlock();
            Block block2 = ((ItemBlock) stack2.getItem()).getBlock();
            if (!block1.material.equals(block2.material)) {
                return false;
            }
        }

        if (!stack1.getDisplayName().equals(stack2.getDisplayName())) {
            return false;
        }

        if (stack1.getItemDamage() != stack2.getItemDamage()) {
            return false;
        }

        return true;

    }





}
