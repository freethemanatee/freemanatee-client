package me.zopac.freemanatee.module.modules.meme;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Info(name="AutoBedTrap", category=Module.Category.MEME)
public class AutoBedTrap
        extends Module {
    private Setting<Integer> range = this.register(Settings.i("Range", 4));
    private Setting<Boolean> rotate = this.register(Settings.b("Rotate", true));
    List<BlockPos> beds;
    int blocksPlaced;

    @Override
    protected void onEnable() {
        this.beds = new ArrayList<BlockPos>();
    }

    @Override
    public void onUpdate() {
        int reach;
        for (int y = reach = this.range.getValue().intValue(); y >= -reach; --y) {
            for (int x = -reach; x <= reach; ++x) {
                for (int z = -reach; z <= reach; ++z) {
                    BlockPos pos = new BlockPos(AutoBedTrap.mc.player.posX + (double)x, AutoBedTrap.mc.player.posY + (double)y, AutoBedTrap.mc.player.posZ + (double)z);
                    if (this.getFacingDirection(pos) == null || !this.blockChecks(AutoBedTrap.mc.world.getBlockState(pos).getBlock()) || !(AutoBedTrap.mc.player.getDistance(AutoBedTrap.mc.player.posX + (double)x, AutoBedTrap.mc.player.posY + (double)y, AutoBedTrap.mc.player.posZ + (double)z) < (double)AutoBedTrap.mc.playerController.getBlockReachDistance() - 0.2) || this.beds.contains((Object)pos)) continue;
                    this.beds.add(pos);
                }
            }
        }
        if (!this.beds.isEmpty()) {
            for (int m = 0; m < this.beds.size(); ++m) {
                BlockPos bed = this.beds.get(m);
                BlockPos x = bed.add(1, 0, 0);
                BlockPos negx = bed.add(-1, 0, 0);
                BlockPos y = bed.add(0, 1, 0);
                BlockPos z = bed.add(0, 0, 1);
                BlockPos negz = bed.add(0, 0, -1);
                int newSlot = -1;
                for (int i = 0; i < 9; ++i) {
                    Block block;
                    ItemStack stack = AutoBedTrap.mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || !((block = ((ItemBlock)stack.getItem()).getBlock()) instanceof BlockObsidian)) continue;
                    newSlot = i;
                    break;
                }
                if (newSlot == -1) {
                    return;
                }
                int oldSlot = AutoBedTrap.mc.player.inventory.currentItem;
                AutoBedTrap.mc.player.inventory.currentItem = newSlot;
                if (this.shouldPlace(x)) {
                    AutoBedTrap.placeBlockScaffold(x, this.rotate.getValue());
                }
                if (this.shouldPlace(negx)) {
                    AutoBedTrap.placeBlockScaffold(negx, this.rotate.getValue());
                }
                if (this.shouldPlace(y)) {
                    AutoBedTrap.placeBlockScaffold(y, this.rotate.getValue());
                }
                if (this.shouldPlace(z)) {
                    AutoBedTrap.placeBlockScaffold(z, this.rotate.getValue());
                }
                if (this.shouldPlace(negz)) {
                    AutoBedTrap.placeBlockScaffold(negz, this.rotate.getValue());
                }
                AutoBedTrap.mc.player.inventory.currentItem = oldSlot;
            }
        }
    }

    private boolean shouldPlace(BlockPos pos) {
        List entities = AutoBedTrap.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream().filter(e -> !(e instanceof EntityItem)).filter(e -> !(e instanceof EntityXPOrb)).collect(Collectors.toList());
        boolean a = entities.isEmpty();
        boolean b = AutoBedTrap.mc.world.getBlockState(pos).getMaterial().isReplaceable();
        return a && b;
    }

    public static boolean placeBlockScaffold(BlockPos pos, boolean rotate) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!AutoBedTrap.canBeClicked(neighbor)) continue;
            Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
            if (rotate) {
                AutoBedTrap.faceVectorPacketInstant(hitVec);
            }
            AutoBedTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoBedTrap.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            AutoBedTrap.processRightClickBlock(neighbor, side2, hitVec);
            AutoBedTrap.mc.player.swingArm(EnumHand.MAIN_HAND);
            AutoBedTrap.mc.rightClickDelayTimer = 0;
            AutoBedTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoBedTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            return true;
        }
        return false;
    }

    private static PlayerControllerMP getPlayerController() {
        return AutoBedTrap.mc.playerController;
    }

    public static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
        AutoBedTrap.getPlayerController().processRightClickBlock(AutoBedTrap.mc.player, AutoBedTrap.mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
    }

    public static IBlockState getState(BlockPos pos) {
        return AutoBedTrap.mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return AutoBedTrap.getState(pos).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos) {
        return AutoBedTrap.getBlock(pos).canCollideCheck(AutoBedTrap.getState(pos), false);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = AutoBedTrap.getNeededRotations2(vec);
        AutoBedTrap.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], AutoBedTrap.mc.player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = AutoBedTrap.getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{AutoBedTrap.mc.player.rotationYaw + MathHelper.wrapDegrees((float)(yaw - AutoBedTrap.mc.player.rotationYaw)), AutoBedTrap.mc.player.rotationPitch + MathHelper.wrapDegrees((float)(pitch - AutoBedTrap.mc.player.rotationPitch))};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(AutoBedTrap.mc.player.posX, AutoBedTrap.mc.player.posY + (double)AutoBedTrap.mc.player.getEyeHeight(), AutoBedTrap.mc.player.posZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(AutoBedTrap.getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return AutoBedTrap.getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    private boolean blockChecks(Block block) {
        return block == Blocks.BED;
    }

    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing direction = null;
        if (!AutoBedTrap.mc.world.getBlockState((BlockPos)pos.add((int)0, (int)1, (int)0)).getBlock().fullBlock && !(AutoBedTrap.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.UP;
        } else if (!AutoBedTrap.mc.world.getBlockState((BlockPos)pos.add((int)0, (int)-1, (int)0)).getBlock().fullBlock && !(AutoBedTrap.mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.DOWN;
        } else if (!AutoBedTrap.mc.world.getBlockState((BlockPos)pos.add((int)1, (int)0, (int)0)).getBlock().fullBlock && !(AutoBedTrap.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.EAST;
        } else if (!AutoBedTrap.mc.world.getBlockState((BlockPos)pos.add((int)-1, (int)0, (int)0)).getBlock().fullBlock && !(AutoBedTrap.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.WEST;
        } else if (!AutoBedTrap.mc.world.getBlockState((BlockPos)pos.add((int)0, (int)0, (int)1)).getBlock().fullBlock && !(AutoBedTrap.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.SOUTH;
        } else if (!AutoBedTrap.mc.world.getBlockState((BlockPos)pos.add((int)0, (int)0, (int)1)).getBlock().fullBlock && !(AutoBedTrap.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.NORTH;
        }
        RayTraceResult rayResult = AutoBedTrap.mc.world.rayTraceBlocks(new Vec3d(AutoBedTrap.mc.player.posX, AutoBedTrap.mc.player.posY + (double)AutoBedTrap.mc.player.getEyeHeight(), AutoBedTrap.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5));
        if (rayResult != null && rayResult.getBlockPos() == pos) {
            return rayResult.sideHit;
        }
        return direction;
    }
}

