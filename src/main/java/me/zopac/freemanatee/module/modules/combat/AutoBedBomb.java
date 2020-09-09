package me.zopac.freemanatee.module.modules.combat;

import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.Friends;
import me.zopac.freemanatee.util.BlocksUtils;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.util.BedAuraUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBed;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Module.Info(name = "AutoBedBomb", category = Module.Category.COMBAT)
public class AutoBedBomb extends Module {


    private Setting<Boolean> autoSwitch;
    private Setting<Boolean> antiSuicide;
    private Setting<Boolean> refill;
    private Setting<Boolean> rotate;

    private Setting<Integer> range;
    private Setting<Integer> switchDelay;
    private Setting<Integer> antiSuicideHlth;

    BlockPos targetBlock;
    BlockPos targetPlayer;

    BlockPos west;
    BlockPos east;
    BlockPos north;
    BlockPos south;

    EnumFacing facing;

    BlockPos targetPlace;

    BedAuraUtils util = new BedAuraUtils();

    public AutoBedBomb() {
        this.range = register(Settings.integerBuilder("Range").withMinimum(0).withMaximum(10).withValue(5));
        this.switchDelay = register(Settings.integerBuilder("Switch Delay").withMinimum(0).withMaximum(10).withValue(3));
        this.antiSuicideHlth = this.register((Setting<Integer>) Settings.integerBuilder("Anti Suicide Health").withMinimum(0).withMaximum(36).withValue(16).withVisibility(b -> antiSuicide.getValue()).build());
        this.antiSuicide = this.register(Settings.b("AntiSuicide", true));
        this.autoSwitch = this.register(Settings.b("AutoSwitch", true));
        this.refill = this.register(Settings.b("Auto Refill", true));
        this.rotate = this.register(Settings.b("Rotate", true));
    }
    boolean moving = false;
    @Override
    public void onUpdate() {
        mc.world.loadedTileEntityList.stream()
                .filter(e -> e instanceof TileEntityBed)
                .filter(e -> mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ()) <= range.getValue())
                .sorted(Comparator.comparing(e -> mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ())))
                .forEach(bed -> {
                    if (mc.player.dimension == 0) return;
                        if (rotate.getValue())
                            BlocksUtils.faceVectorPacketInstant(new Vec3d(bed.getPos().add(0.5, 0.5, 0.5)));
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bed.getPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                        return;
                });
        if(refill.getValue()) {
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                if (mc.player.inventory.getStackInSlot(i) == ItemStack.EMPTY) {
                    slot = i;
                    break;
                }
            }
            if (moving && slot != -1) {
                mc.playerController.windowClick(0, slot + 36, 0, ClickType.PICKUP, mc.player);
                moving = false;
                slot = -1;
            }
            int bedSlot = -1;
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack.getItem() instanceof ItemBed) {
                    bedSlot = 1;
                    break;
                }
            }
            if (slot != -1 && !(mc.currentScreen instanceof GuiContainer) && mc.player.inventory.getItemStack().isEmpty()) {
                int t = -1;
                for (int i = 0; i < 45; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.BED && i >= 9) {
                        t = i;
                        break;
                    }
                }
                if (bedSlot != -1 && mc.player.ticksExisted % switchDelay.getValue() == 0) {
                    mc.player.inventory.currentItem = bedSlot;
                    mc.playerController.updateController();
                }
                if (t != -1) {
                    mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, mc.player);
                    moving = true;
                }
            }
        }
        if(mc.player.getHealth() < this.antiSuicideHlth.getValue() && this.antiSuicide.getValue()) { this.disable(); }
        for (EntityPlayer player : getTargets()) {
            targetPlayer = new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ);
            if (targetPlayer.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {
                west = targetPlayer.west();
                east = targetPlayer.east();
                north = targetPlayer.north();
                south = targetPlayer.south();
                if(player.isElytraFlying()) {
                    targetBlock = util.getBedAuraDistance(west, east, north, south);
                    targetBlock = new BlockPos(targetBlock.getX(), targetBlock.getY() -1, targetBlock.getZ());
                } else {
                    targetBlock = util.getBedAuraDistance(west, east, north, south);
                }
                if (targetBlock != null) {
                    if (targetBlock == west) {
                        facing = EnumFacing.EAST;
                    }
                    if (targetBlock == east) {
                        facing = EnumFacing.WEST;
                    }
                    if (targetBlock == north) {
                        facing = EnumFacing.SOUTH;
                    }
                    if (targetBlock == south) {
                        facing = EnumFacing.NORTH;
                    }
                    if (facing != null) {
                        if (autoSwitch.getValue()) {
                            switchHandToItemIfNeed(Items.BED);
                        }
                        if (mc.player.getHeldItemMainhand().getItem() == Items.BED) {
                            targetPlace = new BlockPos(targetBlock.getX(), targetBlock.getY() + 1, targetBlock.getZ());
                            placeBlock(targetPlace, facing);
                        }
                    }
                }
            }
        }
    }
    public List<EntityPlayer> getTargets() {
        return mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList());
    }
    private void placeBlock(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = facing;
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
        } return true;
    }
}