package me.zopac.freemanatee.module.modules.combat;

import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import me.zopac.freemanatee.setting.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import me.zopac.freemanatee.module.*;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import me.zopac.freemanatee.util.*;
import java.util.stream.Collectors;
import net.minecraft.util.EnumHand;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import java.util.Comparator;
import java.util.List;

@Module.Info(name = "AirAutoTrap", category = Module.Category.COMBAT)
public class AirAutoTrap extends Module {
    private Setting<Boolean> autoobiswitch;
    private Setting<Boolean> autobedswitch;
    BlockPos target;
    public AirAutoTrap() {
        this.autoobiswitch = this.register(Settings.b("Switch to obsidian", true));
        this.autobedswitch = this.register(Settings.b("Bed On Disable", false));
    }
    @Override
    protected void onEnable() {
        for (EntityPlayer player : getTargets()) {
            target = new BlockPos(player.posX, player.posY, player.posZ);
            if (autoobiswitch.getValue()) {
                switchHandToItemIfNeed(ItemBlock.getItemById(49));
            }
            if (mc.player.getHeldItemMainhand().getItem() == ItemBlock.getItemById(49)) {
                if (mc.world.getBlockState(target.up().up()) != Blocks.AIR) {
                    placeBlock(target.up().up(), EnumFacing.DOWN);
                }
                if (mc.world.getBlockState(target.up().up().up()) != Blocks.AIR) {
                    placeBlock(target.up().up().up(), EnumFacing.DOWN);
                }
            }
        }
        this.toggle();
    }
    @Override
    protected void onDisable() {
        if (autobedswitch.getValue()) {
            switchHandToItemIfNeed(ItemBlock.getItemById(355));
        }
    }
    private void placeBlock(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        boolean shouldSneak = !mc.player.isSneaking();
        if (shouldSneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        BlocksUtils.faceVectorPacketInstant(hitVec);
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        if (shouldSneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
    }
    public List<EntityPlayer> getTargets() {
        return mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList());
    }
    private boolean switchHandToItemIfNeed(Item toItem) {
        if (mc.player.getHeldItemMainhand().getItem() == toItem || mc.player.getHeldItemOffhand().getItem() == toItem)
            return false;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY)
                continue;
            if (stack.getItem() == toItem) {
                mc.player.inventory.currentItem = i;
                mc.playerController.updateController();
                return true;
            }
        }
        return true;
    }
}