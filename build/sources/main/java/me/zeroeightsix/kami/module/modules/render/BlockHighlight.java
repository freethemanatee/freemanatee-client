package me.zeroeightsix.kami.module.modules.render;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.state.IBlockState;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import me.zeroeightsix.kami.util.MathUtil;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.client.Minecraft;
import me.zeroeightsix.kami.event.events.RenderEvent;
import net.minecraftforge.common.MinecraftForge;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.setting.Setting;
import net.minecraft.util.math.BlockPos;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "Block Highlight", category = Module.Category.RENDER)
public class BlockHighlight extends Module
{
    private static BlockPos position;
    private Setting<Integer> red;
    private Setting<Integer> green;
    private Setting<Integer> blue;
    private Setting<Integer> alpha;
    private Setting<Float> width;

    public BlockHighlight() {
        this.red = this.register((Setting<Integer>)Settings.integerBuilder("Red").withRange(0, 255).withValue(255).build());
        this.green = this.register((Setting<Integer>)Settings.integerBuilder("Green").withRange(0, 255).withValue(0).build());
        this.blue = this.register((Setting<Integer>)Settings.integerBuilder("Blue").withRange(0, 255).withValue(0).build());
        this.alpha = this.register((Setting<Integer>)Settings.integerBuilder("Transparency").withRange(0, 255).withValue(70).build());
        this.width = this.register((Setting<Float>)Settings.floatBuilder("Thickness").withRange(1.0f, 10.0f).withValue(1.0f).build());
    }

    @Override
    protected void onEnable() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @Override
    protected void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
        BlockHighlight.position = null;
    }

    @Override
    public void onWorldRender(final RenderEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final RayTraceResult ray = mc.objectMouseOver;
        if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            final BlockPos blockpos = ray.getBlockPos();
            final IBlockState iblockstate = mc.world.getBlockState(blockpos);
            if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                final Vec3d interp = MathUtil.interpolateEntity((Entity)mc.player, mc.getRenderPartialTicks());
                KamiTessellator.drawBoundingBox(iblockstate.getSelectedBoundingBox((World)mc.world, blockpos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), this.width.getValue(), (int)this.red.getValue(), (int)this.green.getValue(), (int)this.blue.getValue(), (int)this.alpha.getValue());
            }
        }
    }

    @SubscribeEvent
    public void onDrawBlockHighlight(final DrawBlockHighlightEvent event) {
        if (BlockHighlight.mc.player == null || BlockHighlight.mc.world == null || (!BlockHighlight.mc.playerController.getCurrentGameType().equals((Object)GameType.SURVIVAL) && !BlockHighlight.mc.playerController.getCurrentGameType().equals((Object)GameType.CREATIVE))) {
            return;
        }
        event.setCanceled(true);
    }
}
