package me.zeroeightsix.kami.module.modules.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.chat.AutoEZ;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.EntityUtil;
import me.zeroeightsix.kami.util.Friends;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

@Module.Info(
        name = "AutoCrystal2",
        category = Module.Category.COMBAT
)
public class AutoCrystal2 extends Module {

    private Setting<Boolean> place = this.register(Settings.b("Place", true));
    private Setting<Boolean> explode = this.register(Settings.b("Explode", true));
    private Setting<Boolean> antiWeakness = this.register(Settings.b("Anti Weakness", false));
    private Setting<Boolean> offhand = this.register(Settings.b("Smart Offhand", false));
    private Setting<Integer> offhandHealth = this.register(Settings.integerBuilder("Offhand Min Health").withMinimum(0).withValue(10).withMaximum(20).withVisibility(o -> offhand.getValue()).build());
    private Setting<Integer> hitTickDelay = this.register(Settings.integerBuilder("Hit Delay").withMinimum(0).withValue(3).withMaximum(20).build());
    private Setting<Integer> placeTickDelay = this.register(Settings.integerBuilder("Place Delay").withMinimum(0).withValue(3).withMaximum(20).build());
    private Setting<Double> hitRange = this.register(Settings.doubleBuilder("Hit Range").withMinimum(0.0).withValue(5.0).build());
    private Setting<Double> placeRange = this.register(Settings.doubleBuilder("Place Range").withMinimum(0.0).withValue(5.0).build());
    private Setting<Double> minDamage = this.register(Settings.doubleBuilder("Min Damage").withMinimum(0.0).withValue(4.0).withMaximum(20.0).build());
    private Setting<Double> maxSelfDamage = this.register(Settings.doubleBuilder("Max Self Damage").withMinimum(0.0).withValue(4.0).withMaximum(20.0).build());
    private Setting<Boolean> rotate = this.register(Settings.b("Spoof Rotations", false));
    private Setting<Boolean> juan = this.register(Settings.b("juan mode", false));
    private Setting<Boolean> zopac = this.register(Settings.b("zopac mode", false));
    private Setting<Boolean> manatee = this.register(Settings.b("manatee mode", false));
    private Setting<PlaceMode> placeMode = this.register(Settings.e("Place Mode", PlaceMode.PLACEFIRST));
    private Setting<Boolean> debug = this.register(Settings.b("dev mode", false));
    private BlockPos renderBlock;
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private static boolean togglePitch = false;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;
    private int oldSlot = -1;
    private int newSlot;
    private int hitDelayCounter;
    private int placeDelayCounter;
    EntityEnderCrystal crystal;

    @EventHandler
    private Listener<PacketEvent.Send> packetListener = new Listener<PacketEvent.Send>(event -> {
        if (!this.rotate.getValue()) {
            return;
        }
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && isSpoofingAngles) {
            ((CPacketPlayer)packet).yaw = (float)yaw;
            ((CPacketPlayer)packet).pitch = (float)pitch;
        }
    }, new Predicate[0]);

    public void onUpdate() {

        if (mc.player.getHealth() >= (float) offhandHealth.getValue() && this.offhand.getValue()) {
            this.placeCrystalOffhand();
        }

        if (this.placeMode.getValue().equals(PlaceMode.PLACEFIRST)) {
            if (this.placeDelayCounter >= this.placeTickDelay.getValue()) {
                this.placeCrystal();
            }
            if (this.hitDelayCounter >= this.hitTickDelay.getValue()) {
                this.breakCrystal();
            }
        } else {
            if (this.hitDelayCounter >= this.hitTickDelay.getValue()) {
                this.breakCrystal();
            }
            if (this.placeDelayCounter >= this.placeTickDelay.getValue()) {
                this.placeCrystal();
            }
        }

        this.placeDelayCounter++;
        this.hitDelayCounter++;
        resetRotation();

    }

    public EntityEnderCrystal getBestCrystal(double range) {

        int totems = getTotems();
        double bestDam = 0;
        double minDam = this.minDamage.getValue();

        EntityEnderCrystal bestCrystal = null;
        Entity target = null;

        List<Entity> players = mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList());
        List<Entity> crystals = mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).collect(Collectors.toList());

        for (Entity crystal : crystals) {
            if (mc.player.getDistance((Entity) crystal) > range || crystal == null) continue;
            for (Entity player : players) {
                if (player == mc.player || !(player instanceof EntityPlayer)) continue;
                EntityPlayer testTarget = (EntityPlayer)player;
                if (testTarget.isDead || testTarget.getHealth() <= 0.0f || testTarget.getDistanceSq(mc.player.getPosition()) >= 169.0) {
                    if (this.debug.getValue()) {
                        Command.sendChatMessage("passing");
                    }
                    continue;
                }
                if (testTarget.getDistanceSq(crystal) >= 169.0) continue;

                if (testTarget.getHealth() > 12 && juan.getValue()) {
                    minDam = 7;
                }

                if (testTarget.getHealth() > 16 && manatee.getValue()) {
                    minDam = 12;
                }

                if (testTarget.getHealth() > 16 && zopac.getValue()) {
                    minDam = 12;
                }

                double targetDamage = calculateDamage(crystal.posX, crystal.posY, crystal.posZ, (Entity) testTarget);
                double selfDamage = calculateDamage(crystal.posX, crystal.posY, crystal.posZ, (Entity) mc.player);
                float healthTarget = testTarget.getHealth() + testTarget.getAbsorptionAmount();
                float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                if (targetDamage < minDam || selfDamage > targetDamage && targetDamage < (double)healthTarget || selfDamage > this.maxSelfDamage.getValue() || healthSelf < .5 && totems == 0) {
                    if (this.debug.getValue()) {
                        Command.sendChatMessage("too much self damage / too little damage");
                    }
                }
                if (targetDamage > bestDam) {
                    if (this.debug.getValue()) {
                        Command.sendChatMessage("hit");
                    }
                    target = player;
                    bestDam = targetDamage;
                    bestCrystal = (EntityEnderCrystal) crystal;
                }
            }
        }
        if (ModuleManager.getModuleByName("AutoEZ").isEnabled() && target != null) {
            AutoEZ autoGG = (AutoEZ)ModuleManager.getModuleByName("AutoEZ");
            autoGG.addTargetedPlayer(target.getName());
        }
        players.clear();
        crystals.clear();
        return bestCrystal;
    }

    public BlockPos getBestBlock() {
        List<BlockPos> blocks = this.findCrystalBlocks(this.placeRange.getValue());
        List<Entity> players = mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList());

        BlockPos targetBlock = null;
        EntityPlayer target = null;

        int totems = getTotems();
        double bestDam = 0;
        double minDam = this.minDamage.getValue();

        for (Entity player : players) {
            if (player == mc.player || !(player instanceof EntityPlayer)) continue;
            EntityPlayer testTarget = (EntityPlayer) player;
            if (testTarget.isDead || testTarget.getHealth() <= 0.0f || testTarget.getDistanceSq(mc.player.getPosition()) >= 169.0) continue;
            for (BlockPos blockPos : blocks) {
                if (testTarget.getDistanceSq(blockPos) >= 169.0) continue;
                double targetDamage = calculateDamage((double)blockPos.x + 0.5, blockPos.y + 1, (double)blockPos.z + 0.5, (Entity)testTarget);
                double selfDamage = calculateDamage((double)blockPos.x + 0.5, blockPos.y + 1, (double)blockPos.z + 0.5, (Entity)mc.player);
                float healthTarget = testTarget.getHealth() + testTarget.getAbsorptionAmount();
                float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                if (testTarget.getHealth() < 12 && juan.getValue()) {
                    minDam = 7;
                }

                if (testTarget.getHealth() < 11 && manatee.getValue()) {
                    minDam = 1.5;
                }

                if (testTarget.getHealth() < 16 && zopac.getValue()) {
                    minDam = 4;
                }

                if (targetDamage < minDam || selfDamage > targetDamage && targetDamage < (double)healthTarget || selfDamage > this.maxSelfDamage.getValue() || healthSelf < .5 && totems == 0) continue;
                if (targetDamage > bestDam) {
                    bestDam = targetDamage;
                    targetBlock = blockPos;
                    target = testTarget;
                }
            }
        }
        if (target == null) {
            this.renderBlock = null;
            resetRotation();
        }
        return targetBlock;
    }

    public void breakCrystal() {
        crystal = getBestCrystal(this.hitRange.getValue());
        if (crystal == null) {
            return;
        }
        if (this.explode.getValue()) {
            if (this.antiWeakness.getValue().booleanValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!this.isAttacking) {
                    this.oldSlot = mc.player.inventory.currentItem;
                    this.isAttacking = true;
                }
                this.newSlot = -1;
                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) continue;
                    if (stack.getItem() instanceof ItemSword) {
                        this.newSlot = i;
                        break;
                    }
                    if (!(stack.getItem() instanceof ItemTool)) continue;
                    this.newSlot = i;
                    break;
                }
                if (this.newSlot != -1) {
                    mc.player.inventory.currentItem = this.newSlot;
                    this.switchCooldown = true;
                }
            }
            if (this.debug.getValue()) {
                Command.sendChatMessage("hitting crystal");
            }
            this.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, (EntityPlayer)mc.player);
            mc.playerController.attackEntity((EntityPlayer)mc.player, (Entity)crystal);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        this.hitDelayCounter = 0;
    }

    public void placeCrystal() {

        // getting crystal slot
        int crystalSlot;
        if (this.oldSlot != -1) {
            mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.isAttacking = false;
        crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int i = 0; i < 9; i++) {
                if (mc.player.inventory.getStackInSlot(i).getItem() != Items.END_CRYSTAL) continue;
                crystalSlot = i;
                break;
            }
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        // getting & drawing on target block
        BlockPos targetBlock = getBestBlock();
        if (targetBlock == null) {
            return;
        }
        this.renderBlock = targetBlock;

        // placing
        if (this.place.getValue().booleanValue()) {
            if (!offhand && mc.player.inventory.currentItem != crystalSlot) { // cannot place
                mc.player.inventory.currentItem = crystalSlot;
                resetRotation();
                this.switchCooldown = true;
            }
            this.lookAtPacket((double)targetBlock.x + 0.5, (double)targetBlock.y - 0.5, (double)targetBlock.z + 0.5, (EntityPlayer)mc.player);
            // not a clue
            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double)targetBlock.x + 0.5, (double)targetBlock.y - 0.5, (double)targetBlock.z + 0.5));
            EnumFacing f = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;

            if (this.switchCooldown) {
                this.switchCooldown = false;
                return;
            }
            if (this.debug.getValue()) {
                Command.sendChatMessage("placing crystal");
            }
            mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(targetBlock, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        if (this.rotate.getValue().booleanValue() && isSpoofingAngles) {
            if (togglePitch) {
                mc.player.rotationPitch = (float)((double)mc.player.rotationPitch + 4.0E-4);
                togglePitch = false;
            } else {
                mc.player.rotationPitch = (float)((double)mc.player.rotationPitch - 4.0E-4);
                togglePitch = true;
            }
        }
        this.placeDelayCounter = 0;
    }

    private void placeCrystalOffhand() {
        int slot = this.findCrystalsInHotbar();
        if (this.getOffhand().getItem() == Items.END_CRYSTAL || slot == -1) {
            return;
        }
        if (this.debug.getValue()) {
            Command.sendChatMessage("swapping "+mc.player.inventory.getStackInSlot(45).getItem());
            Command.sendChatMessage("with "+mc.player.inventory.getStackInSlot(slot).getItem());
        }
        this.invPickup(slot);
        this.invPickup(45);
        this.invPickup(slot);
    }

    private void invPickup(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    }

    private ItemStack getOffhand() {
        return mc.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
    }

    public int getTotems() {
        return offhand() +  mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
    }

    public int offhand() {
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return 1;
        }
        return 0;
    }

    private int findCrystalsInHotbar() {
        int slot = -1;
        for (int i = 44; i >= 9; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                slot = i;
                break;
            }
        }
        return slot;
    }



    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private List<BlockPos> findCrystalBlocks(double range) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), (float) range, (int) range, false, true, 0)
                .stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = sphere ? (cy - (int) r) : cy; y < (sphere ? (cy + r) : (cy + h)); y++) {
                    double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
                    if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (int) ((v * v + v) / 2.0D * 9.0D * doubleExplosionSize + 1.0D);
        double finald = 1.0D;
        if (entity instanceof EntityLivingBase)
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage),
                    new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true));
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, ep.getTotalArmorValue(),
                    (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= (1.0F - f / 25.0F);
            if (entity.isPotionActive(Potion.getPotionById(11)))
                damage -= damage / 4.0F;
            return Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
        }
        return CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0F : ((diff == 2) ? 1.0F : ((diff == 1) ? 0.5F : 1.5F)));
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    protected void onEnable() {
        Command.sendChatMessage("\u00A7amanatee is free");
        if (mc.player == null) {
            this.disable();
            return;
        }
    }

    public void onDisable() {
        this.renderBlock = null;
        Command.sendChatMessage("\u00A7cmanatee is no longer free");
        resetRotation();
    }

    private static enum RenderMode {
        SOLID,
        OUTLINE,
        UP,
        FULL;
    }

    private static enum PlaceMode {
        PLACEFIRST,
        BREAKFIRST;
    }

}