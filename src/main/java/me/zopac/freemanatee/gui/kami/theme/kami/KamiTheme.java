/*
 * Decompiled with CFR 0.145.
 */
package me.zopac.freemanatee.gui.kami.theme.kami;

import me.zopac.freemanatee.gui.kami.KamiGUI;
import me.zopac.freemanatee.gui.kami.theme.staticui.RadarUI;
import me.zopac.freemanatee.gui.kami.theme.staticui.TabGuiUI;
import me.zopac.freemanatee.gui.rgui.render.AbstractComponentUI;
import me.zopac.freemanatee.gui.rgui.render.font.FontRenderer;
import me.zopac.freemanatee.gui.rgui.render.theme.AbstractTheme;

public class KamiTheme
extends AbstractTheme {
    FontRenderer fontRenderer;

    public KamiTheme() {
        this.installUI(new RootButtonUI());
        this.installUI(new GUIUI());
        this.installUI(new RootGroupboxUI());
        this.installUI(new KamiFrameUI());
        this.installUI(new RootScrollpaneUI());
        this.installUI(new RootInputFieldUI());
        this.installUI(new RootLabelUI());
        this.installUI(new RootChatUI());
        this.installUI(new RootCheckButtonUI());
        this.installUI(new KamiActiveModulesUI());
        this.installUI(new KamiSettingsPanelUI());
        this.installUI(new RootSliderUI());
        this.installUI(new KamiEnumbuttonUI());
        this.installUI(new RootColorizedCheckButtonUI());
        this.installUI(new KamiUnboundSliderUI());
        this.installUI(new RadarUI());
        this.installUI(new TabGuiUI());
        this.fontRenderer = KamiGUI.fontRenderer;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public class GUIUI
    extends AbstractComponentUI<KamiGUI> {
    }

}

