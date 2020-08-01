package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.util.ColourUtils;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@Module.Info(name = "BaseESP", category = Module.Category.RENDER)
public class BaseESP extends Module {

    private int getTileEntityColor(TileEntity tileEntity) {
        if(tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityDispenser || tileEntity instanceof TileEntityShulkerBox)
            return ColourUtils.Colors.ORANGE;
        else if(tileEntity instanceof TileEntityEnderChest)
            return ColourUtils.Colors.PURPLE;
        else if(tileEntity instanceof TileEntityDropper)
            return ColourUtils.Colors.DARK_RED;
        else if(tileEntity instanceof TileEntityFurnace)
            return ColourUtils.Colors.RED;
        else if(tileEntity instanceof TileEntityEndGateway)
            return ColourUtils.Colors.PURPLE;
        else if(tileEntity instanceof TileEntityBrewingStand)
            return ColourUtils.Colors.ORANGE;
        else if(tileEntity instanceof TileEntityEnchantmentTable)
            return ColourUtils.Colors.BLUE;
        else if(tileEntity instanceof TileEntitySign)
            return ColourUtils.Colors.YELLOW;
        else if(tileEntity instanceof TileEntityHopper)
            return ColourUtils.Colors.GRAY;
        else if(tileEntity instanceof TileEntityBanner)
            return ColourUtils.Colors.GREEN;
        else if(tileEntity instanceof TileEntityEndPortal)
            return ColourUtils.Colors.RAINBOW;
        else if(tileEntity instanceof TileEntityBeacon)
            return ColourUtils.Colors.DARK_RED;
        else
            return -1;
    }

    private int getEntityColor(Entity entity) {
        if(entity instanceof EntityMinecartChest)
            return ColourUtils.Colors.BLUE;
        else if(entity instanceof EntityItemFrame &&
                ((EntityItemFrame) entity).getDisplayedItem().getItem() instanceof ItemShulkerBox)
            return ColourUtils.Colors.PURPLE;
        else
            return -1;
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        ArrayList<Triplet<BlockPos, Integer, Integer>> a = new ArrayList<>();
        GlStateManager.pushMatrix();

        for(TileEntity tileEntity : Wrapper.getWorld().loadedTileEntityList) {
            BlockPos pos = tileEntity.getPos();
            int color = getTileEntityColor(tileEntity);
            int side = GeometryMasks.Quad.ALL;
            if (tileEntity instanceof TileEntityChest) {
                TileEntityChest chest = (TileEntityChest) tileEntity;
                // Leave only the colliding face and then flip the bits (~) to have ALL but that face
                if (chest.adjacentChestZNeg != null) side = ~(side & GeometryMasks.Quad.NORTH);
                if (chest.adjacentChestXPos != null) side = ~(side & GeometryMasks.Quad.EAST);
                if (chest.adjacentChestZPos != null) side = ~(side & GeometryMasks.Quad.SOUTH);
                if (chest.adjacentChestXNeg != null) side = ~(side & GeometryMasks.Quad.WEST);
            }
            if(color != -1) a.add(new Triplet<>(pos, color, side)); //GeometryTessellator.drawCuboid(event.getBuffer(), pos, GeometryMasks.Line.ALL, color);
        }

        for(Entity entity : Wrapper.getWorld().loadedEntityList) {
            BlockPos pos = entity.getPosition();
            int color = getEntityColor(entity);
            if(color != -1) a.add(new Triplet<>(entity instanceof EntityItemFrame ? pos.add(0, -1, 0) : pos, color, GeometryMasks.Quad.ALL)); //GeometryTessellator.drawCuboid(event.getBuffer(), entity instanceof EntityItemFrame ? pos.add(0, -1, 0) : pos, GeometryMasks.Line.ALL, color);
        }

        KamiTessellator.prepare(GL11.GL_QUADS);
        for (Triplet<BlockPos, Integer, Integer> pair : a)
            KamiTessellator.drawBox(pair.getFirst(), changeAlpha(pair.getSecond(), 100), pair.getThird());
        KamiTessellator.release();

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }

    int changeAlpha(int origColor, int userInputedAlpha) {
        origColor = origColor & 0x00ffffff; //drop the previous alpha value
        return (userInputedAlpha << 24) | origColor; //add the one the user inputted
    }

    public class Triplet<T, U, V> {

        private final T first;
        private final U second;
        private final V third;

        public Triplet(T first, U second, V third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public T getFirst() { return first; }
        public U getSecond() { return second; }
        public V getThird() { return third; }
    }
}
