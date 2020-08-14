package me.zopac.freemanatee.module.modules.misc;

import me.zopac.freemanatee.module.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;

@Module.Info(name = "EchestBP", category = Module.Category.MISC)
public class ShulkerBP extends Module {

    private GuiScreen shulkerScreen = null;

    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) {
            Container container = ((GuiContainer)mc.currentScreen).inventorySlots;
            if (container instanceof ContainerChest && ((ContainerChest)container).getLowerChestInventory() instanceof InventoryBasic) {
                InventoryBasic basic = (InventoryBasic)((ContainerChest)container).getLowerChestInventory();
                if (basic.getName().equalsIgnoreCase("Shulker Box")) {
                    this.shulkerScreen = mc.currentScreen;
                    mc.currentScreen = null;
                }
            }
        }
    }

    public void onDisable() {
        if (this.shulkerScreen != null)
            mc.displayGuiScreen(this.shulkerScreen);
        this.shulkerScreen = null;
    }
}
