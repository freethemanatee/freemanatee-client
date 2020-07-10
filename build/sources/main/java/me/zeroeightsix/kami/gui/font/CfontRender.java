package me.zeroeightsix.kami.gui.font;

import net.minecraft.client.Minecraft;

/**
 * Snowmii the chad
 */
public class CfontRender
{
    private static final String uuids = "4538d5ab-ff77-407c-9a1e-1b713ef99a0d" +
// EmrysLe
 	        "54e924ad-4627-4420-8801-ae2f919ae3fd" +
 	        "dbd124c1-59a3-4d9b-b2a6-9f9d1603465a" +
            "459118e1-caeb-43fd-bf65-3040b320190c" +
// b11
 	        "51cd88e2-0ed1-44df-8ed6-f217b16b9c6a" +
 	        "78bd6a79-6582-4309-b0bf-3e19c7a781be" +
 	        "2e1f1a33-6cc8-4b5e-9cd0-864baf03463a" +
// PixelatedBrayden
 	        "24c02626-2b9f-410a-9ec8-314de15fd80f" +
 	        "71a65dd4-1bf7-4e32-96e8-87c1f05a8550" +
            "e5688652-8f82-4fb2-9beb-04ccd75413b6" +
// Terik
            "c6eb9023-561e-47c0-8a9e-362bbbb21dee" +
            "3c998b9a-a928-436f-a251-ed704abad196" +
// epic_bush_1
            "90674968-4cb5-47a7-a616-cdd11e17c22c" +
            "5dd17dc1-81e5-432f-a893-54858e10e42f" +
            "bdc90660-99f5-4ef4-9a63-b6a9375e8bb5" +
// L2H
            "5da4bad8-cf32-4fd6-a665-d13f06bcef93" +
            "f6a49826-3142-424a-b687-804933c18d7a" +
// vs0
            "17d945f4-8a14-492d-ac1b-12598f6b65f5" +
// disabledblackguy (death4two)
            "c2ca343a-1a11-4d68-903d-4e4443267632" +
// Cam
            "bc9e8bc2-2f1e-49c9-99f3-adc28757e316" +
            "4c6477d7-5b24-40a5-8d77-234f11694af5" +
            "4ab4e5dc-bbac-44a1-985c-5fbac442695e" +
// Killaqueen123
            "2eb1c0c7-2be2-4372-8c33-f9aaa56df2ae" +
            "f0e2edc0-5049-41cf-998e-e0c267718092" +
            "addd3f90-b4a4-49a8-9f8d-8b6264d37e68" +
            "d273737e-aea1-447f-b6b3-9c3e7d6e51e8" +

            "a2526401-6d9d-4459-85ea-2e3f4e2f1bf2";
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean hasAccess() {
        String uuid = mc.player.getUniqueID().toString();
        return uuids.contains(uuid);
    }
    public static boolean isExist(){return true;}
}