package me.zeroeightsix.kami.module.modules.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

@Module.Info(name = "BreakingWarning", category = Module.Category.RENDER)
public class BreakingWarning extends Module {

    private Setting<Integer> distanceToDetect = this.register(Settings.integerBuilder("Max Break Distance").withMinimum(1).withValue(2).withMaximum(5).build());
    private Setting<Boolean> announce = this.register(Settings.b("Announce in chat", false));
    private Setting<Integer> chatDelay = this.register(Settings.integerBuilder("Chat Delay").withMinimum(14).withValue(18).withMaximum(25).withVisibility(o -> announce.getValue()).build());

    private int delay;

    private boolean pastDistance(EntityPlayer player, BlockPos pos, double dist) {
        return player.getDistanceSqToCenter(pos) <= Math.pow(dist, 2.0);
    }

    @EventHandler
    public Listener<PacketEvent.Receive> packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;
        if (Objects.isNull((Object) player) || Objects.isNull((Object) world)) {
            return;
        }
        if (event.getPacket() instanceof SPacketBlockBreakAnim) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();
            BlockPos pos = packet.getPosition();
            if (this.pastDistance((EntityPlayer) player, pos, this.distanceToDetect.getValue())) {
                sendChat();
            }
        }
    }, new Predicate[0]);

    public void sendChat() {
        if (this.delay > this.chatDelay.getValue() && this.announce.getValue()) {
            this.delay = 0;
            mc.player.connection.sendPacket((Packet)new CPacketChatMessage("hey " + getPlayer() + " can you stop breaking that block"));
        }
        Command.sendChatMessage("yo dude someone is trying to break into your box, watch out");
        this.delay++;
    }

    public String getPlayer() {
        List<EntityPlayer> entities = new ArrayList<EntityPlayer>();
        entities.addAll(mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList()));
        for (EntityPlayer e : entities) {
            if (e.isDead || e.getHealth() <= 0.0f) continue;
            if (e.getName() == mc.player.getName()) continue;
            if (e.getHeldItemMainhand().getItem() instanceof ItemTool) {
                return e.getName();
            }
        }
        return "";
    }
}