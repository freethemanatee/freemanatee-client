package me.zeroeightsix.kami.module.modules.combat;

import java.util.concurrent.TimeUnit;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Info(
        name = "SelfWeb",
        category = Module.Category.COMBAT
)
public class SelfWeb extends Module {

    BlockPos feet;

    private Setting<Boolean> triggerable = register(Settings.b("Triggerable", true));
    private Setting<Integer> timeoutTicks = register(Settings.integerBuilder("TimeoutTicks").withMinimum(1).withValue(40).withMaximum(100).withVisibility(b -> triggerable.getValue()).build());
    private Setting<Integer> tickDelay = register(Settings.integerBuilder("TickDelay").withMinimum(0).withValue(0).withMaximum(10).build());
    private Setting delay = this.register(Settings.integerBuilder("Delay").withRange(0, 10).withValue((int)3).build());
    int d;
    private int totalTicksRunning = 0;
    private int delayStep = 0;
    private boolean firstRun;
    public static float yaw;
    public static float pitch;
    private Setting announceUsage = this.register(Settings.b("Announce Usage", true));

    public boolean isInBlockRange(Entity target) {
        return target.getDistance(mc.player) <= 4.0F;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }

    private boolean isStackObby(ItemStack stack) {
        return stack != null && stack.getItem() == Item.getItemById(30);
    }

    private boolean doesHotbarHaveWeb() {
        for(int i = 36; i < 45; ++i) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && this.isStackObby(stack)) {
                return true;
            }
        }

        return false;
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static boolean placeBlockLegit(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);
        Vec3d posVec = (new Vec3d(pos)).add(0.5D, 0.5D, 0.5D);
        EnumFacing[] var3 = EnumFacing.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing side = var3[var5];
            BlockPos neighbor = pos.offset(side);
            if (canBeClicked(neighbor)) {
                Vec3d hitVec = posVec.add((new Vec3d(side.getDirectionVec())).scale(0.5D));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0D) {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side.getOpposite(), hitVec, EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);

                    try {
                        TimeUnit.MILLISECONDS.sleep(10L);
                    } catch (InterruptedException var10) {
                        var10.printStackTrace();
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void onUpdate() {
        if (!mc.player.isHandActive()) {
            this.trap(mc.player);
        }

        if (triggerable.getValue() && totalTicksRunning >= timeoutTicks.getValue()) {
            totalTicksRunning = 0;
            this.disable();
            return;
        }

        if (!firstRun) {
            if (delayStep < tickDelay.getValue()) {
                delayStep++;
                return;
            } else {
                delayStep = 0;
            }

            totalTicksRunning++;
        }
    }

    public static double roundToHalf(double d) {
        return (double)Math.round(d * 2.0D) / 2.0D;
    }

    public void onEnable() {
        if (mc.player == null) {
            this.disable();
        } else {
            if ((Boolean)this.announceUsage.getValue()) {
                Command.sendChatMessage("\u00A7aSelfweb has been enabled");
            }

            this.d = 0;
        }
        firstRun = true;
    }

    private void trap(EntityPlayer player) {
        if ((double)player.moveForward == 0.0D && (double)player.moveStrafing == 0.0D && (double)player.moveForward == 0.0D) {
            ++this.d;
        }

        if ((double)player.moveForward != 0.0D || (double)player.moveStrafing != 0.0D || (double)player.moveForward != 0.0D) {
            this.d = 0;
        }

        if (!this.doesHotbarHaveWeb()) {
            this.d = 0;
        }

        if (this.d == (Integer)this.delay.getValue() && this.doesHotbarHaveWeb()) {
            this.feet = new BlockPos(player.posX, player.posY, player.posZ);

            for(int i = 36; i < 45; ++i) {
                ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack != null && this.isStackObby(stack)) {
                    int oldSlot = mc.player.inventory.currentItem;
                    if (mc.world.getBlockState(this.feet).getMaterial().isReplaceable()) {
                        mc.player.inventory.currentItem = i - 36;
                        if (mc.world.getBlockState(this.feet).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.feet);
                        }

                        mc.player.inventory.currentItem = oldSlot;
                        this.d = 0;
                        break;
                    }

                    this.d = 0;
                }

                this.d = 0;
            }

        }
    }

    public void onDisable() {
        this.d = 0;
        if ((Boolean)this.announceUsage.getValue()) {
            Command.sendChatMessage("\u00A7cSelfWeb has been disabled");
        }

        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
    }

    public EnumFacing getEnumFacing(float posX, float posY, float posZ) {
        return EnumFacing.getFacingFromVector(posX, posY, posZ);
    }

    public BlockPos getBlockPos(double x, double y, double z) {
        return new BlockPos(x, y, z);
    }
}
