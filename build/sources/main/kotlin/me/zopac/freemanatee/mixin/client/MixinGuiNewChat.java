package me.zopac.freemanatee.mixin.client;

import me.zopac.freemanatee.module.ModuleManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {

   @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
   private void drawRectBackgroundClean(int left, int top, int right, int bottom, int color) {
      if (!ModuleManager.isModuleEnabled("CleanChat")) {
         Gui.drawRect(left, top, right, bottom, color);
      }
   }

}