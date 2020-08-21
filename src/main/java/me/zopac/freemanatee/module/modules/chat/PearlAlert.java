package me.zopac.freemanatee.module.modules.chat;

import java.util.HashMap;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

@Module.Info(name="PearlAlert", category=Module.Category.CHAT)
public class PearlAlert
        extends Module {

    private Setting<Integer> range = this.register(Settings.i("Range", 300));
    private HashMap<Entity, Vec3d> knownPlayers = new HashMap();
    private HashMap<String, Vec3d> tpdPlayers = new HashMap();
    private int numTicks = 0;
    private int numForgetTicks = 0;

    @Override
    public void onUpdate() {
        if (!this.isEnabled()) {
            return;
        }
        if (this.numTicks >= 50) {
            this.numTicks = 0;
            for (Entity entity : PearlAlert.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityPlayer) || entity.getName().equals(PearlAlert.mc.player.getName())) continue;
                Vec3d playerPos = new Vec3d((double)((int)entity.posX), (double)((int)entity.posY), (double)((int)entity.posZ));
                if (this.knownPlayers.containsKey((Object)entity)) {
                    if (Math.abs(this.knownPlayers.get((Object)entity).distanceTo(playerPos)) > (double)this.range.getValue().intValue() && Math.abs(PearlAlert.mc.player.getPositionVector().distanceTo(playerPos)) > (double)(this.range.getValue() * 2) && (!this.tpdPlayers.containsKey(entity.getName()) || this.tpdPlayers.get(entity.getName()) != playerPos)) {
                        Command.sendChatMessage("Player " + entity.getName() + " teleported to " + PearlAlert.vectorToString(playerPos, new boolean[0]));
                        this.knownPlayers.remove((Object)entity);
                        this.tpdPlayers.put(entity.getName(), playerPos);
                    }
                    this.knownPlayers.put(entity, playerPos);
                    continue;
                }
                this.knownPlayers.put(entity, playerPos);
            }
        }
        if (this.numForgetTicks >= 9000000) {
            this.tpdPlayers.clear();
        }
        ++this.numTicks;
        ++this.numForgetTicks;
    }

    private static String vectorToString(Vec3d vector, boolean ... includeY) {
        boolean reallyIncludeY = includeY.length > 0 ? includeY[0] : true;
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append((int)Math.floor(vector.x));
        builder.append(", ");
        if (reallyIncludeY) {
            builder.append((int)Math.floor(vector.y));
            builder.append(", ");
        }
        builder.append((int)Math.floor(vector.z));
        builder.append(")");
        return builder.toString();
    }
}

