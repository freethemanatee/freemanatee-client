package me.zopac.freemanatee.event.events;

import me.zopac.freemanatee.event.KamiEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class DamageBlockEvent
        extends KamiEvent {
    private BlockPos BlockPos;
    private EnumFacing Direction;

    public DamageBlockEvent(BlockPos posBlock, EnumFacing directionFacing) {
        this.BlockPos = posBlock;
        this.setDirection(directionFacing);
    }

    public BlockPos getPos() {
        return this.BlockPos;
    }

    public EnumFacing getDirection() {
        return this.Direction;
    }

    public void setDirection(EnumFacing direction) {
        this.Direction = direction;
    }
}

