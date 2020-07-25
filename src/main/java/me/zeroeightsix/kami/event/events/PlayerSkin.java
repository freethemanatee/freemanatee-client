package me.zeroeightsix.kami.event.events;

import me.zeroeightsix.kami.event.KamiEvent;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

public class PlayerSkin
        extends KamiEvent {
    private final NetworkPlayerInfo networkPlayerInfo;

    public PlayerSkin(NetworkPlayerInfo networkPlayerInfo) {
        this.networkPlayerInfo = networkPlayerInfo;
    }

    public NetworkPlayerInfo getNetworkPlayerInfo() {
        return this.networkPlayerInfo;
    }

    public static class HasSkin extends PlayerSkin
    {
        public boolean result;

        public HasSkin(final NetworkPlayerInfo networkPlayerInfo, final boolean result) {
            super(networkPlayerInfo);
            this.result = result;
        }
    }

    public static class GetSkin extends PlayerSkin
    {
        public ResourceLocation skinLocation;

        public GetSkin(final NetworkPlayerInfo networkPlayerInfo, final ResourceLocation skinLocation) {
            super(networkPlayerInfo);
            this.skinLocation = skinLocation;
        }
    }

}
