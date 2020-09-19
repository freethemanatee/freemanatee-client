package me.zopac.freemanatee.gui.kami.theme.kami;

import me.zopac.freemanatee.gui.rgui.component.container.Container;
import me.zopac.freemanatee.gui.kami.KamiGUI;
import org.lwjgl.opengl.GL11;
import me.zopac.freemanatee.gui.rgui.render.font.FontRenderer;
import java.awt.Font;
import java.awt.Color;
import me.zopac.freemanatee.gui.font.CFontRenderer;
import me.zopac.freemanatee.gui.rgui.render.AbstractComponentUI;
import me.zopac.freemanatee.gui.rgui.component.use.CheckButton;

public class RootCheckButtonUI<T extends CheckButton> extends AbstractComponentUI<CheckButton>
{
    CFontRenderer cFontRenderer;
    protected Color backgroundColour;
    protected Color backgroundColourHover;
    protected Color idleColourNormal;
    protected Color downColourNormal;
    protected Color idleColourToggle;
    protected Color downColourToggle;

    public RootCheckButtonUI() {
        this.cFontRenderer = new CFontRenderer(new Font("Comfortaa", 0, 18), true, false);
        this.backgroundColour = new Color(0, 0, 255);;
        this.backgroundColourHover = new Color(168, 58, 201);
        this.idleColourNormal = new Color(37, 200, 2);
        this.downColourNormal = new Color(100, 100, 100);
        this.idleColourToggle = new Color(0, 0, 255);
        this.downColourToggle = this.idleColourToggle.brighter();
    }

    @Override
    public void renderComponent(final CheckButton component, final FontRenderer ff) {
        if (component.isToggled()) {}
        if (component.isHovered() || component.isPressed()) {}
        final String text = component.getName();
        int c = component.isPressed() ? 0xffffff : component.isToggled() ? 0x0bc9e7 : 0xffffff;
        if (component.isHovered()) {
            c = (c & 0x7F7F7F) << 1;
        }
        GL11.glEnable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        KamiGUI.cFontRenderer.drawString(text, (float)(component.getWidth() / 2 - KamiGUI.fontRenderer.getStringWidth(text) / 2), (float)(KamiGUI.fontRenderer.getFontHeight() / 2 - 2), c);
        GL11.glDisable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
    }

    @Override
    public void handleAddComponent(final CheckButton component, final Container container) {
        component.setWidth(KamiGUI.fontRenderer.getStringWidth(component.getName()) + 28);
        component.setHeight(KamiGUI.fontRenderer.getFontHeight() + 2);
    }
}