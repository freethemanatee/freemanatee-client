package me.zopac.freemanatee.module.modules.movement;

import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.*;
import net.minecraft.util.EnumHand;

import java.util.Comparator;

@Module.Info(
        name = "AutoMount",
        category = Module.Category.MOVEMENT
)

public class AutoMount extends Module {

    private Setting<Boolean> boat = register(Settings.b("Boats", true));
    private Setting<Boolean> horse = register(Settings.b("Horse", true));
    private Setting<Boolean> skeletonHorse = register(Settings.b("SkeletonHorse", true));
    private Setting<Boolean> donkey = register(Settings.b("Donkey", true));
    private Setting<Boolean> pig = register(Settings.b("Pig", true));
    private Setting<Boolean> llama = register(Settings.b("Llama", true));
    private Setting<Float> range = register(Settings.floatBuilder("Range").withMinimum(1.0f).withValue(2.0f).withMaximum(10.0f).build());

    public void onUpdate()
    {
        // we don't need to do anything if we're already riding.
        if (mc.player.isRiding())
            return;

        Entity entity = mc.world.loadedEntityList.stream()
                .filter(en -> isValidEntity(en))
                .min(Comparator.comparing(en -> mc.player.getDistance(en)))
                .orElse(null);

        if (entity != null)
            mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
    }

    private boolean isValidEntity(Entity entity)
    {
        if (entity.getDistance(mc.player) > range.getValue())
            return false;

        if (entity instanceof AbstractHorse)
        {
            AbstractHorse horse = (AbstractHorse) entity;

            /// no animal abuse done in this module, no thanks.
            if (horse.isChild())
                return false;
        }

        if (entity instanceof EntityBoat && boat.getValue())
            return true;

        if (entity instanceof EntityHorse && horse.getValue())
            return true;

        if (entity instanceof EntitySkeletonHorse && skeletonHorse.getValue())
            return true;

        if (entity instanceof EntityDonkey && donkey.getValue())
            return true;

        if (entity instanceof EntityPig && pig.getValue())
        {
            EntityPig pig = (EntityPig) entity;

            if (pig.getSaddled())
                return true;

            return false;
        }

        if (entity instanceof EntityLlama && llama.getValue())
            return true;

        return false;
    }
}
