package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import me.zeroeightsix.kami.util.EntityUtil;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.setting.Setting;

@Module.Info(name = "Scaffold", category = Module.Category.PLAYER)
public class Scaffold extends Module
{
    private Setting<Integer> future;

    public Scaffold() {
        this.future = this.register(Settings.integerBuilder("Ticks").withMinimum(0).withMaximum(60).withValue(2));
    }

    @Override
    public void onUpdate() {
        if (this.isDisabled() || Scaffold.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        final Vec3d vec3d = EntityUtil.getInterpolatedPos((Entity)Scaffold.mc.player, this.future.getValue());
        final BlockPos blockPos = new BlockPos(vec3d).down();
        final BlockPos belowBlockPos = blockPos.down();
        if (!Wrapper.getWorld().getBlockState(blockPos).getMaterial().isReplaceable()) {
            return;
        }
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (!BlockInteractionHelper.blackList.contains(block)) {
                        if (!(block instanceof BlockContainer)) {
                            if (Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullBlock()) {
                                if (!(((ItemBlock)stack.getItem()).getBlock() instanceof BlockFalling) || !Wrapper.getWorld().getBlockState(belowBlockPos).getMaterial().isReplaceable()) {
                                    newSlot = i;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (newSlot == -1) {
            return;
        }
        final int oldSlot = Wrapper.getPlayer().inventory.currentItem;
        Wrapper.getPlayer().inventory.currentItem = newSlot;
        if (!BlockInteractionHelper.checkForNeighbours(blockPos)) {
            return;
        }
        BlockInteractionHelper.placeBlockScaffold(blockPos);
        Wrapper.getPlayer().inventory.currentItem = oldSlot;
    }
}
