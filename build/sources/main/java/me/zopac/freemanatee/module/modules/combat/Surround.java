package me.zopac.freemanatee.module.modules.combat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Module.Info(
        name = "Surround",
        category = Module.Category.COMBAT
)

public class Surround extends Module {

    private List whiteList;
    private Setting sneak;
    private Setting rotate;
    private Setting disableAfterPlacing;
    private Setting<Boolean> announce;

    public Surround() {
        this.whiteList = Arrays.asList(Blocks.OBSIDIAN);
        this.sneak = this.register(Settings.b("SneakOnly", false));
        this.rotate = this.register(Settings.b("Rotate", true));
        this.announce = this.register(Settings.b("Announce Usage", true));
        this.disableAfterPlacing = this.register(Settings.b("Toggleable", true));
    }

    public static boolean hasNeighbour(BlockPos blockPos) {
        EnumFacing[] var1 = EnumFacing.values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            EnumFacing side = var1[var3];
            BlockPos neighbour = blockPos.offset(side);
            if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }

        return false;
    }

    public void onUpdate() {
        if (!(Boolean) this.sneak.getValue() || mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (this.isEnabled() && mc.player != null) {
                Vec3d vec3d = getInterpolatedPos(mc.player, 0.0F);
                BlockPos northBlockPos = (new BlockPos(vec3d)).north();
                BlockPos southBlockPos = (new BlockPos(vec3d)).south();
                BlockPos eastBlockPos = (new BlockPos(vec3d)).east();
                BlockPos westBlockPos = (new BlockPos(vec3d)).west();
                int newSlot = -1;

                int oldSlot;
                for (oldSlot = 0; oldSlot < 9; ++oldSlot) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(oldSlot);
                    if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                        Block block = ((ItemBlock) stack.getItem()).getBlock();
                        if (this.whiteList.contains(block)) {
                            newSlot = oldSlot;
                            break;
                        }
                    }
                }

                if (newSlot != -1) {
                    oldSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = newSlot;
                    int var10;
                    EnumFacing side;
                    BlockPos neighbour;
                    EnumFacing[] var13;
                    int var14;
                    if (!hasNeighbour(northBlockPos)) {
                        var13 = EnumFacing.values();
                        var14 = var13.length;
                        var10 = 0;

                        while (true) {
                            if (var10 >= var14) {
                                return;
                            }

                            side = var13[var10];
                            neighbour = northBlockPos.offset(side);
                            if (hasNeighbour(neighbour)) {
                                northBlockPos = neighbour;
                                break;
                            }

                            ++var10;
                        }
                    }

                    if (!hasNeighbour(southBlockPos)) {
                        var13 = EnumFacing.values();
                        var14 = var13.length;
                        var10 = 0;

                        while (true) {
                            if (var10 >= var14) {
                                return;
                            }

                            side = var13[var10];
                            neighbour = southBlockPos.offset(side);
                            if (hasNeighbour(neighbour)) {
                                southBlockPos = neighbour;
                                break;
                            }

                            ++var10;
                        }
                    }

                    if (!hasNeighbour(eastBlockPos)) {
                        var13 = EnumFacing.values();
                        var14 = var13.length;
                        var10 = 0;

                        while (true) {
                            if (var10 >= var14) {
                                return;
                            }

                            side = var13[var10];
                            neighbour = eastBlockPos.offset(side);
                            if (hasNeighbour(neighbour)) {
                                eastBlockPos = neighbour;
                                break;
                            }

                            ++var10;
                        }
                    }

                    if (!hasNeighbour(westBlockPos)) {
                        var13 = EnumFacing.values();
                        var14 = var13.length;
                        var10 = 0;

                        while (true) {
                            if (var10 >= var14) {
                                return;
                            }

                            side = var13[var10];
                            neighbour = westBlockPos.offset(side);
                            if (hasNeighbour(neighbour)) {
                                westBlockPos = neighbour;
                                break;
                            }

                            ++var10;
                        }
                    }

                    if (mc.world.getBlockState(northBlockPos).getMaterial().isReplaceable() && this.isEntitiesEmpty(northBlockPos)) {
                        placeBlockScaffold(northBlockPos, (Boolean) this.rotate.getValue());
                    }

                    if (mc.world.getBlockState(southBlockPos).getMaterial().isReplaceable() && this.isEntitiesEmpty(southBlockPos)) {
                        placeBlockScaffold(southBlockPos, (Boolean) this.rotate.getValue());
                    }

                    if (mc.world.getBlockState(eastBlockPos).getMaterial().isReplaceable() && this.isEntitiesEmpty(eastBlockPos)) {
                        placeBlockScaffold(eastBlockPos, (Boolean) this.rotate.getValue());
                    }

                    if (mc.world.getBlockState(westBlockPos).getMaterial().isReplaceable() && this.isEntitiesEmpty(westBlockPos)) {
                        placeBlockScaffold(westBlockPos, (Boolean) this.rotate.getValue());
                    }

                    mc.player.inventory.currentItem = oldSlot;
                    if ((Boolean) this.disableAfterPlacing.getValue()) {
                        this.disable();
                    }

                }
            }
        }
    }

    private boolean isEntitiesEmpty(BlockPos pos) {
        List entities = (List) mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(pos)).stream().filter((e) -> {
            return !(e instanceof EntityItem);
        }).filter((e) -> {
            return !(e instanceof EntityXPOrb);
        }).collect(Collectors.toList());
        return entities.isEmpty();
    }

    public static boolean placeBlockScaffold(BlockPos pos, boolean rotate) {
        new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
        EnumFacing[] var3 = EnumFacing.values();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            EnumFacing side = var3[var5];
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (canBeClicked(neighbor)) {
                Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));
                if (rotate) {
                    faceVectorPacketInstant(hitVec);
                }

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
                processRightClickBlock(neighbor, side2, hitVec);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.rightClickDelayTimer = 0;
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
                return true;
            }
        }

        return false;
    }

    private static PlayerControllerMP getPlayerController() {
        return mc.playerController;
    }

    public static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
        getPlayerController().processRightClickBlock(mc.player, mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getNeededRotations2(vec);
        mc.player.connection.sendPacket(new Rotation(rotations[0], rotations[1], mc.player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return (new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)).add(getInterpolatedAmount(entity, (double) ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public void onEnable() {
        if (mc.player != null && (Boolean) this.announce.getValue()) {
            Command.sendChatMessage("Surround " + ChatFormatting.GREEN + "Enabled");
        }

    }

    public void onDisable() {
        if (mc.player != null && (Boolean) this.announce.getValue()) {
            Command.sendChatMessage("Surround " + ChatFormatting.RED + "Disabled");
        }

    }
}
