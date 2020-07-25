package me.zeroeightsix.kami.module.modules.meme;

import me.zeroeightsix.kami.event.events.PlayerSkin;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "Manatee ESP", category = Module.Category.MEME)
public class manateeesp extends Module {

    private ResourceLocation location;

    public manateeesp() {
        this.location = new ResourceLocation("textures/manatee.png");

    }

    @SubscribeEvent
    public void hasSkin(final PlayerSkin.HasSkin event) {
        if (this.isEnabled()) {
            event.result = true;
        }
    }

    @SubscribeEvent
    public void getSkin(final PlayerSkin.GetSkin event) {
        if (this.isEnabled()) {
            event.skinLocation = this.location;
        }
    }

    @Override
    public void onUpdate() {
        if (this.isDisabled()) {
            return;
        }
    }

}