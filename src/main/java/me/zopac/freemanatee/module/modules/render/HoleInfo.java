package me.zopac.freemanatee.module.modules.render;

import java.awt.Color;
import me.zopac.freemanatee.util.BlocksUtils;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

@Module.Info(name="HoleInfo", category=Module.Category.RENDER)
public class HoleInfo
        extends Module {

    private Setting<Integer> xpos = register(Settings.integerBuilder("X").withMinimum(0).withValue(400).withMaximum(1500).build());
    private Setting<Integer> ypos = register(Settings.integerBuilder("Y").withMinimum(0).withValue(400).withMaximum(1500).build());
    Color c;
    boolean font;
    Color color;
    boolean bedrock;
    boolean obby;
    boolean safe;

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask((boolean)true);
        GlStateManager.clear((int)256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale((float)1.01f, (float)1.01f, (float)0.01f);
    }

    private static void postitemrender() {
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glPopMatrix();
    }

    @Override
    public void onRender() {
        this.doStuff();
        this.renderHole(this.xpos.getValue(), this.ypos.getValue());
    }

    private void doStuff() {
        this.bedrock = this.northBrock() && this.eastBrock() && this.southBrock() && this.westBrock();
        this.obby = !(this.bedrock || !this.northObby() && !this.northBrock() || !this.eastObby() && !this.eastBrock() || !this.southObby() && !this.southBrock() || !this.westObby() && !this.westBrock());
        this.safe = this.obby || this.bedrock;
    }

    private void renderHole(double x, double y) {

        double leftX = x;
        double leftY = y + 16.0;
        double upX = x + 16.0;
        double upY = y;
        double rightX = x + 32.0;
        double rightY = y + 16.0;
        double bottomX = x + 16.0;
        double bottomY = y + 32.0;

        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        switch (mc.getRenderViewEntity().getHorizontalFacing()) {
            case NORTH: {
                if (this.northObby() || this.northBrock()) {
                    this.renderItem(upX, upY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.north()).getBlock()));
                }
                if (this.westObby() || this.westBrock()) {
                    this.renderItem(leftX, leftY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.west()).getBlock()));
                }
                if (this.eastObby() || this.eastBrock()) {
                    this.renderItem(rightX, rightY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.east()).getBlock()));
                }
                if (!this.southObby() && !this.southBrock()) break;
                this.renderItem(bottomX, bottomY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.south()).getBlock()));
                break;
            }
            case SOUTH: {
                if (this.southObby() || this.southBrock()) {
                    this.renderItem(upX, upY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.south()).getBlock()));
                }
                if (this.eastObby() || this.eastBrock()) {
                    this.renderItem(leftX, leftY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.east()).getBlock()));
                }
                if (this.westObby() || this.westBrock()) {
                    this.renderItem(rightX, rightY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.west()).getBlock()));
                }
                if (!this.northObby() && !this.northBrock()) break;
                this.renderItem(bottomX, bottomY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.north()).getBlock()));
                break;
            }
            case WEST: {
                if (this.westObby() || this.westBrock()) {
                    this.renderItem(upX, upY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.west()).getBlock()));
                }
                if (this.southObby() || this.southBrock()) {
                    this.renderItem(leftX, leftY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.south()).getBlock()));
                }
                if (this.northObby() || this.northBrock()) {
                    this.renderItem(rightX, rightY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.north()).getBlock()));
                }
                if (!this.eastObby() && !this.eastBrock()) break;
                this.renderItem(bottomX, bottomY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.east()).getBlock()));
                break;
            }
            case EAST: {
                if (this.eastObby() || this.eastBrock()) {
                    this.renderItem(upX, upY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.east()).getBlock()));
                }
                if (this.northObby() || this.northBrock()) {
                    this.renderItem(leftX, leftY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.north()).getBlock()));
                }
                if (this.southObby() || this.southBrock()) {
                    this.renderItem(rightX, rightY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.south()).getBlock()));
                }
                if (!this.westObby() && !this.westBrock()) break;
                this.renderItem(bottomX, bottomY, new ItemStack(HoleInfo.mc.world.getBlockState(playerPos.west()).getBlock()));
            }
        }
    }

    private void renderItem(double x, double y, ItemStack is) {
        HoleInfo.preitemrender();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(is, (int)x, (int)y);
        RenderHelper.disableStandardItemLighting();
        HoleInfo.postitemrender();
    }

    private boolean northObby() {
        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        return HoleInfo.mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.OBSIDIAN;
    }

    private boolean eastObby() {
        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        return HoleInfo.mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.OBSIDIAN;
    }

    private boolean southObby() {
        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        return HoleInfo.mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.OBSIDIAN;
    }

    private boolean westObby() {
        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        return HoleInfo.mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.OBSIDIAN;
    }

    private boolean northBrock() {
        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        return HoleInfo.mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.BEDROCK;
    }

    private boolean eastBrock() {
        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        return HoleInfo.mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.BEDROCK;
    }

    private boolean southBrock() {
        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        return HoleInfo.mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.BEDROCK;
    }

    private boolean westBrock() {
        Vec3d vec3d = BlocksUtils.getInterpolatedPos((Entity)HoleInfo.mc.player, 0.0f);
        BlockPos playerPos = new BlockPos(vec3d);
        return HoleInfo.mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.BEDROCK;
    }
}

