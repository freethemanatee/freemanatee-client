package me.zopac.freemanatee.module.modules.render;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zopac.freemanatee.event.events.PacketEvent;
import me.zopac.freemanatee.event.events.WorldCheckLightForEvent;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "NoRender", category = Module.Category.MISC)
public class NoRender extends Module {

    private Setting<Boolean> mob = register(Settings.b("Mob", true));
    private Setting<Boolean> sand = register(Settings.b("Sand", true));
    private Setting<Boolean> gentity = register(Settings.b("GEntity", true));
    private Setting<Boolean> object = register(Settings.b("Object", true));
    private Setting<Boolean> xp = register(Settings.b("XP", true));
    private Setting<Boolean> paint = register(Settings.b("Paintings", true));
    private Setting<Boolean> fire = register(Settings.b("Fire", true));
    private Setting<Boolean> explosion = register(Settings.b("Explosions", true));
    private Setting<Boolean> skylight = register(Settings.b("Skylight Updates", true));

    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        Packet packet = event.getPacket();
        if ((packet instanceof SPacketSpawnMob && mob.getValue()) ||
                (packet instanceof SPacketSpawnGlobalEntity && gentity.getValue()) ||
                (packet instanceof SPacketSpawnObject && object.getValue()) ||
                (packet instanceof SPacketSpawnExperienceOrb && xp.getValue()) ||
                (packet instanceof SPacketSpawnObject && sand.getValue()) ||
                (packet instanceof SPacketExplosion && explosion.getValue()) ||
                (packet instanceof SPacketSpawnPainting && paint.getValue()))
            event.cancel();
    });

    @EventHandler
    public Listener<RenderBlockOverlayEvent> blockOverlayEventListener = new Listener<>(event -> {
        if (fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE)
            event.setCanceled(true);
    });

    @SubscribeEvent
    public void onLightingUpdate(WorldCheckLightForEvent event) {
        if (skylight.getValue() && event.getEnumSkyBlock() == EnumSkyBlock.SKY) {
            event.setCanceled(true);
        }
    }

}