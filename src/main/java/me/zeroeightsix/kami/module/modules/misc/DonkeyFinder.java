package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityDonkey;
import java.util.ArrayList;
import java.util.*;

import static me.zeroeightsix.kami.util.MessageSendHelper.sendChatMessage;

@Module.Info(name = "DonkeyFinder", category = Module.Category.MISC)
public class DonkeyFinder extends Module {

    private List<String> knownPlayers;
    boolean test = false;

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        List<Integer> tickPlayerList = new ArrayList<>();

        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityDonkey) {
                sendChatMessage("I found a donkey at: " + Math.round(entity.lastTickPosX) + " " + Math.round(entity.lastTickPosY) + " " + Math.round(entity.lastTickPosZ));
            }


            if (tickPlayerList.size() > 0) {

            }
        }
    }

    @Override
    public void onEnable() {
        this.knownPlayers = new ArrayList<>();

    }
}
