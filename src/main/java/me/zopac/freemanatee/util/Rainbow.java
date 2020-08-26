package me.zopac.freemanatee.util;

import java.awt.Color;
import me.zopac.freemanatee.event.ForgeEventProcessor;

public class Rainbow
{
    public static int getInt() {
        return ForgeEventProcessor.INSTANCE.getRgb();
    }

    public static Color getColor() {
        return ForgeEventProcessor.INSTANCE.getC();
    }

    public static Color getColorWithOpacity(final int opacity) {
        return new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), opacity);
    }

    public static int getIntWithOpacity(final int opacity) {
        return getColorWithOpacity(opacity).getRGB();
    }

    public static int getRainbow(final int speed, final int offset, final float s) {
        float hue = (float)((System.currentTimeMillis() + offset) % speed);
        hue /= speed;
        return Color.getHSBColor(hue, s, 1.0f).getRGB();
    }
}