package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.EntityUtil;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

@Module.Info(
        name = "ESP",
        category = Module.Category.RENDER
)
public class ESP extends Module {

    private Setting mode;
    private Setting players;
    private Setting animals;
    private Setting mobs;

    public ESP() {
        mode = this.register(Settings.e("Mode", ESP.ESPMode.RECTANGLE));
        players = this.register(Settings.b("Players", true));
        animals = this.register(Settings.b("Animals", false));
        mobs = this.register(Settings.b("Mobs", false));
    }

    public void onWorldRender(RenderEvent event) {
        if (Wrapper.getMinecraft().getRenderManager().options != null) {
            switch((ESP.ESPMode)this.mode.getValue()) {
                case RECTANGLE:
                    boolean isThirdPersonFrontal = Wrapper.getMinecraft().getRenderManager().options.thirdPersonView == 2;
                    float viewerYaw = Wrapper.getMinecraft().getRenderManager().playerViewY;
                    mc.world.loadedEntityList.stream().filter(EntityUtil::isLiving).filter((entity) -> {
                        return mc.player != entity;
                    }).map((entity) -> {
                        return (EntityLivingBase)entity;
                    }).filter((entityLivingBase) -> {
                        return !entityLivingBase.isDead;
                    }).filter((entity) -> {
                        return (Boolean)this.players.getValue() && entity instanceof EntityPlayer || EntityUtil.isPassive(entity) ? (Boolean)this.animals.getValue() : (Boolean)this.mobs.getValue();
                    }).forEach((e) -> {
                        GlStateManager.pushMatrix();
                        Vec3d pos = EntityUtil.getInterpolatedPos(e, event.getPartialTicks());
                        GlStateManager.translate(pos.x - mc.getRenderManager().renderPosX, pos.y - mc.getRenderManager().renderPosY, pos.z - mc.getRenderManager().renderPosZ);
                        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1), 1.0F, 0.0F, 0.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.depthMask(false);
                        GlStateManager.disableDepth();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
                        if (e instanceof EntityPlayer) {
                            GL11.glColor3f(1.0F, 1.0F, 1.0F);
                        } else if (EntityUtil.isPassive(e)) {
                            GL11.glColor3f(0.11F, 0.9F, 0.11F);
                        } else {
                            GL11.glColor3f(0.9F, 0.1F, 0.1F);
                        }

                        GlStateManager.disableTexture2D();
                        GL11.glLineWidth(2.0F);
                        GL11.glEnable(2848);
                        GL11.glBegin(2);
                        GL11.glVertex2d((double)(-e.width / 2.0F), 0.0D);
                        GL11.glVertex2d((double)(-e.width / 2.0F), (double)e.height);
                        GL11.glVertex2d((double)(e.width / 2.0F), (double)e.height);
                        GL11.glVertex2d((double)(e.width / 2.0F), 0.0D);
                        GL11.glEnd();
                        GlStateManager.popMatrix();
                    });
                    GlStateManager.enableDepth();
                    GlStateManager.depthMask(true);
                    GlStateManager.disableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.shadeModel(7425);
                    GlStateManager.disableDepth();
                    GlStateManager.enableCull();
                    GlStateManager.glLineWidth(1.0F);
                    GL11.glColor3f(1.0F, 1.0F, 1.0F);
                default:
            }
        }
    }

    public static enum ESPMode {
        RECTANGLE,
    }
}
