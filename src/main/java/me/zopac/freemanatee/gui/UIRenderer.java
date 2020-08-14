package me.zopac.freemanatee.gui;

import me.zopac.freemanatee.KamiMod;
import me.zopac.freemanatee.gui.kami.DisplayGuiScreen;
import me.zopac.freemanatee.gui.kami.KamiGUI;
import me.zopac.freemanatee.gui.rgui.component.Component;
import me.zopac.freemanatee.gui.rgui.component.container.use.Frame;
import me.zopac.freemanatee.gui.rgui.component.listen.RenderListener;
import me.zopac.freemanatee.util.Wrapper;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class UIRenderer {
	
	public static void renderAndUpdateFrames(){
	    if (Wrapper.getMinecraft().currentScreen instanceof DisplayGuiScreen || Wrapper.getMinecraft().gameSettings.showDebugInfo) return;
		KamiGUI gui = KamiMod.getInstance().getGuiManager();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
		for (Component c : gui.getChildren()){
			if (c instanceof Frame){
                GlStateManager.pushMatrix();
                Frame child = (Frame) c;
				if (child.isPinned() && child.isVisible()){
				    boolean slide = child.getOpacity() != 0;
                    GL11.glTranslated(child.getX(), child.getY(), 0);
                    child.getRenderListeners().forEach(renderListener -> renderListener.onPreRender());
                    child.getTheme().getUIForComponent(child).renderComponent(child, child.getTheme().getFontRenderer());

                    int translateX = 0;
                    int translateY = 0;

                    if (slide) {
                        translateX += child.getOriginOffsetX();
                        translateY += child.getOriginOffsetY();
                    }else{
                        if (child.getDocking().isBottom()){
                            translateY += child.getOriginOffsetY();
                        }
                        if (child.getDocking().isRight()){
                            translateX += child.getOriginOffsetX();
                            if (child.getChildren().size() > 0){
                                translateX += (child.getWidth() - child.getChildren().get(0).getX() - child.getChildren().get(0).getWidth()) / DisplayGuiScreen.getScale();
                            }
                        }
                        if (child.getDocking().isLeft() && child.getChildren().size() > 0){
                            translateX -= child.getChildren().get(0).getX();
                        }
                        if (child.getDocking().isTop() && child.getChildren().size() > 0){
                            translateY -= child.getChildren().get(0).getY();
                        }
                    }

                    GL11.glTranslated(translateX, translateY, 0);
                    child.getRenderListeners().forEach(RenderListener::onPostRender);
                    child.renderChildren();
                    GL11.glTranslated(-translateX, -translateY, 0);
                    GL11.glTranslated(-child.getX(), -child.getY(), 0);
				}
                GlStateManager.popMatrix();
            }
		}
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.enableBlend();
	}
	
}
