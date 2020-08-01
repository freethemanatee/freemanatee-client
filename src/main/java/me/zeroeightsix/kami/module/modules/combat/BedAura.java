package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;


import java.util.Comparator;

@Module.Info(
        name = "BedAura",
        category = Module.Category.COMBAT
)

public class BedAura extends Module {

    private Setting<Boolean> rotate = register(Settings.b("Rotate", true));
    private Setting<Boolean> refill = register(Settings.b("Refill", true));
    private Setting<Boolean> dimensionCheck = register(Settings.b("DimensionCheck", true));
    private Setting<Integer> range = this.register(Settings.integerBuilder("Range").withMinimum(0).withValue(5).withMaximum(10).build());

    boolean moving = false;

    public void onUpdate() {
        if(refill.getValue()) {
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                if (mc.player.inventory.getStackInSlot(i) == ItemStack.EMPTY) {
                    slot = i;
                    break;
                }
            }

            if (moving && slot != -1) {
                mc.playerController.windowClick(0, slot + 36, 0, ClickType.PICKUP, mc.player);
                moving = false;
                slot = -1;
            }

            if (slot != -1 && !(mc.currentScreen instanceof GuiContainer) && mc.player.inventory.getItemStack().isEmpty()) {
                //search for beds in inventory
                int t = -1;
                for (int i = 0; i < 45; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.BED && i >= 9) {
                        t = i;
                        break;
                    }
                }
                //click bed item
                if (t != -1) {
                    mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, mc.player);
                    moving = true;
                }
            }
        }

        mc.world.loadedTileEntityList.stream()
                .filter(e -> e instanceof TileEntityBed)
                .filter(e -> mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ()) <= range.getValue())
                .sorted(Comparator.comparing(e -> mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ())))
                .forEach(bed -> {

                    if(dimensionCheck.getValue() && mc.player.dimension == 0) return;

                    if(rotate.getValue()) BlockUtils.faceVectorPacketInstant(new Vec3d(bed.getPos().add(0.5, 0.5, 0.5)));
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bed.getPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));

                });
    }
}