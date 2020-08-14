package me.zopac.freemanatee.gui.rgui.render.theme;

import me.zopac.freemanatee.gui.rgui.component.Component;
import me.zopac.freemanatee.gui.rgui.render.ComponentUI;
import me.zopac.freemanatee.gui.rgui.render.font.FontRenderer;

/**
 * Created by 086 on 25/06/2017.
 */
public interface Theme {
    public ComponentUI getUIForComponent(Component component);
    public FontRenderer getFontRenderer();
}
