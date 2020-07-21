

package me.zeroeightsix.kami.module.modules.render;

import net.minecraft.client.Minecraft;
import me.zeroeightsix.kami.util.ColourHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.setting.Setting;
import net.minecraft.client.renderer.RenderItem;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "ArmorHUD", description = "shows armor durability", category = Module.Category.RENDER)
public class ArmorHUD extends Module
{
    private static RenderItem itemRender;
    private Setting<Boolean> damage;

    public ArmorHUD() {
        this.damage = this.register(Settings.b("Damage", true));
    }

    @Override
    public void onRender() {
        GlStateManager.enableTexture2D();
        final ScaledResolution resolution = new ScaledResolution(ArmorHUD.mc);
        final int i = resolution.getScaledWidth() / 2;
        int iteration = 0;
        final int y = resolution.getScaledHeight() - 55 - (ArmorHUD.mc.player.isInWater() ? 10 : 0);
        for (final ItemStack is : ArmorHUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            ArmorHUD.itemRender.zLevel = 200.0f;
            ArmorHUD.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            ArmorHUD.itemRender.renderItemOverlayIntoGUI(ArmorHUD.mc.fontRenderer, is, x, y, "");
            ArmorHUD.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            ArmorHUD.mc.fontRenderer.drawStringWithShadow(s, (float)(x + 19 - 2 - ArmorHUD.mc.fontRenderer.getStringWidth(s)), (float)(y + 9), 16777215);
            if (!this.damage.getValue()) {
                continue;
            }
            final float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            final int dmg = 100 - (int)(red * 100.0f);
            ArmorHUD.mc.fontRenderer.drawStringWithShadow(dmg + "", (float)(x + 8 - ArmorHUD.mc.fontRenderer.getStringWidth(dmg + "") / 2), (float)(y - 11), ColourHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    static {
        ArmorHUD.itemRender = Minecraft.getMinecraft().getRenderItem();
    }
}
