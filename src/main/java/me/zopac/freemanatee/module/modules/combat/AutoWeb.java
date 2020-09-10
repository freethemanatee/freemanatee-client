package me.zopac.freemanatee.module.modules.combat;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.Friends;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Info(
        name = "AutoWeb",
        category = Module.Category.COMBAT
)
public class AutoWeb extends Module {
    BlockPos head;
    BlockPos feet;
    private Setting delay = this.register(Settings.integerBuilder("Delay").withRange(0, 10).withValue((int)3).build());
    int d;
    public static EntityPlayer target;
    public static List targets;
    public static float yaw;
    public static float pitch;
    public boolean isInBlockRange(Entity target) {
        return target.getDistance(mc.player) <= 4.0F;
    }
    public static boolean canBeClicked(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }
    public boolean isValid(EntityPlayer entity) {
        return entity instanceof EntityPlayer && this.isInBlockRange(entity) && entity.getHealth() > 0.0F && !entity.isDead && !entity.getName().startsWith("Body #") && !Friends.isFriend(entity.getName());
    }
    public void loadTargets() {
        Iterator var1 = mc.world.playerEntities.iterator();
        while(var1.hasNext()) {
            EntityPlayer player = (EntityPlayer)var1.next();
            if (!(player instanceof EntityPlayerSP)) {
                if (this.isValid(player)) {
                    targets.add(player);
                } else if (targets.contains(player)) {
                    targets.remove(player);
                }
            }
        }
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
            if (!this.isValid(target) || target == null) {
                this.updateTarget();
            }
            Iterator var1 = mc.world.playerEntities.iterator();
            while(var1.hasNext()) {
                EntityPlayer player = (EntityPlayer)var1.next();
                if (!(player instanceof EntityPlayerSP) && this.isValid(player) && player.getDistance(mc.player) < target.getDistance(mc.player)) {
                    target = player;
                    return;
                }
            }
            if (this.isValid(target) && mc.player.getDistance(target) < 4.0F) {
                this.trap(target);
            } else {
                this.d = 0;
            }
        }
    }
    public void onEnable() {
        if (mc.player == null) {
            this.disable();
        }
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
            this.head = new BlockPos(player.posX, player.posY + 1.0D, player.posZ);
            this.feet = new BlockPos(player.posX, player.posY, player.posZ);
            for(int i = 36; i < 45; ++i) {
                ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack != null && this.isStackObby(stack)) {
                    int oldSlot = mc.player.inventory.currentItem;
                    if (mc.world.getBlockState(this.head).getMaterial().isReplaceable() || mc.world.getBlockState(this.feet).getMaterial().isReplaceable()) {
                        mc.player.inventory.currentItem = i - 36;
                        if ((double)player.moveForward != 0.0D && (double)player.moveStrafing != 0.0D) {
                            if (mc.world.getBlockState(this.head).getMaterial().isReplaceable()) {
                                placeBlockLegit(this.head);
                            }
                            if (mc.world.getBlockState(this.feet).getMaterial().isReplaceable()) {
                                placeBlockLegit(this.feet);
                            }
                        } else if (mc.world.getBlockState(this.head).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.head);
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
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
        target = null;
    }
    public void updateTarget() {
        Iterator var1 = mc.world.playerEntities.iterator();
        while(var1.hasNext()) {
            EntityPlayer player = (EntityPlayer)var1.next();
            if (!(player instanceof EntityPlayerSP) && !(player instanceof EntityPlayerSP) && this.isValid(player)) {
                target = player;
            }
        }
    }
    public EnumFacing getEnumFacing(float posX, float posY, float posZ) {
        return EnumFacing.getFacingFromVector(posX, posY, posZ);
    }
    public BlockPos getBlockPos(double x, double y, double z) {
        return new BlockPos(x, y, z);
    }
}
