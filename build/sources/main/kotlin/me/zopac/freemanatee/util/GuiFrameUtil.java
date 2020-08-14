package me.zopac.freemanatee.util;

import me.zopac.freemanatee.KamiMod;
import me.zopac.freemanatee.gui.kami.KamiGUI;
import me.zopac.freemanatee.gui.rgui.component.container.use.Frame;
import me.zopac.freemanatee.gui.rgui.util.ContainerHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.util.List;

/**
 * @author S-B99
 * Created by S-B99 on 24/03/20
 */
public class GuiFrameUtil {
    // This is bad, but without a rearchitecture, it's probably staying... - 20kdc and S-B99
    public static Frame getFrameByName(String name) {
        KamiGUI kamiGUI = KamiMod.getInstance().getGuiManager();
        if (kamiGUI == null)
            return null;
        List<Frame> frames = ContainerHelper.getAllChildren(Frame.class, kamiGUI);
        for (Frame frame : frames)
            if (frame.getTitle().equalsIgnoreCase(name))
                return frame;
        return null;
    }

    /* Additional method to prevent calling kamiGui if you already have an instance */
    public static Frame getFrameByName(KamiGUI kamiGUI, String name) {
        if (kamiGUI == null)
            return null;
        List<Frame> frames = ContainerHelper.getAllChildren(Frame.class, kamiGUI);
        for (Frame frame : frames)
            if (frame.getTitle().equalsIgnoreCase(name))
                return frame;
        return null;
    }

    public static void fixFrames(Minecraft mc) {
        KamiGUI kamiGUI = KamiMod.getInstance().getGuiManager();
        if (kamiGUI == null || mc.player == null) return;
        List<Frame> frames = ContainerHelper.getAllChildren(Frame.class, kamiGUI);
        for (Frame frame : frames) {
            int divider = mc.gameSettings.guiScale;
            if (divider == 0) divider = 3;
            if (frame.getX() > (Display.getWidth() / divider)) {
                frame.setX((Display.getWidth() / divider) - frame.getWidth());
            }
            if (frame.getY() > (Display.getHeight() / divider)) {
                frame.setY((Display.getHeight() / divider) - frame.getHeight());
            }
            if (frame.getX() < 0) frame.setX(0);
            if (frame.getY() < 0) frame.setY(0);
        }
    }
}