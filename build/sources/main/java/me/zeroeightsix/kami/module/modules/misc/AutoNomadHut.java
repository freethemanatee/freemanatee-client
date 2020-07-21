package me.zeroeightsix.kami.module.modules.misc;

import net.minecraft.util.math.MathHelper;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.Vec3i;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.util.Wrapper;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.util.math.BlockPos;
import me.zeroeightsix.kami.setting.Setting;
import net.minecraft.util.math.Vec3d;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "AutoNomadHut", description = "best module and you cant deny it", category = Module.Category.MISC)
public class AutoNomadHut extends Module
{
    private final Vec3d[] surroundTargets;
    private final Vec3d[] surroundTargetsCritical;
    private Setting<Boolean> toggleable;
    private Setting<Boolean> spoofRotations;
    private Setting<Boolean> spoofHotbar;
    private Setting<Double> blockPerTick;
    private Setting<Boolean> debugMessages;
    private BlockPos basePos;
    private int offsetStep;
    private int playerHotbarSlot;
    private int lastHotbarSlot;

    public AutoNomadHut() {
        this.surroundTargets = new Vec3d[] { new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(2.0, 0.0, 0.0), new Vec3d(2.0, 0.0, 1.0), new Vec3d(2.0, 0.0, -1.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(-2.0, 0.0, 1.0), new Vec3d(-2.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 2.0), new Vec3d(1.0, 0.0, 2.0), new Vec3d(-1.0, 0.0, 2.0), new Vec3d(0.0, 0.0, -2.0), new Vec3d(-1.0, 0.0, -2.0), new Vec3d(1.0, 0.0, -2.0), new Vec3d(2.0, 1.0, -1.0), new Vec3d(2.0, 1.0, 1.0), new Vec3d(-2.0, 1.0, 0.0), new Vec3d(-2.0, 1.0, 1.0), new Vec3d(-2.0, 1.0, -1.0), new Vec3d(0.0, 1.0, 2.0), new Vec3d(1.0, 1.0, 2.0), new Vec3d(-1.0, 1.0, 2.0), new Vec3d(0.0, 1.0, -2.0), new Vec3d(1.0, 1.0, -2.0), new Vec3d(-1.0, 1.0, -2.0), new Vec3d(2.0, 2.0, -1.0), new Vec3d(2.0, 2.0, 1.0), new Vec3d(-2.0, 2.0, 1.0), new Vec3d(-2.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 2.0), new Vec3d(-1.0, 2.0, 2.0), new Vec3d(1.0, 2.0, -2.0), new Vec3d(-1.0, 2.0, -2.0), new Vec3d(2.0, 3.0, 0.0), new Vec3d(2.0, 3.0, -1.0), new Vec3d(2.0, 3.0, 1.0), new Vec3d(-2.0, 3.0, 0.0), new Vec3d(-2.0, 3.0, 1.0), new Vec3d(-2.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 2.0), new Vec3d(1.0, 3.0, 2.0), new Vec3d(-1.0, 3.0, 2.0), new Vec3d(0.0, 3.0, -2.0), new Vec3d(1.0, 3.0, -2.0), new Vec3d(-1.0, 3.0, -2.0), new Vec3d(0.0, 4.0, 0.0), new Vec3d(1.0, 4.0, 0.0), new Vec3d(-1.0, 4.0, 0.0), new Vec3d(0.0, 4.0, 1.0), new Vec3d(0.0, 4.0, -1.0), new Vec3d(1.0, 4.0, 1.0), new Vec3d(-1.0, 4.0, 1.0), new Vec3d(-1.0, 4.0, -1.0), new Vec3d(1.0, 4.0, -1.0), new Vec3d(2.0, 4.0, 0.0), new Vec3d(2.0, 4.0, 1.0), new Vec3d(2.0, 4.0, -1.0) };
        this.surroundTargetsCritical = new Vec3d[] { new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(2.0, 0.0, 0.0), new Vec3d(2.0, 0.0, 1.0), new Vec3d(2.0, 0.0, -1.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(-2.0, 0.0, 1.0), new Vec3d(-2.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 2.0), new Vec3d(1.0, 0.0, 2.0), new Vec3d(-1.0, 0.0, 2.0), new Vec3d(0.0, 0.0, -2.0), new Vec3d(-1.0, 0.0, -2.0), new Vec3d(1.0, 0.0, -2.0), new Vec3d(2.0, 1.0, -1.0), new Vec3d(2.0, 1.0, 1.0), new Vec3d(-2.0, 1.0, 0.0), new Vec3d(-2.0, 1.0, 1.0), new Vec3d(-2.0, 1.0, -1.0), new Vec3d(0.0, 1.0, 2.0), new Vec3d(1.0, 1.0, 2.0), new Vec3d(-1.0, 1.0, 2.0), new Vec3d(0.0, 1.0, -2.0), new Vec3d(1.0, 1.0, -2.0), new Vec3d(-1.0, 1.0, -2.0), new Vec3d(2.0, 2.0, -1.0), new Vec3d(2.0, 2.0, 1.0), new Vec3d(-2.0, 2.0, 1.0), new Vec3d(-2.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 2.0), new Vec3d(-1.0, 2.0, 2.0), new Vec3d(1.0, 2.0, -2.0), new Vec3d(-1.0, 2.0, -2.0), new Vec3d(2.0, 3.0, 0.0), new Vec3d(2.0, 3.0, -1.0), new Vec3d(2.0, 3.0, 1.0), new Vec3d(-2.0, 3.0, 0.0), new Vec3d(-2.0, 3.0, 1.0), new Vec3d(-2.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 2.0), new Vec3d(1.0, 3.0, 2.0), new Vec3d(-1.0, 3.0, 2.0), new Vec3d(0.0, 3.0, -2.0), new Vec3d(1.0, 3.0, -2.0), new Vec3d(-1.0, 3.0, -2.0), new Vec3d(0.0, 4.0, 0.0), new Vec3d(1.0, 4.0, 0.0), new Vec3d(-1.0, 4.0, 0.0), new Vec3d(0.0, 4.0, 1.0), new Vec3d(0.0, 4.0, -1.0), new Vec3d(1.0, 4.0, 1.0), new Vec3d(-1.0, 4.0, 1.0), new Vec3d(-1.0, 4.0, -1.0), new Vec3d(1.0, 4.0, -1.0), new Vec3d(2.0, 4.0, 0.0), new Vec3d(2.0, 4.0, 1.0), new Vec3d(2.0, 4.0, -1.0) };
        this.toggleable = this.register(Settings.b("Toggleable", true));
        this.spoofRotations = this.register(Settings.b("Spoof Rotations", false));
        this.spoofHotbar = this.register(Settings.b("Spoof Hotbar", false));
        this.blockPerTick = this.register(Settings.d("Blocks per Tick", 1.0));
        this.debugMessages = this.register(Settings.b("Debug Messages", false));
        this.offsetStep = 0;
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
    }

    @Override
    public void onUpdate() {
        if (this.isDisabled() || AutoNomadHut.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (this.offsetStep == 0) {
            this.basePos = new BlockPos(AutoNomadHut.mc.player.getPositionVector()).down();
            this.playerHotbarSlot = Wrapper.getPlayer().inventory.currentItem;
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[AutoNomadHut] Starting Loop, current Player Slot: " + this.playerHotbarSlot);
            }
            if (!this.spoofHotbar.getValue()) {
                this.lastHotbarSlot = AutoNomadHut.mc.player.inventory.currentItem;
            }
        }
        for (int i = 0; i < (int)Math.floor(this.blockPerTick.getValue()); ++i) {
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[AutoNomadHut] Loop iteration: " + this.offsetStep);
            }
            if (this.offsetStep >= this.surroundTargets.length) {
                this.endLoop();
                return;
            }
            final Vec3d offset = this.surroundTargets[this.offsetStep];
            this.placeBlock(new BlockPos((Vec3i)this.basePos.add(offset.x, offset.y, offset.z)));
            ++this.offsetStep;
        }
    }

    @Override
    protected void onEnable() {
        if (AutoNomadHut.mc.player == null) {
            this.disable();
            return;
        }
        if (this.debugMessages.getValue()) {
            Command.sendChatMessage("[AutoNomadHut] Enabling");
        }
        this.playerHotbarSlot = Wrapper.getPlayer().inventory.currentItem;
        this.lastHotbarSlot = -1;
        if (this.debugMessages.getValue()) {
            Command.sendChatMessage("[AutoNomadHut] Saving initial Slot  = " + this.playerHotbarSlot);
        }
    }

    @Override
    protected void onDisable() {
        if (AutoNomadHut.mc.player == null) {
            return;
        }
        if (this.debugMessages.getValue()) {
            Command.sendChatMessage("[AutoNomadHut] Disabling");
        }
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[AutoNomadHut] Setting Slot to  = " + this.playerHotbarSlot);
            }
            if (this.spoofHotbar.getValue()) {
                AutoNomadHut.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.playerHotbarSlot));
            }
            else {
                Wrapper.getPlayer().inventory.currentItem = this.playerHotbarSlot;
            }
        }
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
    }

    private void endLoop() {
        this.offsetStep = 0;
        if (this.debugMessages.getValue()) {
            Command.sendChatMessage("[AutoNomadHut] Ending Loop");
        }
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[AutoNomadHut] Setting Slot back to  = " + this.playerHotbarSlot);
            }
            if (this.spoofHotbar.getValue()) {
                AutoNomadHut.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.playerHotbarSlot));
            }
            else {
                Wrapper.getPlayer().inventory.currentItem = this.playerHotbarSlot;
            }
            this.lastHotbarSlot = this.playerHotbarSlot;
        }
        if (!this.toggleable.getValue()) {
            this.disable();
        }
    }

    private void placeBlock(final BlockPos blockPos) {
        if (!Wrapper.getWorld().getBlockState(blockPos).getMaterial().isReplaceable()) {
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[AutoNomadHut] Block is already placed, skipping");
            }
            return;
        }
        if (!BlockInteractionHelper.checkForNeighbours(blockPos)) {
            if (this.debugMessages.getValue()) {
                Command.sendChatMessage("[AutoNomadHut] !checkForNeighbours(blockPos), disabling! ");
            }
            return;
        }
        this.placeBlockExecute(blockPos);
    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }

    public void placeBlockExecute(final BlockPos pos) {
        final Vec3d eyesPos = new Vec3d(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(), Wrapper.getPlayer().posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (!canBeClicked(neighbor)) {
                if (this.debugMessages.getValue()) {
                    Command.sendChatMessage("[AutoNomadHut] No neighbor to click at!");
                }
            }
            else {
                final Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) > 18.0625) {
                    if (this.debugMessages.getValue()) {
                        Command.sendChatMessage("[AutoNomadHut] Distance > 4.25 blocks!");
                    }
                }
                else {
                    boolean needSneak = false;
                    final Block blockBelow = AutoNomadHut.mc.world.getBlockState(neighbor).getBlock();
                    if (BlockInteractionHelper.blackList.contains(blockBelow) || BlockInteractionHelper.shulkerList.contains(blockBelow)) {
                        if (this.debugMessages.getValue()) {
                            Command.sendChatMessage("[AutoNomadHut] Sneak enabled!");
                        }
                        needSneak = true;
                    }
                    if (needSneak) {
                        AutoNomadHut.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoNomadHut.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    final int obiSlot = this.findObiInHotbar();
                    if (obiSlot == -1) {
                        if (this.debugMessages.getValue()) {
                            Command.sendChatMessage("[AutoNomadHut] No Obi in Hotbar, disabling!");
                        }
                        this.disable();
                        return;
                    }
                    if (this.lastHotbarSlot != obiSlot) {
                        if (this.debugMessages.getValue()) {
                            Command.sendChatMessage("[AutoNomadHut] Setting Slot to Obi at  = " + obiSlot);
                        }
                        if (this.spoofHotbar.getValue()) {
                            AutoNomadHut.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(obiSlot));
                        }
                        else {
                            Wrapper.getPlayer().inventory.currentItem = obiSlot;
                        }
                        this.lastHotbarSlot = obiSlot;
                    }
                    AutoNomadHut.mc.playerController.processRightClickBlock(Wrapper.getPlayer(), AutoNomadHut.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    AutoNomadHut.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                    if (needSneak) {
                        if (this.debugMessages.getValue()) {
                            Command.sendChatMessage("[AutoNomadHut] Sneak disabled!");
                        }
                        AutoNomadHut.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoNomadHut.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    }
                    return;
                }
            }
        }
    }

    private static boolean canBeClicked(final BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    private static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static IBlockState getState(final BlockPos pos) {
        return Wrapper.getWorld().getBlockState(pos);
    }

    private static void faceVectorPacketInstant(final Vec3d vec) {
        final float[] rotations = getLegitRotations(vec);
        Wrapper.getPlayer().connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], Wrapper.getPlayer().onGround));
    }

    private static float[] getLegitRotations(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { Wrapper.getPlayer().rotationYaw + MathHelper.wrapDegrees(yaw - Wrapper.getPlayer().rotationYaw), Wrapper.getPlayer().rotationPitch + MathHelper.wrapDegrees(pitch - Wrapper.getPlayer().rotationPitch) };
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(), Wrapper.getPlayer().posZ);
    }
}
