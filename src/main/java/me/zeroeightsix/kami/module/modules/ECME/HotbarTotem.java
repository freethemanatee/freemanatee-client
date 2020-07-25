package me.zeroeightsix.kami.module.modules.ECME;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;

@Module.Info(
        name = "HotbarTotem",
        category = Module.Category.ECME
)
public class HotbarTotem extends Module {

    public void onUpdate() {
        if (mc.currentScreen == null || !(mc.currentScreen instanceof GuiContainer)) {
            if (mc.player.inventory.getStackInSlot(0).getItem() != Items.TOTEM_OF_UNDYING) {
                for(int i = 9; i < 35; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.SWAP, mc.player);
                        break;
                    }
                }

            }
        }
    }
}