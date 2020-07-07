package me.zeroeightsix.kami.module.modules.ECME;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.KamiTessellator;
import me.zeroeightsix.kami.util.ReflectionHelper;
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

/**
 * Created 26 October 2019 by hub
 * Updated 27 October 2019 by hub
 * <p>
 * RusherHack leak skid :P
 */
@Module.Info(name = "IllegalItemBypass", category = Module.Category.ECME, description = "Illegal Item Bypass")
public class IllegalItemBypass extends Module {

    private Setting<Boolean> circleOwn = register(Settings.b("Draw Circle for own 32k Hopper", true));
    private Setting<Boolean> circleOthers = register(Settings.b("Draw Circle for other Hoppers", true));
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

        // TODO - find a better way of doing this (action on hopper placement), maybe there is an handy event?
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            if (mc.player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.HOPPER)) {
                BlockPos clickTarget = ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos();
                if (!(mc.world.getBlockState(clickTarget).getBlock() == Blocks.HOPPER)) {
                    // TODO - instead of just using y+1, there should be a raytrace here to check for manually placed hoppers that were placed against a blockside.
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
