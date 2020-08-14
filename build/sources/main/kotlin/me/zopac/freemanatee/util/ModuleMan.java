package me.zopac.freemanatee.util;

import me.zopac.freemanatee.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Blocks;

public class ModuleMan {

    private String holeType;
    private BlockPos pos;

    public ModuleMan() {
        this.holeType = "\u00A74 None";
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
            return "\u00A74 None";
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
            return "i broke a thing: " + e;
        }
    }

    public String isAura2() {
        try {
            if (ModuleManager.getModuleByName("AutoCrystal2").isEnabled()) {
                return "\u00A7a CA2";
            }
            return "\u00A74 CA2";
        } catch (Exception e) {
            return "i broke a thing: " + e;
        }
    }
    public String isTrap() {
        try {
            if (ModuleManager.getModuleByName("AutoTrap").isEnabled()) {
                return "\u00A7a AT";
            }
            return "\u00A74 AT";
        } catch (Exception e) {
            return "i broke a thing: "+e;
        }
    }

    public String isSurround() {
        try {
            if (ModuleManager.getModuleByName("Surround").isEnabled()) {
                return "\u00A7a SU";
            }
            return "\u00A74 SU";
        } catch (Exception e) {
            return "i broke a thing: "+e;
        }
    }

    public String isSelfTrap() {
        try {
            if (ModuleManager.getModuleByName("SelfTrap").isEnabled()) {
                return "\u00A7a ST";
            }
            return "\u00A74 ST";
        } catch (Exception e) {
            return "i broke a thing: "+e;
        }
    }

    public String isFill() {
        try {
            if (ModuleManager.getModuleByName("HoleFiller").isEnabled()) {
                return "\u00A7a HF";
            }
            return "\u00A74 HF";
        } catch (Exception e) {
            return "i broke a thing: "+e;
        }
    }

}