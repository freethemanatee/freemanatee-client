package me.zeroeightsix.kami.module.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.chat.AutoGG;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import me.zeroeightsix.kami.util.EntityUtil;
import me.zeroeightsix.kami.util.Friends;
import me.zeroeightsix.kami.util.KamiTessellator2;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
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
import net.minecraft.world.World;

@Module.Info(name="AutoCrystal2", category=Module.Category.COMBAT)
public class AutoCrystal2 extends Module {

    private Setting<Boolean> place = this.register(Settings.b("Place", true));
    private Setting<Boolean> explode = this.register(Settings.b("Explode", true));
    private Setting<Boolean> experamental = this.register(Settings.b("Experimental", false));
    private Setting<Boolean> autoSwitch = this.register(Settings.b("Auto Switch", true));
    private Setting<Boolean> antiWeakness = this.register(Settings.b("Anti Weakness", false));
    private Setting<Boolean> spoofRotations = this.register(Settings.b("Spoof Rotations", false));
    private Setting<Boolean> cancelMining = this.register(Settings.b("Cancel Mining", true));
    private Setting<Boolean> fastMode = this.register(Settings.b("Fast Mode", true));
    private Setting<Boolean> debug = this.register(Settings.b("Debug Messages", false));
    private Setting<Integer> hitTickDelay = this.register(Settings.integerBuilder("Delay").withMinimum(0).withValue(4).withMaximum(6).build());
    private Setting<Double> hitRange = this.register(Settings.doubleBuilder("Hit Range").withMinimum(0.0).withValue(4.5).build());
    private Setting<Double> placeRange = this.register(Settings.doubleBuilder("Place Range").withMinimum(0.0).withValue(4.5).build());
    private Setting<Double> minDamage = this.register(Settings.doubleBuilder("Min Damage").withMinimum(0.0).withValue(6.0).withMaximum(20.0).build());
    private Setting<Double> maxSelf = this.register(Settings.doubleBuilder("Max Self").withMinimum(0.0).withValue(7.0).withMaximum(20.0).build());
    private Setting<Boolean> rainbow = this.register(Settings.b("RainbowMode", true));
    private Setting<Integer> red = this.register(Settings.integerBuilder("Red").withRange(0, 255).withValue(255).withVisibility(o -> !rainbow.getValue()).build());
    private Setting<Integer> green = this.register(Settings.integerBuilder("Green").withRange(0, 255).withValue(255).withVisibility(o -> !rainbow.getValue()).build());
    private Setting<Integer> blue = this.register(Settings.integerBuilder("Blue").withRange(0, 255).withValue(255).withVisibility(o -> !rainbow.getValue()).build());
    private Setting<Float> satuation = this.register(Settings.floatBuilder("Saturation").withRange(0.0f, 1.0f).withValue(0.6f).withVisibility(o -> rainbow.getValue()).build());
    private Setting<Float> brightness = this.register(Settings.floatBuilder("Brightness").withRange(0.0f, 1.0f).withValue(0.6f).withVisibility(o -> rainbow.getValue()).build());
    private Setting<Integer> speed = this.register(Settings.integerBuilder("Speed").withRange(0, 10).withValue(2).withVisibility(o -> rainbow.getValue()).build());
    private Setting<Integer> alpha = this.register(Settings.integerBuilder("Transparency").withRange(0, 255).withValue(70).build());
    private Setting<RenderMode> renderMode = this.register(Settings.e("Render Mode", RenderMode.SOLID));
    private Setting<Boolean> faceplace = this.register(Settings.b("Faceplace", true));
    private Setting<Double> faceplacedmg = this.register(Settings.doubleBuilder("Enemy Health Min").withMinimum(0.0).withValue(8.0).withMaximum(20.0).withVisibility(o -> faceplace.getValue()).build());


    private BlockPos renderBlock;
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private boolean flag = false;
    private boolean shouldBreak = false;
    private static boolean togglePitch = false;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;
    private int oldSlot = -1;
    private int newSlot;
    private int hitDelayCounter;
    private float hue;
    private Color rgbc;
    public Set<EntityPlayer> fuckedPlayers;

    @EventHandler
    private Listener<PacketEvent.Send> packetListener = new Listener<PacketEvent.Send>(event -> {
        if (!this.spoofRotations.getValue().booleanValue()) {
            return;
        }
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && isSpoofingAngles) {
            ((CPacketPlayer)packet).yaw = (float)yaw;
            ((CPacketPlayer)packet).pitch = (float)pitch;
        }
    }, new Predicate[0]);

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(AutoCrystal2.mc.player.posX), Math.floor(AutoCrystal2.mc.player.posY), Math.floor(AutoCrystal2.mc.player.posZ));
    }

    static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double)doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = AutoCrystal2.getBlastReduction((EntityLivingBase)entity, AutoCrystal2.getDamageMultiplied(damage), new Explosion((World) AutoCrystal2.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }

    private static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer)entity;
            DamageSource ds = DamageSource.causeExplosionDamage((Explosion)explosion);
            damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)ep.getTotalArmorValue(), (float)((float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
            int k = EnchantmentHelper.getEnchantmentModifierDamage((Iterable)ep.getArmorInventoryList(), (DamageSource)ds);
            float f = MathHelper.clamp((float)k, (float)0.0f, (float)20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)entity.getTotalArmorValue(), (float)((float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
        return damage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = AutoCrystal2.mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = AutoCrystal2.mc.player.rotationYaw;
            pitch = AutoCrystal2.mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (mc.getRenderManager().options == null) return;
        if (this.renderBlock != null) {
            if (rainbow.getValue()) {
                this.rgbc = Color.getHSBColor(this.hue, this.satuation.getValue(), this.brightness.getValue());
                this.drawBlock(this.renderBlock, rgbc.getRed(), rgbc.getGreen(), rgbc.getBlue());
                if (this.hue + ( (float) speed.getValue()/200) > 1) {
                    this.hue = 0;
                } else {
                    this.hue += ( (float) speed.getValue()/200);
                }
            } else {
                this.drawBlock(this.renderBlock, this.red.getValue(), this.green.getValue(), this.blue.getValue());
            }
        }
    }

    private void drawBlock(final BlockPos blockPos, final int r, final int g, final int b) {
        final Color color = new Color(r, g, b, this.alpha.getValue());
        KamiTessellator2.prepare(7);
        if (this.renderMode.getValue().equals(RenderMode.UP)) {
            KamiTessellator2.drawBox(blockPos, color.getRGB(), 2);
        } else if (this.renderMode.getValue().equals(RenderMode.SOLID)) {
            KamiTessellator2.drawBox(blockPos, color.getRGB(), 63);
        } else if (this.renderMode.getValue().equals(RenderMode.OUTLINE)) {
            IBlockState iBlockState2 = AutoCrystal2.mc.world.getBlockState(blockPos);
            Vec3d interp2 = interpolateEntity((Entity) AutoCrystal2.mc.player, mc.getRenderPartialTicks());
            KamiTessellator2.drawBoundingBox(iBlockState2.getSelectedBoundingBox((World) AutoCrystal2.mc.world, blockPos).offset(-interp2.x, -interp2.y, -interp2.z), 1.5f, r, g, b, this.alpha.getValue());
        } else {
            IBlockState iBlockState3 = AutoCrystal2.mc.world.getBlockState(blockPos);
            Vec3d interp3 = interpolateEntity((Entity) AutoCrystal2.mc.player, mc.getRenderPartialTicks());
            KamiTessellator2.drawFullBox(iBlockState3.getSelectedBoundingBox((World) AutoCrystal2.mc.world, blockPos).offset(-interp3.x, -interp3.y, -interp3.z), blockPos, 1.5f, r, g, b, this.alpha.getValue());
        }
        KamiTessellator2.release();
    }

    public Boolean checkHole(EntityPlayer ent) {
        BlockPos pos = new BlockPos(ent.posX, ent.posY-1, ent.posZ);
        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR) {
            if (canPlaceCrystal(pos.south()) || (canPlaceCrystal(pos.south().south()) && mc.world.getBlockState(pos.add(0, 1, 1)).getBlock() == Blocks.AIR)) {
                return true;
            } else if (canPlaceCrystal(pos.east()) || (canPlaceCrystal(pos.east().east()) && mc.world.getBlockState(pos.add(1, 1, 0)).getBlock() == Blocks.AIR)) {
                return true;
            } else if (canPlaceCrystal(pos.west()) || (canPlaceCrystal(pos.west().west()) && mc.world.getBlockState(pos.add(-1, 1, 0)).getBlock() == Blocks.AIR)) {
                return true;
            } else if (canPlaceCrystal(pos.north()) || (canPlaceCrystal(pos.north().north()) && mc.world.getBlockState(pos.add(0, 1, -1)).getBlock() == Blocks.AIR)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void placeCrystals() {
        int crystalSlot;
        int crys = 1;
        if (this.oldSlot != -1) {
            AutoCrystal2.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.isAttacking = false;
        int n = crystalSlot = AutoCrystal2.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? AutoCrystal2.mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (AutoCrystal2.mc.player.inventory.getStackInSlot(l).getItem() != Items.END_CRYSTAL) continue;
                crystalSlot = l;
                break;
            }
        }
        boolean offhand = false;
        if (AutoCrystal2.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }
        for (int i = 0; i < crys; i++) {
            List<BlockPos> blocks = this.findCrystalBlocks();
            List<Entity> entities = new ArrayList<Entity>();
            entities.addAll(mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList()));
            BlockPos targetBlock = null;
            double targetBlockDamage = 0.0;
            EntityPlayer target = null;
            double minDam = this.minDamage.getValue();
            for (Entity entity2 : entities) {
                if (entity2 == AutoCrystal2.mc.player || !(entity2 instanceof EntityPlayer)) continue;
                EntityPlayer testTarget = (EntityPlayer)entity2;
                if (testTarget.isDead || testTarget.getHealth() <= 0.0f || testTarget.getDistanceSq(AutoCrystal2.mc.player.getPosition()) >= 169.0) continue;
                for (BlockPos blockPos : blocks) {
                    if (testTarget.getDistanceSq(blockPos) >= 169.0) continue;
                    double targetDamage = AutoCrystal2.calculateDamage((double)blockPos.x + 0.5, blockPos.y + 1, (double)blockPos.z + 0.5, (Entity)testTarget);
                    double selfDamage = AutoCrystal2.calculateDamage((double)blockPos.x + 0.5, blockPos.y + 1, (double)blockPos.z + 0.5, (Entity) AutoCrystal2.mc.player);
                    float healthTarget = testTarget.getHealth() + testTarget.getAbsorptionAmount();
                    float healthSelf = AutoCrystal2.mc.player.getHealth() + AutoCrystal2.mc.player.getAbsorptionAmount();
                    if (testTarget.getHealth() < faceplacedmg.getValue() && faceplace.getValue()) {
                        minDam = 0.1;
                    }
                    if (targetDamage < minDam || selfDamage >= (double)healthSelf - 0.5 || selfDamage > targetDamage && targetDamage < (double)healthTarget || !(targetDamage > targetBlockDamage) || selfDamage > maxSelf.getValue() || mc.player.getHealth() - selfDamage < 2) continue;
                    targetBlock = blockPos;
                    targetBlockDamage = targetDamage;
                    target = testTarget;
                }
            }
            if (target == null) {
                this.renderBlock = null;
                AutoCrystal2.resetRotation();
                return;
            }
            this.renderBlock = targetBlock;
            if (ModuleManager.getModuleByName("AutoGG").isEnabled()) {
                AutoGG autoGG = (AutoGG)ModuleManager.getModuleByName("AutoGG");
                autoGG.addTargetedPlayer(target.getName());
            }
            if (this.place.getValue().booleanValue()) {
                if (!offhand && AutoCrystal2.mc.player.inventory.currentItem != crystalSlot) {
                    if (this.autoSwitch.getValue().booleanValue()) {
                        AutoCrystal2.mc.player.inventory.currentItem = crystalSlot;
                        AutoCrystal2.resetRotation();
                        this.switchCooldown = true;
                    }
                    return;
                }
                this.lookAtPacket((double)targetBlock.x + 0.5, (double)targetBlock.y - 0.5, (double)targetBlock.z + 0.5, (EntityPlayer) AutoCrystal2.mc.player);
                RayTraceResult result = AutoCrystal2.mc.world.rayTraceBlocks(new Vec3d(AutoCrystal2.mc.player.posX, AutoCrystal2.mc.player.posY + (double) AutoCrystal2.mc.player.getEyeHeight(), AutoCrystal2.mc.player.posZ), new Vec3d((double)targetBlock.x + 0.5, (double)targetBlock.y - 0.5, (double)targetBlock.z + 0.5));
                EnumFacing f = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
                if (this.switchCooldown) {
                    this.switchCooldown = false;
                    return;
                }
                if (this.debug.getValue()) {
                    Command.sendChatMessage("placing crystal");
                }
                AutoCrystal2.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(targetBlock, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            }
            if (this.spoofRotations.getValue().booleanValue() && isSpoofingAngles) {
                if (togglePitch) {
                    AutoCrystal2.mc.player.rotationPitch = (float)((double) AutoCrystal2.mc.player.rotationPitch + 4.0E-4);
                    togglePitch = false;
                } else {
                    AutoCrystal2.mc.player.rotationPitch = (float)((double) AutoCrystal2.mc.player.rotationPitch - 4.0E-4);
                    togglePitch = true;
                }
            }
        }
    }

    public void breakCrystals(EntityEnderCrystal crystal) {
        if (crystal == null) {
            return;
        }
        if (this.shouldBreak) {
            this.hitDelayCounter = 0;
            if (this.antiWeakness.getValue().booleanValue() && AutoCrystal2.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!this.isAttacking) {
                    this.oldSlot = AutoCrystal2.mc.player.inventory.currentItem;
                    this.isAttacking = true;
                }
                this.newSlot = -1;
                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = AutoCrystal2.mc.player.inventory.getStackInSlot(i);
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
                    AutoCrystal2.mc.player.inventory.currentItem = this.newSlot;
                    this.switchCooldown = true;
                }
            }
                double selfDamage = AutoCrystal2.calculateDamage(crystal.posX + 0.5, crystal.posY + 1, crystal.posZ + 0.5, (Entity) AutoCrystal2.mc.player);
                if (selfDamage < this.maxSelf.getValue() && mc.player.getHealth() - selfDamage > 0) {
                    if (this.debug.getValue()) {
                        Command.sendChatMessage("hitting crystal");
                    }
                    this.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, (EntityPlayer) AutoCrystal2.mc.player);
                    AutoCrystal2.mc.playerController.attackEntity((EntityPlayer) AutoCrystal2.mc.player, (Entity)crystal);
                    AutoCrystal2.mc.player.swingArm(EnumHand.MAIN_HAND);
                    return;
                }
            } else {
                if (this.debug.getValue()) {
                    Command.sendChatMessage("hitting crystal");
                }
                this.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, (EntityPlayer) AutoCrystal2.mc.player);
                AutoCrystal2.mc.playerController.attackEntity((EntityPlayer) AutoCrystal2.mc.player, (Entity)crystal);
                AutoCrystal2.mc.player.swingArm(EnumHand.MAIN_HAND);
                return;
            }
        }


    @Override
    public void onUpdate() {
        if (mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE && !cancelMining.getValue() && mc.player.isHandActive()) {
            return;
        }
        EntityEnderCrystal crystal = AutoCrystal2.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).map(entity -> (EntityEnderCrystal)entity).min(Comparator.comparing(c -> Float.valueOf(AutoCrystal2.mc.player.getDistance((Entity)c)))).orElse(null);
        if (crystal != null && (double) AutoCrystal2.mc.player.getDistance((Entity)crystal) <= this.hitRange.getValue()) {
            this.shouldBreak = true;
            if (this.hitDelayCounter < this.hitTickDelay.getValue()) {
                ++this.hitDelayCounter;
                this.shouldBreak = false;
                if (!this.fastMode.getValue()) {
                    return;
                }
            }
        }
        if (this.experamental.getValue()) {
            if (this.explode.getValue().booleanValue()) {
                placeCrystals();
            }
            if (this.place.getValue().booleanValue()) {
                breakCrystals(crystal);
            }
        } else {
            if (this.explode.getValue().booleanValue()) {
                breakCrystals(crystal);
            }
            if (this.place.getValue().booleanValue()) {
                placeCrystals();
            }
        }
        AutoCrystal2.resetRotation();
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
        AutoCrystal2.setYawAndPitch((float)v[0], (float)v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (AutoCrystal2.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || AutoCrystal2.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && AutoCrystal2.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && AutoCrystal2.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && AutoCrystal2.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && AutoCrystal2.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)BlockInteractionHelper.getSphere(AutoCrystal2.getPlayerPos(), this.placeRange.getValue().floatValue(), this.placeRange.getValue().intValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
    }

    private static enum RenderMode {
        SOLID,
        OUTLINE,
        UP,
        FULL;
    }

    @Override
    public void onEnable() {
        fuckedPlayers = new HashSet<EntityPlayer>();
        if (mc.player == null) {
            this.disable();
            return;
        }
        Command.sendChatMessage(ChatFormatting.GREEN.toString() + " manatee is free");
        this.hitDelayCounter = this.hitTickDelay.getValue();
        this.hue = 0f;
    }

    @Override
    public void onDisable() {
        this.renderBlock = null;
        AutoCrystal2.resetRotation();
        Command.sendChatMessage(ChatFormatting.RED.toString() + " manatee is no longer free");
    }
}