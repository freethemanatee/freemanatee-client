package me.zeroeightsix.kami.module.modules.ECME;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Created by hub on 5 August 2019.
 */
@Module.Info(name = "HopperNuker", category = Module.Category.ECME)
public class HopperNuker extends Module {

    private Setting<Double> range = register(Settings.d("Range", 5.5d));
    private Setting<Boolean> pickswitch = register(Settings.b("Auto Switch", false));

    private int oldSlot = -1;
    private boolean isMining = false;

    @Override
    public void onUpdate() {

        BlockPos pos;

        pos = getNearestHopper();

        if (pos != null) {

            if (!isMining) {
                // save initial player hand
                oldSlot = Wrapper.getPlayer().inventory.currentItem;
                isMining = true;
            }

            float[] angle = BlockInteractionHelper.calcAngle(Wrapper.getPlayer().getPositionEyes(Wrapper.getMinecraft().getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() + 0.5F), (pos.getZ() + 0.5F)));

            Wrapper.getPlayer().rotationYaw = angle[0];
            Wrapper.getPlayer().rotationYawHead = angle[0];
            Wrapper.getPlayer().rotationPitch = angle[1];

            if (canBreak(pos)) {

                if (pickswitch.getValue()) {

                    // search pick in hotbar
                    int newSlot = -1;

                    for (int i = 0; i < 9; i++) {

                        ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);

                        if (stack == ItemStack.EMPTY) {
                            continue;
                        }

                        if ((stack.getItem() instanceof ItemPickaxe)) {
                            newSlot = i;
                            break;
                        }

                    }

                    // check if any picks were found
                    if (newSlot != -1) {
                        Wrapper.getPlayer().inventory.currentItem = newSlot;
                    }

                }

                Wrapper.getMinecraft().playerController.onPlayerDamageBlock(pos, Wrapper.getPlayer().getHorizontalFacing());
                Wrapper.getPlayer().swingArm(EnumHand.MAIN_HAND);
            }
        } else {
            if (pickswitch.getValue() && oldSlot != -1) {
                // restore initial player hand
                Wrapper.getPlayer().inventory.currentItem = oldSlot;
                oldSlot = -1;
                isMining = false;
            }
        }

    }

    private boolean canBreak(BlockPos pos) {
        IBlockState blockState = (Wrapper.getWorld().getBlockState(pos));
        Block block = blockState.getBlock();
        return (block.getBlockHardness(blockState, Wrapper.getWorld(), pos) != -1.0F);
    }

    private BlockPos getNearestHopper() {
        Double maxDist = this.range.getValue();
        BlockPos ret = null;
        Double x;
        for (x = maxDist; x >= -maxDist; x--) {
            Double y;
            for (y = maxDist; y >= -maxDist; y--) {
                Double z;
                for (z = maxDist; z >= -maxDist; z--) {
                    BlockPos pos = new BlockPos(Wrapper.getPlayer().posX + x, Wrapper.getPlayer().posY + y, Wrapper.getPlayer().posZ + z);
                    double dist = Wrapper.getPlayer().getDistance(pos.getX(), pos.getY(), pos.getZ());
                    if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.HOPPER && canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY) {
                        maxDist = dist;
                        ret = pos;
                    }
                }
            }
        }
        return ret;
    }

}