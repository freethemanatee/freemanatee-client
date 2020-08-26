package me.zopac.freemanatee.module.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import me.zopac.freemanatee.event.events.RenderEvent;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.setting.Settings;
import me.zopac.freemanatee.util.ColourHolder;
import me.zopac.freemanatee.util.EntityUtil;
import me.zopac.freemanatee.util.Friends;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.IPlayerFileData;
import org.lwjgl.opengl.GL11;

@Module.Info(name="Nametags", description="Draws descriptive nametags above entities", category=Module.Category.RENDER)
public class Nametags
        extends Module {

    private Setting<Double> range = this.register(Settings.d("Range", 200.0));
    //private Setting<Float> scale = this.register(Settings.floatBuilder("Scale").withMinimum(Float.valueOf(0.5f)).withMaximum(Float.valueOf(10.0f)).withValue(Float.valueOf(2.5f)).build());
    private Setting<Boolean> health = this.register(Settings.b("Health", true));
    private Setting<Boolean> ping = this.register(Settings.b("Ping", true));
    private Setting<Boolean> armor = this.register(Settings.b("Armor", true));
    private Setting<Boolean> EnchantText = this.register(Settings.b("Enchants", false));
    @Override
    public void onWorldRender(RenderEvent event) {
        if (Nametags.mc.getRenderManager().options == null) {
            return;
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        Minecraft.getMinecraft().world.loadedEntityList.stream().filter(EntityUtil::isLiving).filter(entity -> !EntityUtil.isFakeLocalPlayer(entity)).filter(entity -> entity instanceof EntityPlayer).filter(entity -> (double)Nametags.mc.player.getDistance(entity) < this.range.getValue()).sorted(Comparator.comparing(entity -> Float.valueOf(-Nametags.mc.player.getDistance(entity)))).forEach(this::drawNametag);
        GlStateManager.disableTexture2D();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private void drawNametag(Entity entityIn) {
        GlStateManager.pushMatrix();
        Vec3d interp = EntityUtil.getInterpolatedRenderPos(entityIn, mc.getRenderPartialTicks());
        float yAdd = entityIn.height + 0.5f - (entityIn.isSneaking() ? 0.25f : 0.0f);
        double x = interp.x;
        double y = interp.y + (double)yAdd;
        double z = interp.z;
        float viewerYaw = Nametags.mc.getRenderManager().playerViewY;
        float viewerPitch = Nametags.mc.getRenderManager().playerViewX;
        boolean isThirdPersonFrontal = Nametags.mc.getRenderManager().options.thirdPersonView == 2;
        GlStateManager.translate((double)x, (double)y, (double)z);
        GlStateManager.rotate((float)(-viewerYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch), (float)1.0f, (float)0.0f, (float)0.0f);
        float f = Nametags.mc.player.getDistance(entityIn);
        //float m = f / 8.0f * (float)Math.pow(1.258925437927246, this.scale.getValue().floatValue());
        //GlStateManager.scale((float)m, (float)m, (float)m);
        FontRenderer fontRendererIn = Nametags.mc.fontRenderer;
        GlStateManager.scale((float)-0.025f, (float)-0.025f, (float)0.025f);
        String str = entityIn.getName() + (this.health.getValue() != false ? " " + this.getHealthColoured(entityIn, Math.round(((EntityLivingBase)entityIn).getHealth() + (entityIn instanceof EntityPlayer ? ((EntityPlayer)entityIn).getAbsorptionAmount() : 0.0f))) : "");
        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.disableDepth();
        GL11.glTranslatef((float)0.0f, (float)-20.0f, (float)0.0f);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)(-i - 1), 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.5f).endVertex();
        bufferbuilder.pos((double)(-i - 1), 19.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.5f).endVertex();
        bufferbuilder.pos((double)(i + 1), 19.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.5f).endVertex();
        bufferbuilder.pos((double)(i + 1), 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.5f).endVertex();
        tessellator.draw();
        bufferbuilder.begin(2, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)(-i - 1), 8.0, 0.0).color(0.1f, 0.1f, 0.1f, 0.1f).endVertex();
        bufferbuilder.pos((double)(-i - 1), 19.0, 0.0).color(0.1f, 0.1f, 0.1f, 0.1f).endVertex();
        bufferbuilder.pos((double)(i + 1), 19.0, 0.0).color(0.1f, 0.1f, 0.1f, 0.1f).endVertex();
        bufferbuilder.pos((double)(i + 1), 8.0, 0.0).color(0.1f, 0.1f, 0.1f, 0.1f).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        if (!entityIn.isSneaking()) {
            fontRendererIn.drawString(str, -i, 10, entityIn instanceof EntityPlayer ? (Friends.isFriend(entityIn.getName()) ? 49151 : 0xFFFFFF) : 0xFFFFFF);
        } else {
            fontRendererIn.drawString(str, -i, 10, 0xFFAA00);
        }
        if (entityIn instanceof EntityPlayer && this.armor.getValue().booleanValue()) {
            this.renderArmor((EntityPlayer)entityIn, 0, -(fontRendererIn.FONT_HEIGHT + 1) - 20);
        }
        GlStateManager.glNormal3f((float)0.0f, (float)0.0f, (float)0.0f);
        //GL11.glTranslatef((float)0.0f, (float)20.0f, (float)0.0f);
        GlStateManager.scale((float)-40.0f, (float)-40.0f, (float)40.0f);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public void renderArmor(EntityPlayer player, int x, int y) {
        InventoryPlayer items = player.inventory;
        ItemStack inHand = player.getHeldItemMainhand();
        ItemStack boots = items.armorItemInSlot(0);
        ItemStack leggings = items.armorItemInSlot(1);
        ItemStack body = items.armorItemInSlot(2);
        ItemStack helm = items.armorItemInSlot(3);
        ItemStack offHand = player.getHeldItemOffhand();
        ItemStack[] stuff = null;
        stuff = inHand != null && offHand != null ? new ItemStack[]{inHand, helm, body, leggings, boots, offHand} : (inHand != null && offHand == null ? new ItemStack[]{inHand, helm, body, leggings, boots} : (inHand == null && offHand != null ? new ItemStack[]{helm, body, leggings, boots, offHand} : new ItemStack[]{helm, body, leggings, boots}));
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        ItemStack[] array = stuff;
        int length = stuff.length;
        for (int j = 0; j < length; ++j) {
            ItemStack i = array[j];
            if (i == null || i.getItem() == null) continue;
            stacks.add(i);
        }
        int width = 16 * stacks.size() / 2;
        x -= width;
        GlStateManager.disableDepth();
        for (ItemStack stack : stacks) {
            this.renderItem(stack, x, y);
            x += 16;
        }
    }

    private String getHealthColoured(Entity entity, int health) {
        float max = ((EntityLivingBase)entity).getMaxHealth();
        int result = Math.round((float)health / max * 100.0f);
        if (result > 75) {
            return (Object)TextFormatting.GREEN + "" + health;
        }
        if (result > 50) {
            return (Object)TextFormatting.YELLOW + "" + health;
        }
        if (result > 25) {
            return (Object)TextFormatting.RED + "" + health;
        }
        return (Object)TextFormatting.DARK_RED + "" + health;
    }

    public void renderItem(ItemStack stack, int x, int y) {
        FontRenderer fontRenderer = Nametags.mc.fontRenderer;
        RenderItem renderItem = mc.getRenderItem();
        EnchantEntry[] enchants = new EnchantEntry[]{new EnchantEntry(Enchantments.PROTECTION, "Pro"), new EnchantEntry(Enchantments.THORNS, "Thr"), new EnchantEntry(Enchantments.SHARPNESS, "Sha"), new EnchantEntry(Enchantments.FIRE_ASPECT, "Fia"), new EnchantEntry(Enchantments.KNOCKBACK, "Knb"), new EnchantEntry(Enchantments.UNBREAKING, "Unb"), new EnchantEntry(Enchantments.POWER, "Pow"), new EnchantEntry(Enchantments.FIRE_PROTECTION, "Fpr"), new EnchantEntry(Enchantments.FEATHER_FALLING, "Fea"), new EnchantEntry(Enchantments.BLAST_PROTECTION, "Bla"), new EnchantEntry(Enchantments.PROJECTILE_PROTECTION, "Ppr"), new EnchantEntry(Enchantments.RESPIRATION, "Res"), new EnchantEntry(Enchantments.AQUA_AFFINITY, "Aqu"), new EnchantEntry(Enchantments.DEPTH_STRIDER, "Dep"), new EnchantEntry(Enchantments.FROST_WALKER, "Fro"), new EnchantEntry(Enchantments.BINDING_CURSE, "Bin"), new EnchantEntry(Enchantments.SMITE, "Smi"), new EnchantEntry(Enchantments.BANE_OF_ARTHROPODS, "Ban"), new EnchantEntry(Enchantments.LOOTING, "Loo"), new EnchantEntry(Enchantments.SWEEPING, "Swe"), new EnchantEntry(Enchantments.EFFICIENCY, "Eff"), new EnchantEntry(Enchantments.SILK_TOUCH, "Sil"), new EnchantEntry(Enchantments.FORTUNE, "For"), new EnchantEntry(Enchantments.FLAME, "Fla"), new EnchantEntry(Enchantments.LUCK_OF_THE_SEA, "Luc"), new EnchantEntry(Enchantments.LURE, "Lur"), new EnchantEntry(Enchantments.MENDING, "Men"), new EnchantEntry(Enchantments.VANISHING_CURSE, "Van"), new EnchantEntry(Enchantments.PUNCH, "Pun")};
        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();
        float scale1 = 0.3f;
        GlStateManager.translate((float)(x - 3), (float)(y + 8), (float)0.0f);
        GlStateManager.scale((float)0.3f, (float)0.3f, (float)0.3f);
        GlStateManager.popMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.zLevel = -100.0f;
        GlStateManager.disableDepth();
        renderItem.renderItemIntoGUI(stack, x, y);
        renderItem.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, null);
        GlStateManager.enableDepth();
        GlStateManager.scale((float)0.75f, (float)0.75f, (float)0.75f);
        if (stack.isItemStackDamageable()) {
            this.drawDamage(stack, x, y);
        }
        GlStateManager.scale((float)1.33f, (float)1.33f, (float)1.33f);
        EnchantEntry[] array = enchants;
        int length = enchants.length;
        for (int i = 0; i < length; ++i) {
            EnchantEntry enchant = array[i];
            int level = EnchantmentHelper.getEnchantmentLevel((Enchantment)enchant.getEnchant(), (ItemStack)stack);
            String levelDisplay = "" + level;
            if (level > 10) {
                levelDisplay = "10+";
            }
            if (level <= 0) continue;
            float scale2 = 0.32f;
            GlStateManager.translate((float)(x - 1), (float)(y + 2), (float)0.0f);
            GlStateManager.scale((float)0.42f, (float)0.42f, (float)0.42f);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            if (this.EnchantText.getValue()) {
                fontRenderer.drawString("\u00a7f" + enchant.getName() + " " + levelDisplay, (float) (20 - fontRenderer.getStringWidth("\u00a7f" + enchant.getName() + " " + levelDisplay) / 2), 0.0f, Color.WHITE.getRGB(), true);
            }
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale((float)2.42f, (float)2.42f, (float)2.42f);
            GlStateManager.translate((float)(-x + 1), (float)(-y), (float)0.0f);
            y += (int)((float)(fontRenderer.FONT_HEIGHT + 3) * 0.28f);
        }
        renderItem.zLevel = 0.0f;
        //RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

   public void drawDamage(ItemStack itemstack, int x, int y) {
        float green = ((float)itemstack.getMaxDamage() - (float)itemstack.getItemDamage()) / (float)itemstack.getMaxDamage();
        float red = 1.0f - green;
        int dmg = 100 - (int)(red * 100.0f);
        GlStateManager.disableDepth();
        //Nametags.mc.fontRenderer.drawStringWithShadow(dmg + "", (float)(x + 8) - (float)Nametags.mc.fontRenderer.getStringWidth(dmg + "") / 2.0f, (float)(y - 0), ColourHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
        GlStateManager.enableDepth();
    }

    public static class EnchantEntry {
        private Enchantment enchant;
        private String name;

        public EnchantEntry(Enchantment enchant, String name) {
            this.enchant = enchant;
            this.name = name;
        }

        public Enchantment getEnchant() {
            return this.enchant;
        }

        public String getName() {
            return this.name;
        }
    }
}

