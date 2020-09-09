package me.zopac.freemanatee.gui.kami.theme.kami;

import java.awt.*;

/**
 * @author S-B99
 * Class for all the main GUI colours used by the default kami theme
 * mfw I make it easier for skids to customize kami
 */
public class KamiGuiColors {

    public enum GuiC {
        bgColour(new Color(204, 51, 255, 255)), // normal colored
        bgColourHover(new Color(157, 54, 191)), // light colored

        buttonPressed(new Color(211, 101, 247)),

        // N = normal T = toggled
        buttonIdleN(new Color(200, 200, 200)), // lighter grey
        buttonHoveredN(new Color(190, 190, 190)), // light grey

        buttonIdleT(new Color(213, 158, 232, 255)), // lighter colored
        buttonHoveredT((new Color(buttonIdleT.color.getRGB())).brighter()),

        windowOutline(new Color(191, 0, 255)),
        windowOutlineWidth(1.8f),

        pinnedWindow(new Color(211, 101, 247)),
        unpinnedWindow(168.3),
        lineWindow(112.2),

        sliderColour(new Color(227, 143, 255)),

        enumColour(new Color(211, 101, 247)),

        chatOutline(new Color(176, 38, 222)),

        scrollBar(new Color(211, 101, 247));

        public Color color;
        public float aFloat;
        public double aDouble;

        GuiC(Color color) {
            this.color = color;
        }

        GuiC(float aFloat) {
            this.aFloat = aFloat;
        }

        GuiC(double aDouble) {
            this.aDouble = aDouble;
        }
    }
}