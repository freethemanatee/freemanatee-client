package me.zopac.freemanatee.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.event.events.PacketEvent;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.init.Items;
import static me.zopac.freemanatee.util.InfoCalculator.reverseNumber;
@Module.Info(name = "AutoMend", category = Module.Category.COMBAT)
public class AutoMend extends Module {
    private Setting<Boolean> autoThrow = register(Settings.b("Auto Throw", true));
    private Setting<Boolean> autoSwitch = register(Settings.b("Auto Switch", true));
    private Setting<Boolean> autoDisable = register(Settings.booleanBuilder("Auto Disable").withValue(false).withVisibility(o -> autoSwitch.getValue()).build());
    private Setting<Integer> threshold = register(Settings.integerBuilder("Repair %").withMinimum(1).withMaximum(100).withValue(75));
    private int initHotbarSlot = -1;
    @EventHandler
    private Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (mc.player != null && (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE)) {
            mc.rightClickDelayTimer = 0;
        }
    });
    @Override
    protected void onEnable() {
        if (mc.player == null) return;

        if (autoSwitch.getValue()) {
            initHotbarSlot = mc.player.inventory.currentItem;
        }
    }
    @Override
    protected void onDisable() {
        if (mc.player == null) return;

        if (autoSwitch.getValue()) {
            if (initHotbarSlot != -1 && initHotbarSlot != mc.player.inventory.currentItem) {
                mc.player.inventory.currentItem = initHotbarSlot;
            }
        }
    }
    @Override
    public void onUpdate() {
        if (mc.player == null) return;
        if (shouldMend(0) || shouldMend(1) || shouldMend(2) || shouldMend(3)) {
            if (autoSwitch.getValue() && (mc.player.getHeldItemMainhand().getItem() != Items.EXPERIENCE_BOTTLE)) {
                int xpSlot = findXpPots();
                if (xpSlot == -1) {
                    if (autoDisable.getValue()) {
                        Command.sendChatMessage(" No XP in hotbar, disabling");
                        disable();
                    }
                    return;
                }
                mc.player.inventory.currentItem = xpSlot;
            }
            if (autoThrow.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) {
                mc.rightClickMouse();
            }
        }
    }
    private int findXpPots() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        } return slot;
    }
    private boolean shouldMend(int i) { // (100 * damage / max damage) >= (100 - 70)
        if (mc.player.inventory.armorInventory.get(i).getMaxDamage() == 0) return false;
        return (100 * mc.player.inventory.armorInventory.get(i).getItemDamage())
                / mc.player.inventory.armorInventory.get(i).getMaxDamage()
                > reverseNumber(threshold.getValue(), 1, 100);
    }
}