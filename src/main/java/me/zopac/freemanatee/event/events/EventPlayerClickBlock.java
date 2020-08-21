package me.zopac.freemanatee.event.events;

import me.zopac.freemanatee.event.KamiEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventPlayerClickBlock
        extends KamiEvent {

    public BlockPos Location;
    public EnumFacing Facing;

    public EventPlayerClickBlock(BlockPos loc, EnumFacing face) {
        this.Location = loc;
        this.Facing = face;
    }
}

