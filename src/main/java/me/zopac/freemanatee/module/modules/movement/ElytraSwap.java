package me.zopac.freemanatee.module.modules.movement;

import me.zopac.freemanatee.module.*;
import net.minecraft.init.*;
import me.zopac.freemanatee.command.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

@Module.Info(
        name = "ElytraSwap",
        category = Module.Category.MOVEMENT
)

public class ElytraSwap extends Module {

    public void onEnable() {
        if (ElytraSwap.mc.player != null) {
            final InventoryPlayer items = ElytraSwap.mc.player.inventory;
            final ItemStack body = items.armorItemInSlot(2);
            final String body2 = body.getItem().getItemStackDisplayName(body);
            if (body2.equals("Air")) {
                int t = 0;
                int c = 0;
                for (int i = 9; i < 45; ++i) {
                    if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                        t = i;
                        break;
                    }
                }
                if (t != 0) {
                    Command.sendChatMessage("Equipping Elytra");
                    ElytraSwap.mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                }
                if (t == 0) {
                    for (int i = 9; i < 45; ++i) {
                        if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_CHESTPLATE) {
                            c = i;
                            break;
                        }
                    }
                    if (c != 0) {
                        Command.sendChatMessage("Equipping Chestplate");
                        ElytraSwap.mc.playerController.windowClick(0, c, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                        ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                    }
                }
                if (c == 0 && t == 0) {
                    Command.sendChatMessage("You do not have an Elytra or a Chestplate in your inventory. Disabling");
                }
                this.disable();
            }
            if (body2.equals("Elytra")) {
                int t = 0;
                for (int j = 9; j < 45; ++j) {
                    if (ElytraSwap.mc.player.inventory.getStackInSlot(j).getItem() == Items.DIAMOND_CHESTPLATE) {
                        t = j;
                        break;
                    }
                }
                if (t != 0) {
                    int l = 0;
                    Command.sendChatMessage("Equipping Chestplate");
                    ElytraSwap.mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                    for (int i = 9; i < 45; ++i) {
                        if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() == Items.AIR) {
                            l = i;
                            break;
                        }
                    }
                    ElytraSwap.mc.playerController.windowClick(0, l, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                }
                if (t == 0) {
                    Command.sendChatMessage("You do not have a Chestplate in your inventory. Keeping Elytra equipped");
                }
                this.disable();
            }
            if (body2.equals("Diamond Chestplate")) {
                int t = 0;
                for (int j = 9; j < 45; ++j) {
                    if (ElytraSwap.mc.player.inventory.getStackInSlot(j).getItem() == Items.ELYTRA) {
                        t = j;
                        break;
                    }
                }
                if (t != 0) {
                    int u = 0;
                    Command.sendChatMessage("Equipping Elytra");
                    ElytraSwap.mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                    for (int i = 9; i < 45; ++i) {
                        if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() == Items.AIR) {
                            u = i;
                            break;
                        }
                    }
                    ElytraSwap.mc.playerController.windowClick(0, u, 0, ClickType.PICKUP, (EntityPlayer)ElytraSwap.mc.player);
                }
                if (t == 0) {
                    Command.sendChatMessage("You do not have a Elytra in your inventory. Keeping Chestplate equipped");
                }
                this.disable();
            }
        }
    }
}
