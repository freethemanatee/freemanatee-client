package me.zopac.freemanatee.module.modules.dispenserpvp;

import net.minecraft.client.gui.inventory.*;
import net.minecraft.network.play.client.*;
import me.zopac.freemanatee.setting.*;
import me.zopac.freemanatee.command.*;
import me.zopac.freemanatee.module.*;
import net.minecraft.entity.player.*;
import me.zopac.freemanatee.util.*;
import net.minecraft.util.math.*;
import net.minecraft.inventory.*;
import net.minecraft.network.*;
import net.minecraft.entity.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import java.text.*;
import java.math.*;

@Module.Info(name = "Auto32k", category = Module.Category.dispenserpvp)
public class Auto32k extends Module {
    private static final DecimalFormat df;
    private Setting<Boolean> autoEnableHitAura;
    private Setting<Boolean> debugMessages;
    private Setting<Boolean> eventMessages;
    private Setting<Boolean> fillHopper;
    private Setting<Boolean> grabItem;
    private Setting<Boolean> rotate;
    private BlockPos placeTarget;
    private boolean isSneaking;
    private int dispenserSlot;
    private int targetDistance;
    private int redstoneSlot;
    private int shulkerSlot;
    private int hopperSlot;
    private int obiSlot;
    private int stage;

    public Auto32k() {
        this.autoEnableHitAura = this.register(Settings.b("Auto enable Hit Aura", false));
        this.debugMessages = this.register(Settings.b("Debug Messages", false));
        this.eventMessages = this.register(Settings.b("Event Messages", false));
        this.fillHopper = this.register(Settings.b("Fill Hopper", true));
        this.grabItem = this.register(Settings.b("Grab Item", true));
        this.rotate = this.register(Settings.b("Rotate", false));
    }

    @Override
    protected void onEnable() {
        if (Auto32k.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            this.disable();
            return;
        }
        Auto32k.df.setRoundingMode(RoundingMode.CEILING);
        this.stage = 0;
        this.placeTarget = null;
        this.obiSlot = -1;
        this.dispenserSlot = -1;
        this.shulkerSlot = -1;
        this.redstoneSlot = -1;
        this.hopperSlot = -1;
        this.isSneaking = false;
        for (int i = 0; i < 9 && (this.obiSlot == -1 || this.dispenserSlot == -1 || this.shulkerSlot == -1 || this.redstoneSlot == -1 || this.hopperSlot == -1); ++i) {
            final ItemStack stack = Auto32k.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block == Blocks.HOPPER) {
                        this.hopperSlot = i;
                    }
                    else if (BlockInteractionHelper.shulkerList.contains(block)) {
                        this.shulkerSlot = i;
                    }
                    else if (block == Blocks.OBSIDIAN) {
                        this.obiSlot = i;
                    }
                    else if (block == Blocks.DISPENSER) {
                        this.dispenserSlot = i;
                    }
                    else if (block == Blocks.REDSTONE_BLOCK) {
                        this.redstoneSlot = i;
                    }
                }
            }
        }
        if (this.obiSlot == -1 || this.dispenserSlot == -1 || this.shulkerSlot == -1 || this.redstoneSlot == -1 || this.hopperSlot == -1) {
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Items missing, disabling.");
            }
            this.disable();
            return;
        }
        if (Auto32k.mc.objectMouseOver == null || Auto32k.mc.objectMouseOver.getBlockPos() == null || Auto32k.mc.objectMouseOver.getBlockPos().up() == null) {
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Not a valid place target, disabling.");
            }
            this.disable();
            return;
        }
        this.placeTarget = Auto32k.mc.objectMouseOver.getBlockPos().up();
        this.targetDistance = (int)Auto32k.mc.player.getPositionVector().distanceTo(new Vec3d((Vec3i)this.placeTarget));
        if (this.targetDistance >= 5 || this.targetDistance <= 1) {
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Not a valid place target, disabling.");
            }
            this.disable();
            return;
        }
        if (this.debugMessages.getValue()) {
            Command.sendChatMessage("[Auto32k] Place Target: " + this.placeTarget.x + " " + this.placeTarget.y + " " + this.placeTarget.z + " Distance: " + Auto32k.df.format(Auto32k.mc.player.getPositionVector().distanceTo(new Vec3d((Vec3i)this.placeTarget))));
        }
    }

    @Override
    public void onUpdate() {
        if (Auto32k.mc.player == null || ModuleManager.isModuleEnabled("Freecam ")) {
            return;
        }
        if (this.stage == 0) {
            Auto32k.mc.player.inventory.currentItem = this.obiSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget), EnumFacing.DOWN);
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Placed obsidian.");
            }
            Auto32k.mc.player.inventory.currentItem = this.dispenserSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget.add(0, 1, 0)), EnumFacing.DOWN);
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Placed dispenser.");
            }
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Attempting to open dispenser.");
            }
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.placeTarget.add(0, 1, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            this.stage = 1;
            return;
        }
        if (this.stage == 1) {
            if (!(Auto32k.mc.currentScreen instanceof GuiContainer)) {
                return;
            }
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Successfully opened dispenser.");
            }
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Attempting to move shulker.");
            }
            Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 1, this.shulkerSlot, ClickType.SWAP, (EntityPlayer)Auto32k.mc.player);
            Auto32k.mc.player.closeScreen();
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Successfully moved shulker.");
            }
            Auto32k.mc.player.inventory.currentItem = this.redstoneSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget.add(0, 2, 0)), EnumFacing.DOWN);
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Placed redstone block.");
            }
            this.stage = 2;
        }
        else {
            if (this.stage != 2) {
                if (this.stage == 3) {
                    if (!(Auto32k.mc.currentScreen instanceof GuiContainer)) {
                        return;
                    }
                    if (((GuiContainer)Auto32k.mc.currentScreen).inventorySlots.getSlot(0).getStack().isEmpty) {
                        return;
                    }
                    if (this.fillHopper.getValue()) {
                        if (this.eventMessages.getValue()) {
                            Command.sendChatMessage("[Auto32k] Beginning to fill hopper.");
                        }
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 4, this.obiSlot, ClickType.SWAP, (EntityPlayer)Auto32k.mc.player);
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 4, 0, ClickType.PICKUP, (EntityPlayer)Auto32k.mc.player);
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 4, 1, ClickType.PICKUP, (EntityPlayer)Auto32k.mc.player);
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 3, 1, ClickType.PICKUP, (EntityPlayer)Auto32k.mc.player);
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 2, 1, ClickType.PICKUP, (EntityPlayer)Auto32k.mc.player);
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 1, 1, ClickType.PICKUP, (EntityPlayer)Auto32k.mc.player);
                    }
                    if (this.eventMessages.getValue()) {
                        Command.sendChatMessage("[Auto32k] Attempting to move sword. ");
                    }
                    Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 0, Auto32k.mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer)Auto32k.mc.player);
                    if (this.fillHopper.getValue()) {
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 0, 0, ClickType.PICKUP, (EntityPlayer)Auto32k.mc.player);
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 0, this.obiSlot, ClickType.SWAP, (EntityPlayer)Auto32k.mc.player);
                        if (this.eventMessages.getValue()) {
                            Command.sendChatMessage("[Auto32k] Hopper filled.");
                        }
                    }
                    if (this.eventMessages.getValue()) {
                        Command.sendChatMessage("[Auto32k] Successfully moved sword. ");
                    }
                    if (this.autoEnableHitAura.getValue()) {
                        ModuleManager.getModuleByName("Aura").enable();
                    }
                    if (this.eventMessages.getValue()) {
                        Command.sendChatMessage("[Auto32k] 32k placed, disabling.");
                    }
                    this.disable();
                }
                return;
            }
            final Block block = Auto32k.mc.world.getBlockState(this.placeTarget.offset(Auto32k.mc.player.getHorizontalFacing().getOpposite()).up()).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid) {
                return;
            }
            Auto32k.mc.player.inventory.currentItem = this.hopperSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget.offset(Auto32k.mc.player.getHorizontalFacing().getOpposite())), Auto32k.mc.player.getHorizontalFacing());
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Placed hopper.");
            }
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Attempting to open hopper.");
            }
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.placeTarget.offset(Auto32k.mc.player.getHorizontalFacing().getOpposite()), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            if (this.eventMessages.getValue()) {
                Command.sendChatMessage("[Auto32k] Successfully opened hopper.");
            }
            Auto32k.mc.player.inventory.currentItem = this.shulkerSlot;
            if (!this.grabItem.getValue()) {
                this.disable();
                return;
            }
            this.stage = 3;
        }
    }

    private void placeBlock(final BlockPos pos, final EnumFacing side) {
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!this.isSneaking) {
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (this.rotate.getValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }
        Auto32k.mc.playerController.processRightClickBlock(Auto32k.mc.player, Auto32k.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    static {
        df = new DecimalFormat("#.#");
    }
}
