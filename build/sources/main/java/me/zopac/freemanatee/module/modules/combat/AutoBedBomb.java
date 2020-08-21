package me.zopac.freemanatee.module.modules.combat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.module.ModuleManager;
import me.zopac.freemanatee.module.modules.ECME.Auto32k;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.BlockInteractionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Info(name="AutoBedBomb", category=Module.Category.COMBAT)
public class AutoBedBomb
        extends Module {

    private static final DecimalFormat df = new DecimalFormat("#.#");
    private Setting<Boolean> rotate = this.register(Settings.b((String)"Rotate", (boolean)false));
    private Setting<Boolean> debugMessages = this.register(Settings.b((String)"Debug Messages", (boolean)false));
    private int stage;
    private BlockPos placeTarget;
    private int bedSlot;
    private boolean isSneaking;

    protected void onEnable() {
        if (Auto32k.mc.player == null || ModuleManager.isModuleEnabled((String)"Freecam")) {
            this.disable();
            return;
        }
        df.setRoundingMode(RoundingMode.CEILING);
        this.stage = 0;
        this.placeTarget = null;
        this.bedSlot = -1;
        this.isSneaking = false;
        for (int i = 0; i < 9 && this.bedSlot == -1; ++i) {
            ItemStack stack = Auto32k.mc.player.inventory.getStackInSlot(i);
            if (!(stack.getItem() instanceof ItemBed)) continue;
            this.bedSlot = 1;
            break;
        }
        if (this.bedSlot == -1) {
            if (((Boolean)this.debugMessages.getValue()).booleanValue()) {
                Command.sendChatMessage((String)"[AutoBedBomb] Bed(s) missing, disabling.");
            }
            this.disable();
            return;
        }
        if (Auto32k.mc.objectMouseOver == null || Auto32k.mc.objectMouseOver.getBlockPos() == null || Auto32k.mc.objectMouseOver.getBlockPos().up() == null) {
            if (((Boolean)this.debugMessages.getValue()).booleanValue()) {
                Command.sendChatMessage((String)"[AutoBedBomb] Not a valid place target, disabling.");
            }
            this.disable();
            return;
        }
        this.placeTarget = Auto32k.mc.objectMouseOver.getBlockPos().up();
        if (!((Boolean)this.debugMessages.getValue()).booleanValue()) {
            return;
        }
    }

    public void onUpdate() {
        if (Auto32k.mc.player == null) {
            return;
        }
        if (ModuleManager.isModuleEnabled((String)"Freecam")) {
            return;
        }
        if (this.stage == 0) {
            Auto32k.mc.player.inventory.currentItem = this.bedSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget), EnumFacing.DOWN);
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.placeTarget.add(0, 0, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            this.stage = 1;
            return;
        }
        this.disable();
    }

    private void placeBlock(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!this.isSneaking) {
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            BlockInteractionHelper.faceVectorPacketInstant((Vec3d)hitVec);
        }
        Auto32k.mc.playerController.processRightClickBlock(Auto32k.mc.player, Auto32k.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
}
