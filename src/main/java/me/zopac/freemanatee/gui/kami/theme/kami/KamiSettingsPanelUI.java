package me.zopac.freemanatee.gui.kami.theme.kami;

import me.zopac.freemanatee.gui.kami.RenderHelper;
import me.zopac.freemanatee.gui.kami.component.SettingsPanel;
import me.zopac.freemanatee.gui.rgui.render.AbstractComponentUI;
import me.zopac.freemanatee.gui.rgui.render.font.FontRenderer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 16/12/2017.
 */
public class KamiSettingsPanelUI extends AbstractComponentUI<SettingsPanel> {

    @Override
    public void renderComponent(SettingsPanel component, FontRenderer fontRenderer) {
        super.renderComponent(component, fontRenderer);
//        glLineWidth(2);
//        glColor3f(.59f,.05f,.11f);
//        glBegin(GL_LINES);
//        {
//            glVertex2d(0,component.getHeight());
//            glVertex2d(component.getWidth(),component.getHeight());
//        }
//        glEnd();

        glLineWidth(2f);
        glColor4f(0,0,0,.6f);
        RenderHelper.drawFilledRectangle(0,0,component.getWidth(),component.getHeight());
        glColor3f(255,255,255);
        glLineWidth(1.5f);
        RenderHelper.drawRectangle(0,0,component.getWidth(),component.getHeight());
    }
}
