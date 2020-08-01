package me.zeroeightsix.kami.util;

import me.zeroeightsix.kami.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Blocks;

public class ModuleMan {

    public Integer totems;
    private String holeType;
    private BlockPos pos;

    public ModuleMan() {
        this.holeType = "\u00A74 0";
        getPlayerPos();
    }

    public Boolean getPlayerPos() {
        try {
            this.pos = new BlockPos(Math.floor(Minecraft.getMinecraft().player.posX), Math.floor(Minecraft.getMinecraft().player.posY), Math.floor(Minecraft.getMinecraft().player.posZ));
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public String getHoleType() {

        if (getPlayerPos()) {
            return "\u00A74 0";
        }

        getPlayerPos();

        if ((Minecraft.getMinecraft().world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK)
                && (Minecraft.getMinecraft().world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK)
                && (Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK)
                && (Minecraft.getMinecraft().world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK)
                && (Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK)) {
            this.holeType = "\u00A72 Safe";
            return this.holeType;
        }

        else if ((Minecraft.getMinecraft().world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK
                | Minecraft.getMinecraft().world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN)
                && (Minecraft.getMinecraft().world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK
                | Minecraft.getMinecraft().world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN)
                && (Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK
                | Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN)
                && (Minecraft.getMinecraft().world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK
                | Minecraft.getMinecraft().world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN)
                && (Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK
                | Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN)) {
            this.holeType = "\u00A76 Unsafe";
            return this.holeType;
        }

        else {
            this.holeType = "\u00A78 None";
            return this.holeType;
        }
    }

    public String isAura() {
        try {
            if (ModuleManager.getModuleByName("AutoCrystal").isEnabled()) {
                return "\u00A7a CA";
            }
            return "\u00A74 CA";
        } catch (Exception e) {
            return "lack of games: "+e;
        }
    }

    public String isAura2() {
        try {
            if (ModuleManager.getModuleByName("AutoCrystal2").isEnabled()) {
                return "\u00A7a CA2";
            }
            return "\u00A74 CA2";
        } catch (Exception e) {
            return "lack of games: "+e;
        }
    }

    public String isTrap() {
        try {
            if (ModuleManager.getModuleByName("AutoTrap").isEnabled()) {
                return "\u00A7a AT";
            }
            return "\u00A74 AT";
        } catch (Exception e) {
            return "lack of games: "+e;
        }
    }

    public String isSurround() {
        try {
            if (ModuleManager.getModuleByName("Surround").isEnabled()) {
                return "\u00A7a SU";
            }
            return "\u00A74 SU";
        } catch (Exception e) {
            return "lack of games: "+e;
        }
    }

    public String isFill() {
        try {
            if (ModuleManager.getModuleByName("HoleFiller").isEnabled()) {
                return "\u00A7a HF";
            }
            return "\u00A74 HF";
        } catch (Exception e) {
            return "lack of games: "+e;
        }
    }

    public int getTotemsInt() {
        return offhand() +  Minecraft.getMinecraft().player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
    }

    public String getTotems() {
        try {
            totems = offhand() + Minecraft.getMinecraft().player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();

            if (totems > 1) {
                return "\u00A7a "+totems;
            } else {
                return "\u00a7b "+totems;
            }

        } catch (Exception e) {
            return "0";
        }
    }

    public Integer offhand() {
        if (Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return 1;
        }
        return 0;
    }

}