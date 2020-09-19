package me.zopac.freemanatee.event.events;

import com.mojang.authlib.GameProfile;
import me.zopac.freemanatee.event.KamiEvent;
import me.zopac.freemanatee.util.PlayerInfo;

import java.util.Objects;

public class EntityConnectEvent extends KamiEvent {

    private final PlayerInfo playerInfo;
    private final GameProfile profile;

    public EntityConnectEvent(PlayerInfo playerInfo, GameProfile profile) {
        Objects.requireNonNull(profile);
        this.playerInfo = playerInfo;
        this.profile = profile;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public GameProfile getProfile() {
        return profile;
    }

    public static class Join extends EntityConnectEvent {

        public Join(PlayerInfo playerInfo, GameProfile profile) {
            super(playerInfo, profile);
        }
    }

    public static class Leave extends EntityConnectEvent {

        public Leave(PlayerInfo playerInfo, GameProfile profile) {
            super(playerInfo, profile);
        }
    }



}

