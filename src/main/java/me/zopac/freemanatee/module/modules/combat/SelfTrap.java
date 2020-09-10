package me.zopac.freemanatee.module.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.BlockInteractionHelper;
import me.zopac.freemanatee.util.EntityUtil;
import me.zopac.freemanatee.util.Friends;
import me.zopac.freemanatee.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Info(
        name = "Selftrap",
        category = Module.Category.COMBAT
)

public class SelfTrap extends Module {
    private final Vec3d[] offsetsDefault = new Vec3d[]{new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(0.0D, 3.0D, 0.0D)};
    private final Vec3d[] offsetsExtra = new Vec3d[]{new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(0.0D, 3.0D, 0.0D), new Vec3d(0.0D, 4.0D, 0.0D)};
    private Setting range = this.register(Settings.d("Range", 5.5D));
    private Setting blockPerTick = this.register(Settings.i("Blocks per Tick", 6));
    private Setting extrablock = this.register(Settings.b("Extra Block", true));
    private Setting announceUsage = this.register(Settings.b("Announce Usage", true));
    private EntityPlayer closestTarget;
    private String lastTickTargetName;
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private boolean isSneaking = false;
    private int offsetStep = 0;
    private boolean firstRun;
    private double yHeight;
    private double xPos;
    private double zPos;
    protected void onEnable() {
        if (mc.player == null) {
            this.disable();
        } else {
            this.firstRun = true;
            this.playerHotbarSlot = Wrapper.getPlayer().inventory.currentItem;
            this.lastHotbarSlot = -1;
            this.yHeight = (double)((int)Math.round(mc.player.posY));
            this.xPos = (double)((int)Math.round(mc.player.posX));
            this.zPos = (double)((int)Math.round(mc.player.posZ));
            if ((Boolean)this.announceUsage.getValue()) {
                Command.sendChatMessage("[SelfTrap] " + ChatFormatting.GREEN.toString() + "Enabled" + ChatFormatting.RESET.toString() + " ");
            }
        }
    }
    protected void onDisable() {
        if (mc.player != null) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                Wrapper.getPlayer().inventory.currentItem = this.playerHotbarSlot;
            }
            if (this.isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
            this.playerHotbarSlot = -1;
            this.lastHotbarSlot = -1;
            if ((Boolean)this.announceUsage.getValue()) {
                Command.sendChatMessage("[SelfTrap] " + ChatFormatting.RED.toString() + "Disabled" + ChatFormatting.RESET.toString() + " ");
            }
        }
    }
    public void onUpdate() {
        if (mc.player != null) {
            }
            this.findClosestTarget();
            if (this.closestTarget == null) {
                if (this.firstRun) {
                    this.firstRun = false;
                }
            } else {
                if (this.firstRun) {
                    this.firstRun = false;
                    this.lastTickTargetName = this.closestTarget.getName();
                } else if (!this.lastTickTargetName.equals(this.closestTarget.getName())) {
                    this.lastTickTargetName = this.closestTarget.getName();
                    this.offsetStep = 0;
                }
                List placeTargets = new ArrayList();
                if ((Boolean)this.extrablock.getValue()) {
                    Collections.addAll(placeTargets, this.offsetsExtra);
                } else {
                    Collections.addAll(placeTargets, this.offsetsDefault);
                }
                int blocksPlaced;
                for(blocksPlaced = 0; blocksPlaced < (Integer)this.blockPerTick.getValue(); ++this.offsetStep) {
                    if (this.offsetStep >= placeTargets.size()) {
                        this.offsetStep = 0;
                        break;
                    }
                    BlockPos offsetPos = new BlockPos((Vec3d)placeTargets.get(this.offsetStep));
                    BlockPos targetPos = (new BlockPos(this.closestTarget.getPositionVector())).down().add(offsetPos.x, offsetPos.y, offsetPos.z);
                    boolean shouldTryToPlace = true;
                    if (!Wrapper.getWorld().getBlockState(targetPos).getMaterial().isReplaceable()) {
                        shouldTryToPlace = false;
                    }
                    Iterator var6 = mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos)).iterator();
                    while(var6.hasNext()) {
                        Entity entity = (Entity)var6.next();
                        if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                            shouldTryToPlace = false;
                            break;
                        }
                    }
                    if (shouldTryToPlace && this.placeBlockTravis(targetPos)) {
                        ++blocksPlaced;
                    }
                }
                if (blocksPlaced > 0) {
                    if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                        Wrapper.getPlayer().inventory.currentItem = this.playerHotbarSlot;
                        this.lastHotbarSlot = this.playerHotbarSlot;
                    }
                    if (this.isSneaking) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
                        this.isSneaking = false;
                    }
                }
            }
        }
    private boolean placeBlockTravis(BlockPos pos) {
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        } else if (!BlockInteractionHelper.checkForNeighbours(pos)) {
            return false;
        } else {
            Vec3d eyesPos = new Vec3d(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY + (double)Wrapper.getPlayer().getEyeHeight(), Wrapper.getPlayer().posZ);
            EnumFacing[] var3 = EnumFacing.values();
            int var4 = var3.length;
            for(int var5 = 0; var5 < var4; ++var5) {
                EnumFacing side = var3[var5];
                BlockPos neighbor = pos.offset(side);
                EnumFacing side2 = side.getOpposite();
                if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                    Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));
                    if (eyesPos.distanceTo(hitVec) <= (Double)this.range.getValue()) {
                        int obiSlot = this.findObiInHotbar();
                        if (obiSlot == -1) {
                            this.disable();
                            return false;
                        }
                        if (this.lastHotbarSlot != obiSlot) {
                            Wrapper.getPlayer().inventory.currentItem = obiSlot;
                            this.lastHotbarSlot = obiSlot;
                        }
                        Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
                        if (BlockInteractionHelper.blackList.contains(neighborPos)) {
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
                            this.isSneaking = true;
                        }
                        BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        return true;
                    }
                }
            }
            return false;
        }
    }
    private int findObiInHotbar() {
        int slot = -1;
        for(int i = 0; i < 9; ++i) {
            ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() instanceof BlockObsidian) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    private void findClosestTarget() {
        List playerList = Wrapper.getWorld().playerEntities;
        this.closestTarget = null;
        Iterator var2 = playerList.iterator();

        while(var2.hasNext()) {
            EntityPlayer target = (EntityPlayer)var2.next();
            if (!Friends.isFriend(target.getName()) && EntityUtil.isLiving(target) && target.getHealth() > 0.0F) {
                if (this.closestTarget == null) {
                    this.closestTarget = target;
                } else if (Wrapper.getPlayer().getDistance(target) < Wrapper.getPlayer().getDistance(this.closestTarget)) {
                    this.closestTarget = target;
                }
            }
        }
    }
}
