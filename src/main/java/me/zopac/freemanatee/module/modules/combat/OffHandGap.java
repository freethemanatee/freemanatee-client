package me.zopac.freemanatee.module.modules.combat;

import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.Friends;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Info(name = "OffHandGap", category = Module.Category.COMBAT)
public class OffHandGap extends Module {

    private Setting<Integer> health = this.register(Settings.integerBuilder("Health Switch").withRange(1, 36).withValue(16));
    int gapples;
    public void onUpdate() {
        gapples = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (mc.currentScreen instanceof GuiContainer || mc.world == null || mc.player == null)
            return;
        if (!shouldTotem()) {
            if (!(mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE)) {
                final int slot = getGapSlot() < 9 ? getGapSlot() + 36 : getGapSlot();
                if (getGapSlot() != -1) {
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                }
            }
        } else if (!(mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)) {
            final int slot = getTotemSlot() < 9 ? getTotemSlot() + 36 : getTotemSlot();
            if (getTotemSlot() != -1) {
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }
    private boolean nearPlayers() {
        return mc.world.playerEntities.stream().anyMatch(e -> e != mc.player && e.getEntityId() != -1488 && !Friends.isFriend(e.getName()) && mc.player.getDistance(e) <= 200);
    }
    private boolean shouldTotem() {
        if (mc.player != null) {
            return (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA || !nearPlayers() || mc.player.getHealth() + mc.player.getAbsorptionAmount() <= health.getValue());
        }
        return (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= health.getValue() || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.DIAMOND_CHESTPLATE || !nearPlayers();
    }
    private boolean isEmpty(BlockPos pos){
        return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream().filter(e -> e instanceof EntityEnderCrystal).count() == 0;
    }
    private boolean isGapplesAABBEmpty() {
        return isEmpty(mc.player.getPosition().add(1, 0, 0)) && isEmpty(mc.player.getPosition().add(-1, 0, 0)) && isEmpty(mc.player.getPosition().add(0, 0, 1)) && isEmpty(mc.player.getPosition().add(0, 0, -1)) && isEmpty(mc.player.getPosition());
    }
    int getGapSlot() {
        int gapSlot = -1;
        for (int i = 45; i > 0; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.GOLDEN_APPLE) {
                gapSlot = i;
                break;
            }
        }
        return gapSlot;
    }
    int getTotemSlot() {
        int totemSlot = -1;
        for (int i = 45; i > 0; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }
        return totemSlot;
    }
    @Override
    public String getHudInfo() {
        return "\u00A77[\u00A7f" + gapples + "\u00A77]";
    }
}
