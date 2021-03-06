package me.zopac.freemanatee.module.modules.dispenserpvp;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.event.events.PacketEvent;
import me.zopac.freemanatee.event.events.RenderEvent;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.KamiTessellator;
import me.zopac.freemanatee.util.ReflectionHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

@Module.Info(name = "IllegalItemBypass", category = Module.Category.dispenserpvp)
public class IllegalItemBypass extends Module {

    private Setting<Boolean> circleOwn = register(Settings.b("Draw Circle for own 32k Hopper", false));
    private Setting<Boolean> circleOthers = register(Settings.b("Draw Circle for other Hoppers", false));
    private Setting<Integer> circleMaxRange = register(Settings.i("Circle max Range", 64));

    private BlockPos ownHopper = null;

    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {

        if (isDisabled()) {
            return;
        }

        if (event.getPacket() instanceof CPacketCloseWindow) {
            event.cancel();
        }

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            if (mc.player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.HOPPER)) {
                BlockPos clickTarget = ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos();
                if (!(mc.world.getBlockState(clickTarget).getBlock() == Blocks.HOPPER)) {
                    ownHopper = clickTarget.add(0, 1, 0);
                }
            }
        }

    });

    @Override
    public void onWorldRender(RenderEvent event) {

        if (circleOwn.getValue()) {
            if (ownHopper != null) {
                if (mc.player.getDistance(ownHopper.x, ownHopper.y, ownHopper.z) <= circleMaxRange.getValue()) {
                    drawRange(ownHopper, Color.GREEN.getRed() / 255f, Color.GREEN.getGreen() / 255f, Color.BLUE.getBlue() / 255f);
                }
            }
        }

        if (circleOthers.getValue()) {
            for (Object tileEntity : mc.world.loadedTileEntityList) {
                if (tileEntity instanceof TileEntityHopper) {
                    BlockPos targetHopper = ((TileEntityHopper) tileEntity).getPos();
                    if (mc.player.getDistance(targetHopper.x, targetHopper.y, targetHopper.z) > circleMaxRange.getValue()) {
                        continue;
                    }
                    if (ownHopper != null && ((TileEntityHopper) tileEntity).getPos().equals(ownHopper)) {
                        continue;
                    }
                    drawRange(targetHopper, Color.RED.getRed() / 255f, Color.ORANGE.getGreen() / 255f, Color.ORANGE.getBlue() / 255f);
                }
            }
        }

    }

    private void drawRange(BlockPos blockPos, float red, float green, float blue) {

        KamiTessellator.prepare(GL11.GL_QUADS);

        double x = blockPos.getX() + 0.5 - ReflectionHelper.getRenderPosX();
        double y = blockPos.getY() - ReflectionHelper.getRenderPosY();
        double z = blockPos.getZ() + 0.5 - ReflectionHelper.getRenderPosZ();

        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL_BLEND);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4d(red, green, blue, 0.20);
        GL11.glBegin(9);

        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex3d(x + Math.sin(i * 3.1415 / 180.0) * (double) 7, y, z + Math.cos(i * 3.1415 / 180.0) * (double) 7);
        }

        GL11.glEnd();
        GL11.glLineWidth(2.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);

        KamiTessellator.release();

    }

}
