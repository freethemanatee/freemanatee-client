package me.zopac.freemanatee.module.modules.combat;

import me.zopac.freemanatee.command.Command;
import net.minecraft.init.MobEffects;

import java.util.Collections;
import java.util.WeakHashMap;
import me.zopac.freemanatee.setting.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Set;
import me.zopac.freemanatee.setting.Setting;
import me.zopac.freemanatee.module.Module;

@Module.Info(
        name = "StrengthDetect",
        category = Module.Category.COMBAT
)
public class StrengthDetect extends Module {
    private Setting<Boolean> watermark;
    private Setting<Boolean> color;
    private Set<EntityPlayer> str;
    public static final Minecraft mc;
    public StrengthDetect() {
        this.watermark = this.register(Settings.b("Watermark", true));
        this.color = this.register(Settings.b("Color", false));
        this.str = Collections.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());
    }
    @Override
    public void onUpdate() {
        for (final EntityPlayer player : StrengthDetect.mc.world.playerEntities) {
            if (player.equals((Object)StrengthDetect.mc.player)) {
                continue;
            }
            if (player.isPotionActive(MobEffects.STRENGTH) && !this.str.contains(player)) {
                if (this.watermark.getValue()) {
                    if (this.color.getValue()) {
                        Command.sendChatMessage("&a " + player.getDisplayNameString() + "has strength");
                    }
                    else {
                        Command.sendChatMessage(player.getDisplayNameString() + " has strength");
                    }
                }
                else if (this.color.getValue()) {
                    Command.sendRawChatMessage("&a " + player.getDisplayNameString() + " has strength");
                }
                else {
                    Command.sendRawChatMessage(player.getDisplayNameString() + " has strength");
                }
                this.str.add(player);
            }
            if (!this.str.contains(player)) {
                continue;
            }
            if (player.isPotionActive(MobEffects.STRENGTH)) {
                continue;
            }
            if (this.watermark.getValue()) {
                if (this.color.getValue()) {
                    Command.sendChatMessage("&c " + player.getDisplayNameString() + " doesnt have strength anymore");
                }
                else {
                    Command.sendChatMessage(player.getDisplayNameString() + " doesnt have strength anymore");
                }
            }
            else if (this.color.getValue()) {
                Command.sendRawChatMessage("&c " + player.getDisplayNameString() + " doesnt have strength anymore");
            }
            else {
                Command.sendRawChatMessage(player.getDisplayNameString() + " doesnt have strength anymore");
            }
            this.str.remove(player);
        }
    }
    static {
        mc = Minecraft.getMinecraft();
    }
}
