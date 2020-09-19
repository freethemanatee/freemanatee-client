
package me.zopac.freemanatee.gui.kami.theme.kami;

import me.zopac.freemanatee.gui.kami.RenderHelper;
import me.zopac.freemanatee.gui.rgui.component.container.Container;
import me.zopac.freemanatee.gui.rgui.component.use.InputField;
import me.zopac.freemanatee.gui.rgui.render.AbstractComponentUI;
import me.zopac.freemanatee.gui.rgui.render.font.FontRenderer;
import org.lwjgl.opengl.GL11;

public class RootInputFieldUI<T extends InputField>
extends AbstractComponentUI<InputField> {
    @Override
    public void renderComponent(InputField component, FontRenderer fontRenderer) {
        GL11.glColor3f((float)0.22f, (float)0.22f, (float)0.33f);
        RenderHelper.drawFilledRectangle(0.0f, 0.0f, component.getWidth(), component.getHeight());
        GL11.glLineWidth((float)1.5f);
        GL11.glColor4f((float)0.043f, (float) 0.788f, (float)0.905f, (float)0.6f);
        RenderHelper.drawRectangle(0.0f, 0.0f, component.getWidth(), component.getHeight());
    }

    @Override
    public void handleAddComponent(InputField component, Container container) {
        component.setWidth(200);
        component.setHeight(component.getTheme().getFontRenderer().getFontHeight());
    }
}

