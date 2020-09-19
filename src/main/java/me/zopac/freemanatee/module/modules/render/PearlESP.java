package me.zopac.freemanatee.module.modules.render;

import java.awt.Color;
import me.zopac.freemanatee.event.events.RenderEvent;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.KamiTessellator;
import me.zopac.freemanatee.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;

@Module.Info(name="PearlESP", category=Module.Category.RENDER)
public class PearlESP
        extends Module {
    private Setting<Integer> r = this.register(Settings.integerBuilder("Red").withMinimum(0).withMaximum(255).withValue(0).build());
    private Setting<Integer> g = this.register(Settings.integerBuilder("Green").withMinimum(0).withMaximum(255).withValue(255).build());
    private Setting<Integer> b = this.register(Settings.integerBuilder("Blue").withMinimum(0).withMaximum(255).withValue(30).build());
    private Setting<Integer> a = this.register(Settings.integerBuilder("Alpha").withMinimum(0).withMaximum(255).withValue(255).build());

    @Override
    public void onWorldRender(RenderEvent event) {
        for (Entity e : PearlESP.mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderPearl)) continue;
            Vec3d vec = MathUtil.interpolateEntity(e, event.getPartialTicks());
            double posX = vec.x - (double)PearlESP.mc.player.renderOffsetX;
            double posY = vec.y - (double)PearlESP.mc.player.renderOffsetY;
            double posZ = vec.z - (double)PearlESP.mc.player.renderOffsetZ;
            Color color = new Color(this.r.getValue(), this.g.getValue(), this.b.getValue(), this.a.getValue());
            KamiTessellator.prepare(7);
            KamiTessellator.drawBox((int)posX, (int)posY, (int)posZ, color.getRGB(), 63);
            KamiTessellator.release();
        }
    }
}

