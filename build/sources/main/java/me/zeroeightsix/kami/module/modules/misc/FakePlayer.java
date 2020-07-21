package me.zeroeightsix.kami.module.modules.misc;

import com.mojang.authlib.GameProfile;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Module.Info(
        name = "FakePlayer",
        category = Module.Category.MISC
)
public class FakePlayer extends Module {

    private Setting<SpawnMode> spawnMode = register(Settings.e("Spawn Mode", SpawnMode.SINGLE));

    private List<Integer> fakePlayerIdList = null;

    private enum SpawnMode {
        SINGLE, MULTI
    }

    private static final String[][] fakePlayerInfo =
            {
                    {"4538d5ab-ff77-407c-9a1e-1b713ef99a0d", "zopac", "-3", "0"},
                    {"51cd88e2-0ed1-44df-8ed6-f217b16b9c6a", "b11", "0", "-3"},
                    {"8deac414-6c37-44fb-82bd-6873efc1b0cf", "_o_b_a_m_a_", "3", "0"},
                    {"17d945f4-8a14-492d-ac1b-12598f6b65f5", "vs0", "0", "3"},
                    {"2e1f1a33-6cc8-4b5e-9cd0-864baf03463a", "ybh", "-6", "0"},
                    {"442b386f-d6f4-49e2-a38f-b0e5e49346f7", "jysu", "0", "-6"},
                    {"78bd6a79-6582-4309-b0bf-3e19c7a781be", "freemanatee", "6", "0"},
                    {"0f75a81d-70e5-43c5-b892-f33c524284f2", "popbob", "0", "6"},
                    {"64a5c834-514b-4024-9aa4-515719f6e7fa", "iTristan", "-9", "0"},
                    {"5da4bad8-cf32-4fd6-a665-d13f06bcef93", "L2H", "0", "-9"},
                    {"71a65dd4-1bf7-4e32-96e8-87c1f05a8550", "popcornbob", "9", "0"},
                    {"9800cc02-eaf5-4c32-a13b-d277fc4d5f72", "simplesurvival", "0", "9"}
            };

    @Override
    protected void onEnable() {

        if (mc.player == null || mc.world == null) {
            this.disable();
            return;
        }

        fakePlayerIdList = new ArrayList<>();

        int entityId = -101;

        for (String[] data : fakePlayerInfo) {

            if (spawnMode.getValue().equals(SpawnMode.SINGLE)) {
                addFakePlayer(data[0], data[1], entityId, 0, 0);
                break;
            } else {
                addFakePlayer(data[0], data[1], entityId, Integer.parseInt(data[2]), Integer.parseInt(data[3]));
            }

            entityId--;

        }

    }

    private void addFakePlayer(String uuid, String name, int entityId, int offsetX, int offsetZ) {

        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString(uuid), name));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.posX = fakePlayer.posX + offsetX;
        fakePlayer.posZ = fakePlayer.posZ + offsetZ;
        mc.world.addEntityToWorld(entityId, fakePlayer);
        fakePlayerIdList.add(entityId);

    }

    @Override
    public void onUpdate() {

        if (fakePlayerIdList == null || fakePlayerIdList.isEmpty() ) {
            this.disable();
        }

    }

    @Override
    protected void onDisable() {

        if (mc.player == null || mc.world == null) {
            return;
        }

        if (fakePlayerIdList != null) {
            for (int id : fakePlayerIdList) {
                mc.world.removeEntityFromWorld(id);
            }
        }

    }

}
