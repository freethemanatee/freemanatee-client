package me.zopac.freemanatee.module.modules.misc;

import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import static me.zopac.freemanatee.util.MessageSendHelper.sendChatMessage;
import static me.zopac.freemanatee.util.MessageSendHelper.sendErrorMessage;

@Module.Info(name = "AutoNametag", category = Module.Category.MISC)
public class AutoNametag extends Module {
    private Setting<Mode> modeSetting = register(Settings.e("Mode", Mode.WITHER));
    private Setting<Float> range = register(Settings.floatBuilder("Range").withMinimum(2.0f).withValue(3.5f).withMaximum(10.0f).build());
    private Setting<Boolean> autoSlot = register(Settings.b("Auto Slot", true));
    private Setting<Boolean> debug = register(Settings.b("Debug", false));

    private String currentName = "";

    public void onUpdate() {
        useNameTag();
    }

    private void useNameTag() {
        int originalSlot = mc.player.inventory.currentItem;
        for (Entity w : mc.world.getLoadedEntityList()) {
            switch (modeSetting.getValue()) {
                case WITHER:
                    if (w instanceof EntityWither && !w.getDisplayName().getUnformattedText().equals(currentName)) {
                        final EntityWither wither = (EntityWither) w;
                        if (mc.player.getDistance(wither) <= range.getValue()) {
                            if (debug.getValue())
                                sendChatMessage("Found unnamed Wither");
                            selectNameTags();
                            mc.playerController.interactWithEntity(mc.player, wither, EnumHand.MAIN_HAND);
                        }
                    }
                    return;
                case ANY:
                    if (w instanceof EntityMob || w instanceof EntityAnimal && !w.getDisplayName().getUnformattedText().equals(currentName)) {
                        if (mc.player.getDistance(w) <= range.getValue()) {
                            if (debug.getValue())
                                sendChatMessage("Found unnamed " + w.getDisplayName().getUnformattedText());
                            selectNameTags();
                            mc.playerController.interactWithEntity(mc.player, w, EnumHand.MAIN_HAND);
                        }
                    }
            }
        }
        if (autoSlot.getValue()) mc.player.inventory.currentItem = originalSlot;
    }

    private void selectNameTags() {
        if (!autoSlot.getValue()) return;
        int tagSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || stack.getItem() instanceof ItemBlock) continue;
            Item tag = stack.getItem();
            if (tag instanceof ItemNameTag) {
                tagSlot = i;
                currentName = stack.getDisplayName();
            }
        }

        if (tagSlot == -1) {
            if (debug.getValue()) sendErrorMessage(getName() + "Error: No nametags in hotbar");
            disable();
            return;
        }

        mc.player.inventory.currentItem = tagSlot;
    }

    private enum Mode { WITHER, ANY }
}