package me.zopac.freemanatee.mixin.client;

import me.zopac.freemanatee.module.modules.render.TabFriends;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable returnable) {
        if (TabFriends.INSTANCE.isEnabled()) {
            returnable.cancel();
            returnable.setReturnValue(TabFriends.getPlayerName(networkPlayerInfoIn));
        }
    }

}
