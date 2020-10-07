package me.zopac.freemanatee.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zopac.freemanatee.command.Command;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityBed;
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

@Module.Info(name = "BedBombRewrite", category = Module.Category.COMBAT)
public class BedBombRewrite extends Module {

    private Setting<Boolean> announceusage;
    private Setting<Boolean> autobedswitch;
    private Setting<Boolean> antiSuicide;
    private Setting<Boolean> refill;

    private Setting<Integer> antiSuicideHlth;
    private Setting<Integer> range;

    BlockPos target;

    public BedBombRewrite() {

        this.antiSuicideHlth = this.register((Setting<Integer>) Settings.integerBuilder("Anti Suicide Health").withMinimum(0).withMaximum(36).withValue(16).withVisibility(b -> antiSuicide.getValue()).build());
        this.range = register(Settings.integerBuilder("Range").withMinimum(0).withMaximum(10).withValue(6));
        this.announceusage = this.register(Settings.b("Announce Usage", false));
        this.autobedswitch = this.register(Settings.b("Auto Switch", false));
        this.antiSuicide = this.register(Settings.b("AntiSuicide", true));
        this.refill = this.register(Settings.b("Refill", false));

    }
    @Override
    public void onUpdate() {
        if(mc.player.getHealth() < this.antiSuicideHlth.getValue() && this.antiSuicide.getValue()) { this.disable(); }
        for (EntityPlayer player : getTargets()) {
            target = new BlockPos(player.posX, player.posY, player.posZ);
            if (mc.player.getHeldItemMainhand().getItem() == ItemBlock.getItemById(355)) {
                if (mc.world.getBlockState(target.up().up()) != Blocks.AIR) {
                    placeBlock(target.up(), EnumFacing.DOWN);
                }
            }
        }
        if (mc.player.dimension != 0) {
            mc.world.loadedTileEntityList.stream()
                    .filter((e) -> e instanceof TileEntityBed)
                    .filter((e) -> mc.player.getPosition().getDistance(e.getPos().x, e.getPos().y, e.getPos().z) <= range.getValue())
                    .map((entity) -> (TileEntityBed) entity)
                    .min(Comparator.comparing((e) -> mc.player.getPosition().getDistance(e.getPos().x, e.getPos().y, e.getPos().z)))
                    .ifPresent(bed -> mc.playerController.processRightClickBlock(mc.player, mc.world, bed.getPos(), EnumFacing.UP, new Vec3d(bed.getPos().getX(), bed.getPos().getY(), bed.getPos().getZ()), EnumHand.MAIN_HAND));
        } else {
            MessageSendHelper.sendErrorMessage("Exploding beds only works in the nether and in the end, disabling!");
            disable();
        }
        if(refill.getValue()) {
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                if (mc.player.inventory.getStackInSlot(i) == ItemStack.EMPTY) {
                    slot = i;
                    break;
                }
            }
        }
    }
    @Override
    public void onEnable() {
        if (autobedswitch.getValue()) {
            switchHandToItemIfNeed(ItemBlock.getItemById(355));
        }
        if (announceusage.getValue()) {
            Command.sendChatMessage("BedBombRewrite" + ChatFormatting.GREEN.toString() + " Enabled");
        }
    }
    @Override
    public void onDisable() {
        if (announceusage.getValue()) {
            Command.sendChatMessage("BedBombRewrite" + ChatFormatting.RED.toString() + " Disabled");
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