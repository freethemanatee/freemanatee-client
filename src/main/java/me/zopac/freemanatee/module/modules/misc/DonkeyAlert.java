package me.zopac.freemanatee.module.modules.misc;

import me.zopac.freemanatee.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;

import java.util.ArrayList;
import java.util.*;

import static me.zopac.freemanatee.util.MessageSendHelper.sendChatMessage;

@Module.Info(name = "DonkeyAlert", category = Module.Category.MISC)
public class DonkeyAlert extends Module {

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        List<Integer> tickPlayerList = new ArrayList<>();

        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityDonkey) {
                sendChatMessage("I found a donkey at: " + Math.round(entity.lastTickPosX) + " " + Math.round(entity.lastTickPosY) + " " + Math.round(entity.lastTickPosZ));
            }
        }
        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityLlama) {
                sendChatMessage("I found a llama at: " + Math.round(entity.lastTickPosX) + " " + Math.round(entity.lastTickPosY) + " " + Math.round(entity.lastTickPosZ));
            }
        }

        if (tickPlayerList.size() > 0) {

        }
    }
}