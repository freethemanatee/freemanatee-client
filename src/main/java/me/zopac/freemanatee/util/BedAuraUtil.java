package me.zopac.freemanatee.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class BedAuraUtil {
    double oneB;
    double twoB;
    double threeB;
    double fourB;
    Minecraft mc = Minecraft.getMinecraft();
    public BlockPos beddistance(BlockPos one, BlockPos two, BlockPos three, BlockPos four) {
        oneB = mc.player.getDistanceSqToCenter(one);
        twoB = mc.player.getDistanceSqToCenter(two);
        threeB = mc.player.getDistanceSqToCenter(three);
        fourB = mc.player.getDistanceSqToCenter(four);
        if(oneB < twoB && oneB < threeB && oneB < fourB && hasObsidianAbove(one)) { return one; }
        else if(twoB < threeB && twoB < fourB && hasObsidianAbove(two)) { return two; }
        else if(threeB < fourB && hasObsidianAbove(three)) { return three; }
        else if(hasObsidianAbove(four)) { return four; }
        else {  return null;  }
    }
    public boolean hasObsidianAbove(BlockPos block) {
        BlockPos above = new BlockPos(block.getX(), block.getY() +1, block.getZ());
        IBlockState state = mc.world.getBlockState(above);
        return state.getBlock() == Blocks.AIR;
    }
}
