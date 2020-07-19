package me.zeroeightsix.kami.module.modules.render;


import com.mojang.realmsclient.gui.ChatFormatting;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

@Module.Info(
        name = "TabFriends",
        category = Module.Category.RENDER
)
public class TabFriends extends Module {
    public static TabFriends INSTANCE;

    public TabFriends() {
        INSTANCE = this;
    }

    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String dname = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        return Friends.isFriend(dname) ? ChatFormatting.AQUA.toString() + dname : dname;
    }
}
