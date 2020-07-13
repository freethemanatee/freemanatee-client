
package me.zeroeightsix.kami.gui.kami.theme.kami;

import java.awt.Color;
import java.awt.Font;
import me.zeroeightsix.kami.gui.font.CFontRenderer;
import me.zeroeightsix.kami.gui.rgui.component.AlignedComponent;
import me.zeroeightsix.kami.gui.rgui.component.Component;
import me.zeroeightsix.kami.gui.rgui.component.use.Label;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;
import org.lwjgl.opengl.GL11;

public class RootLabelUI<T extends Label>
extends AbstractComponentUI<Label> {
    CFontRenderer cFontRenderer = new CFontRenderer(new Font("Arial", 0, 18), true, false);

    @Override
    public void renderComponent(Label component, FontRenderer a) {
        a = component.getFontRenderer();
        String[] lines = component.getLines();
        int y = 0;
        boolean shadow = component.isShadow();
        for (String s : lines) {
            int x = 0;
            if (component.getAlignment() == AlignedComponent.Alignment.CENTER) {
                x = component.getWidth() / 2 - a.getStringWidth(s) / 2;
            } else if (component.getAlignment() == AlignedComponent.Alignment.RIGHT) {
                x = component.getWidth() - a.getStringWidth(s);
            }
            if (shadow) {
                GL11.glDisable((int)2884);
                GL11.glEnable((int)3042);
                GL11.glEnable((int)3553);
                this.cFontRenderer.drawStringWithShadow(s, x, y, Color.white.getRGB());
                GL11.glEnable((int)2884);
                GL11.glDisable((int)3042);
                GL11.glDisable((int)3553);
            } else {
                GL11.glDisable((int)2884);
                GL11.glEnable((int)3042);
                GL11.glEnable((int)3553);
                this.cFontRenderer.drawString(s, x, y, Color.white.getRGB());
                GL11.glEnable((int)2884);
                GL11.glDisable((int)3042);
                GL11.glDisable((int)3553);
            }
            y += a.getFontHeight() + 3;
        }
        GL11.glDisable((int)3553);
        GL11.glDisable((int)3042);
    }

    @Override
    public void handleSizeComponent(Label component) {
        String[] lines = component.getLines();
        int y = 0;
        int w = 0;
        for (String s : lines) {
            w = Math.max(w, component.getFontRenderer().getStringWidth(s));
            y += component.getFontRenderer().getFontHeight() + 3;
        }
        component.setWidth(w);
        component.setHeight(y);
    }
}

