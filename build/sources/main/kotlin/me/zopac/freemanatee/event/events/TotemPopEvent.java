package me.zopac.freemanatee.event.events;

import me.zopac.freemanatee.event.KamiEvent;
import net.minecraft.entity.Entity;

public class TotemPopEvent extends KamiEvent {

    private Entity entity;

    public TotemPopEvent(Entity entity) {
        super();
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}